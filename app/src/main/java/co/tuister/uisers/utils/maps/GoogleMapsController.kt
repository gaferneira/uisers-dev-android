package co.tuister.uisers.utils.maps

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapsController : MapController {

    private var map: GoogleMap? = null

    private val markersList = mutableMapOf<String, Marker>()

    override fun setup(childFragmentManager: FragmentManager, lifecycle: Lifecycle, fragmentContainerId: Int, onMapReady: (() -> Unit)) {
        val mMapFragment = SupportMapFragment.newInstance()
        mMapFragment.getMapAsync { googleMap ->
            map = googleMap
            onMapReady()
        }
        childFragmentManager.beginTransaction().add(fragmentContainerId, mMapFragment).commit()
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                lifecycle.removeObserver(this)
                map = null
            }
        })
    }

    override fun setCenter(latLng: Pair<Double, Double>, zoom: Float?) {
        val position = LatLng(latLng.first, latLng.second)
        map?.moveCamera(CameraUpdateFactory.newLatLng(position))
        zoom?.run {
            setZoom(this)
        }
    }

    override fun setZoom(value: Float) {
        map?.moveCamera(CameraUpdateFactory.zoomTo(value))
    }

    override fun setMarker(
        latLng: Pair<Double, Double>,
        tag: String?,
        title: String?,
        icon: Drawable?
    ) {
        val position = LatLng(latLng.first, latLng.second)

        var marker = tag?.let { markersList[it] }

        if (marker != null) {
            marker.title = title
            marker.position = position
        } else {
            val marketOptions = MarkerOptions().position(position).title(title)
            marker = map?.addMarker(marketOptions)
            tag?.let { markersList[it] = marker!! }
        }
    }

    override fun zoomIn() {
        // TODO
    }

    override fun zoomOut() {
        // TODO
    }

    override fun addGeoJsonLayer(tag: String, geoJsonString: String) {
        // TODO
    }

    @SuppressLint("MissingPermission")
    override fun setMyLocationButton(show: Boolean) {
        map?.isMyLocationEnabled = show
    }
}
