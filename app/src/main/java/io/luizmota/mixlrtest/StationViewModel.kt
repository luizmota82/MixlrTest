package io.luizmota.mixlrtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StationViewModel(private val stationRepository: StationRepository) : ViewModel() {

    private val _viewState: MutableLiveData<StationViewState> =
        MutableLiveData<StationViewState>().apply {
            postValue(StationViewState.Initial())
        }

    val viewState: LiveData<StationViewState> = _viewState

    fun fetchStationData(stationId: Int) {
        _viewState.postValue(StationViewState.Loading)
        stationRepository.fetchData(
            stationId = stationId,
            onCompleted = {
                _viewState.postValue(StationViewState.Idle(details = it))
            },
            onError = {
                _viewState.postValue(StationViewState.Error(it.localizedMessage))
            }
        )
    }

    fun refreshData() {
        _viewState.postValue(StationViewState.Initial())
    }
}

sealed class StationViewState {
    data class Initial(val stationId: Int = 23) : StationViewState()
    object Loading : StationViewState()
    data class Error(val message: String?) : StationViewState()
    data class Idle(val details: StationDetails) : StationViewState()
}
