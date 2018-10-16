package se.studieresan.studs.events.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.spotify.mobius.Connection
import com.spotify.mobius.First
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.functions.Consumer
import se.studieresan.studs.R
import se.studieresan.studs.StudsApplication
import se.studieresan.studs.events.detail.loop.*
import se.studieresan.studs.loopFrom
import se.studieresan.studs.models.StudsUser


class MapsActivity : AppCompatActivity(), EventDetailAdapter.OnItemPressedListener {

    var googleMap: GoogleMap? = null
    var controller: MobiusLoop.Controller<EventDetailModel, Event>? = null
    var dispatch: Consumer<Event>? = null
    val eventId by lazy { intent.getStringExtra(EventDetailFragment.EVENT_ID_KEY) }
    var users: List<StudsUser> = emptyList()
    var addressCache: String? = null
    val stateKey = "state"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("checkins")

        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        val app = (application as StudsApplication)
        val effectHandler = EffectHandler(app.eventSource, app.userSource, collection, applicationContext)

        val loop = loopFrom(::update, effectHandler)
                .logger(AndroidLogger.tag("MapActivity"))
                .eventSource(MergedEventSource(
                        CheckinSource(eventId, collection),
                        MapSource(mapFragment, styleOptions)
                ))
                .init { First.first(EventDetailModel(), setOf(LoadEvent(eventId), LoadUsers)) }

        controller = MobiusAndroid.controller(loop, EventDetailModel())
        controller?.connect(::connectViews)

        createDetailFragment(eventId)
    }

    override fun onStart() {
        super.onStart()
        controller?.start()
    }

    override fun onResume() {
        super.onResume()
        controller?.model?.let {
            render(it)
        }
    }

    override fun onStop() {
        super.onStop()
        controller?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.disconnect()
    }

    private fun connectViews(consumer: Consumer<Event>): Connection<EventDetailModel> {
        dispatch = consumer
        return object: Connection<EventDetailModel> {
            override fun accept(model: EventDetailModel) = render(model)

            override fun dispose() {}
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        controller?.model?.let { model ->
            val cleanedModel = model.copy(map = null)
            outState?.putSerializable(stateKey, cleanedModel)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val state = savedInstanceState?.getSerializable(stateKey)
        if (state is EventDetailModel) {
            render(state)
        }
    }

    var cachedPosition: Position? = null
    private fun render(model: EventDetailModel) {
        if (addressCache == null && model.event?.location != null && model.map != null) {
            moveToPosition(model.map, model.event.location)
            addressCache = model.event.location
        }
        users = model.users

        val detailFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_holder) as EventDetailFragment
        detailFragment.render(model)

        if (model.location != cachedPosition) {
            cachedPosition = model.location
            val position = LatLng(model.location.latitude, model.location.longitude)
            if (model.location.latitude == 59.3293 && model.location.longitude == 18.0686) {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f))
            } else {
                val offset = 0.01f
                val southWest = LatLng(position.latitude + offset, position.longitude + offset)
                val northEast = LatLng(position.latitude - offset, position.longitude - offset)
                val bounds = LatLngBounds(northEast, southWest)
                googleMap?.addMarker(MarkerOptions().position(position).title("Event Location"))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
            }
        }
    }

    private fun moveToPosition(googleMap: GoogleMap?, position: String?) {
        if (position == null) return
        if (googleMap == null) return
        this.googleMap = googleMap

        dispatch?.accept(TriggerGetLocation(position))
    }

    private fun createDetailFragment(eventId: String) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_holder, EventDetailFragment.newInstance(eventId = eventId))
                .commit()
    }

    override fun checkInUser(user: StudsUser) {
        // TODO move to loop
        val loggedInUser = (application as StudsApplication).userSource.getLoggedInUser(users)
        loggedInUser?.id?.let { id ->
            dispatch?.accept(TriggerCheckIn(user, id, eventId))
        }
    }

    override fun checkOutUser(userId: String, checkIn: se.studieresan.studs.models.CheckIn) {
        dispatch?.accept(TriggerCheckOut(userId, checkIn.id))
    }

    override fun callUser(telephoneNumer: String) {
        // TODO Move to loop
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$telephoneNumer")
        if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            startActivity(callIntent)
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    0
            )
        }
    }

    fun toggleUserListType() {
        dispatch?.accept(ToggleUserListType)
    }

}
