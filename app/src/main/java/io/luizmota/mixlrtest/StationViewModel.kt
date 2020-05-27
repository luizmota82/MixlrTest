package io.luizmota.mixlrtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class StationViewModel(private val stationRepository: StationRepository) : ViewModel() {

    private val _viewState: MutableLiveData<StationViewState> =
        MutableLiveData<StationViewState>().apply {
            postValue(StationViewState.Initial())
        }

    val viewState: LiveData<StationViewState> = _viewState

    fun fetchStationData(stationId: Int) {
        stationRepository.fetchData(stationId, onCompleted = {
            _viewState.postValue(StationViewState.Idle(details = it))
        }, onError = {
            // handle error
        })
    }

    override fun onCleared() {
        // cancel any ongoing calls and kill repo
        super.onCleared()
    }

    fun refreshData() {
        _viewState.postValue(StationViewState.Initial())
    }
}

internal sealed class StationViewState {
    data class Initial(val stationId: Int = 22): StationViewState()
    object Empty : StationViewState()
    object Loading : StationViewState()
    object Error : StationViewState()
    data class Idle(val details: StationDetails) : StationViewState()
}
