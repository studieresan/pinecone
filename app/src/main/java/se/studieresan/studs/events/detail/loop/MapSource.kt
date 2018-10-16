package se.studieresan.studs.events.detail.loop

import android.content.res.Resources
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.spotify.mobius.EventSource
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer

class MapSource(
        val mapFragment: SupportMapFragment,
        val styleOptions: MapStyleOptions
): EventSource<Event>, OnMapReadyCallback {

    var eventConsumer: Consumer<Event>? = null

    override fun subscribe(eventConsumer: Consumer<Event>?): Disposable {
        this.eventConsumer = eventConsumer
        mapFragment.getMapAsync(this)
        return Disposable {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(styleOptions)
            if (!success) Log.e("MAP", "Style parsing failed.")
        } catch (e: Resources.NotFoundException) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
        eventConsumer?.accept(MapLoaded(googleMap))
    }

}
