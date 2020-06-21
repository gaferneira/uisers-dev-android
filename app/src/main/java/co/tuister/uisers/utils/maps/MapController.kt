package co.tuister.uisers.utils.maps

import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager

interface MapController {
    fun setup(childFragmentManager: FragmentManager, fragmentContainerId: Int, onMapReady: () -> Unit)
    fun setCenter(latLng: Pair<Double, Double>, zoom: Float? = null)
    fun setZoom(value: Float)
    fun setMarker(latLng: Pair<Double, Double>, tag: String? = null, title: String? = null, icon: Drawable? = null)
    fun zoomIn()
    fun zoomOut()
    fun addGeoJsonLayer(tag: String, geoJsonString: String)
    fun setMyLocationButton(show: Boolean)
}
