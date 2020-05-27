package io.luizmota.mixlrtest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_station.*

class StationActivity : AppCompatActivity(R.layout.activity_station) {

    private var viewModel: StationViewModel? = null
        get() {
            if (field == null) {
                field = createViewModel()
            }
            return field
        }

    private fun createViewModel(): StationViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return StationViewModel(stationRepository = StationRepository()) as T
            }

        })[StationViewModel::class.java]
    }

    private val glideRequestManager: RequestManager by lazy {
        Glide.with(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel?.viewState?.observe(this) {
            it.process()
        }

        with(shows_list) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ShowsAdapter(requestManager = glideRequestManager)
        }

        refresh_layout.setOnRefreshListener {
            (shows_list.adapter as? ShowsAdapter)?.display(schedule = emptyList())
            station_name.text = ""
            station_shows.visibility = View.INVISIBLE
            viewModel?.refreshData()
        }
    }

    private fun StationViewState.process() {
        when (this) {
            is StationViewState.Initial -> {
                viewModel?.fetchStationData(stationId = stationId)
                group_details.visibility = View.GONE
            }
            is Error -> showError(message)
            is StationViewState.Loading -> showLoadingIndicator()
            is StationViewState.Idle -> displayData(details = details)
        }
    }

    private fun displayData(details: StationDetails) {
        station_name.text = details.name
        station_shows.visibility = View.VISIBLE
        (shows_list.adapter as? ShowsAdapter)?.display(schedule = details.schedule)
        refresh_layout.isRefreshing = false
        error_message.visibility = View.GONE
        group_details.visibility = View.VISIBLE
    }

    private fun showError(errorMessage: String? = null) {
        group_details.visibility = View.GONE
        error_message.text = errorMessage?.let {
            errorMessage
        } ?: getString(R.string.details_error_generic)
        error_message.visibility = View.VISIBLE
    }

    private fun showLoadingIndicator() {
        refresh_layout.isRefreshing = true
    }
}
