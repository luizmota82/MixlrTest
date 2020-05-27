package io.luizmota.mixlrtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_station.*

class StationActivity : AppCompatActivity(R.layout.activity_station) {

    private val viewModel: StationViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return StationViewModel(stationRepository = StationRepository()) as T
            }

        })[StationViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.viewState.observe(this) {
            handleState(it)
        }

        with(shows_list) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ShowsAdapter()
        }

        refresh_layout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun handleState(state: StationViewState) {
        when (state) {
            is StationViewState.Initial -> {
                viewModel.fetchStationData(stationId = state.stationId)
            }
            is Error -> showError()
            is StationViewState.Loading -> showLoadingIndicator()
            is StationViewState.Idle -> displayData(details = state.details)
        }
    }

    private fun displayData(details: StationDetails) {
        station_name.text = details.name
        (shows_list.adapter as? ShowsAdapter)?.display(schedule = details.schedule)
    }

    private fun showError() {

    }

    private fun showLoadingIndicator() {
        refresh_layout.isRefreshing = true
    }
}
