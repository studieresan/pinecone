package se.studieresan.studs.events.master.detail

import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_maps.*
import se.studieresan.studs.R
import se.studieresan.studs.StudsApplication
import se.studieresan.studs.models.StudsEvent
import se.studieresan.studs.ui.DraggableCoordinatorLayout.MapFocusListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapFocusListener {

    private var googleMap: GoogleMap? = null
    private var event: StudsEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val eventId = intent.getStringExtra(EventDetailFragment.EVENT_ID_KEY)
        supportFragmentManager
                .beginTransaction()
                .add(
                        R.id.fragment_holder,
                        EventDetailFragment.newInstance(eventId = eventId)
                )
                .commit()

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val eventSource = (application as StudsApplication).eventSource
        eventSource.getEventById(eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onEvent)

        draggableCoordinatorLayout.focusListener = this
    }

    private fun onEvent(event: StudsEvent) {
        this.event = event
        moveToPosition(googleMap, event.location)
    }

    private fun moveToPosition(googleMap: GoogleMap?, position: String?) {
        if (position == null) return
        if (googleMap == null) return

        locationOfEvent(address = position)?.let { location ->
            val offset = 0.01f
            val position = LatLng(location.latitude, location.longitude)
            val southWest = LatLng(location.latitude + offset, location.longitude + offset)
            val northEast = LatLng(location.latitude - offset, location.longitude - offset)
            val bounds = LatLngBounds(northEast, southWest)
            googleMap.addMarker(MarkerOptions().position(position).title("Event Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
        }
    }

    fun locationOfEvent(address: String): Address? {
        val geocoder = Geocoder(applicationContext)
        val location = geocoder
                .getFromLocationName("Stockholm, $address", 1)
                .firstOrNull()
        return location
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        moveToPosition(googleMap, event?.location)
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json))

            if (!success) {
                Log.e("MAP", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
    }

    override fun onMapFocus() {
        val googleMap = googleMap ?: return
        val event = event ?: return
        val location = event.location ?: return
        val address = locationOfEvent(location) ?: return

        val latLng = LatLng(address.latitude, address.longitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f))
    }

}
