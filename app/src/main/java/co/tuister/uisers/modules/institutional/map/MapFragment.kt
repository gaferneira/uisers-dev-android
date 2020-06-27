package co.tuister.uisers.modules.institutional.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.databinding.FragmentInstitutionalMapBinding
import co.tuister.uisers.modules.institutional.map.MapViewModel.State
import co.tuister.uisers.utils.extensions.AppPermission
import co.tuister.uisers.utils.extensions.handlePermission
import co.tuister.uisers.utils.extensions.handlePermissionsResult
import co.tuister.uisers.utils.extensions.requestPermission
import co.tuister.uisers.utils.maps.MapController
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel

class MapFragment : BaseFragment<FragmentInstitutionalMapBinding>() {

    private lateinit var viewModel: MapViewModel

    private val mapController: MapController by inject()
    private var sites: List<Site>? = null
    private var places: List<Place>? = null
    private var filterPlaces: List<Place>? = null
    private var selectedSite: Site? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstitutionalMapBinding.inflate(inflater)
        initViews()
        initViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapController.setup(childFragmentManager, lifecycle, R.id.fragment_container_view_map) {
            initMap()
        }
    }

    private fun initMap() {
        val uis = Pair(DEFAULT_LAT, DEFAULT_LON)
        with(mapController) {
            setCenter(uis, DEFAULT_ZOOM)
        }
        checkLocationPermission()
    }

    private fun initViews() {
        binding.editTextSite.setOnClickListener { showSitesDialog() }
        binding.editTextPlace.setOnClickListener { showPlacesDialog() }
    }

    private fun initViewModel() {
        viewModel = getViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                update(it)
            }
        }
        viewModel.initialize()
    }

    private fun update(status: BaseState<Any>?) {
        when (status) {
            is State.LoadPlaces -> loadPlaces(status)
            is State.LoadSites -> loadSites(status)
        }
    }

    private fun loadSites(state: State.LoadSites) {
        handleState(state) {
            state.data?.run {
                sites = this
            }
        }
    }

    private fun loadPlaces(state: MapViewModel.State.LoadPlaces) {
        handleState(state) {
            it?.run {
                places = this
            }
        }
    }

    private fun showSitesDialog() {
        val options = sites?.map { it.name } ?: return
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.institutional_hint_map_sites)
            .setItems(options.toTypedArray()) { _, which ->
                binding.editTextSite.setText(options[which])
                val site = sites?.get(which) ?: return@setItems
                if (site != selectedSite) {
                    binding.editTextPlace.text = null
                    selectedSite = site
                    with(mapController) {
                        setCenter(site.latlng, DEFAULT_ZOOM)
                    }
                }
            }
            .create()

        dialog.show()
    }

    private fun showPlacesDialog() {
        filterPlaces = places?.filter {
            selectedSite?.id == null || selectedSite?.id == it.siteId
        }

        val options = filterPlaces?.map { it.title } ?: return
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.institutional_hint_map_sites)
            .setItems(options.toTypedArray()) { _, which ->
                binding.editTextPlace.setText(options[which])
                val place = filterPlaces?.get(which) ?: return@setItems
                with(mapController) {
                    mapController.setMarker(place.latlng, TAG_MARKER_PLACE, place.title)
                    setCenter(place.latlng, DEFAULT_ZOOM_SELECTED)
                }
            }
            .create()

        dialog.show()
    }

    private fun checkLocationPermission() {
        handlePermission(
            AppPermission.AccessFineLocation,
            onGranted = {
                mapController.setMyLocationButton(true)
            },
            onDenied = {
                requestPermission(AppPermission.AccessFineLocation)
            },
            onRationaleNeeded = {
                requestPermission(AppPermission.AccessFineLocation)
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionsResult(
            requestCode, permissions, grantResults,
            onPermissionGranted = {
                mapController.setMyLocationButton(true)
            }
        )
    }

    companion object {
        private const val TAG_MARKER_PLACE = "TAG_MARKER_PLACE"
        private const val DEFAULT_LAT = 7.139014
        private const val DEFAULT_LON = -73.120622
        private const val DEFAULT_ZOOM = 16f
        private const val DEFAULT_ZOOM_SELECTED = 18f
    }
}
