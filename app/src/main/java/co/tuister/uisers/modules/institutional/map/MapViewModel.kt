package co.tuister.uisers.modules.institutional.map

import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site
import co.tuister.domain.usecases.institutional.GetPlacesUseCase
import co.tuister.domain.usecases.institutional.GetSitesUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
    private val getSites: GetSitesUseCase,
    private val getPlaces: GetPlacesUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class LoadSites(result: Result<List<Site>>) : State<List<Site>>(result)
        class LoadPlaces(result: Result<List<Place>>) : State<List<Place>>(result)
    }

    fun initialize() {
        updateSites()
        updatePlaces()
    }

    private fun updateSites() {
        viewModelScope.launch {
            setState(State.LoadSites(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getSites() }
            result.fold(
                {
                    setState(State.LoadSites(Result.Error(it)))
                },
                {
                    setState(State.LoadSites(Result.Success(it)))
                }
            )
        }
    }

    private fun updatePlaces() {
        viewModelScope.launch {
            setState(State.LoadPlaces(Result.InProgress()))
            val result = withContext(Dispatchers.IO) { getPlaces() }
            result.fold(
                {
                    setState(State.LoadPlaces(Result.Error(it)))
                },
                {
                    setState(State.LoadPlaces(Result.Success(it)))
                }
            )
        }
    }
}
