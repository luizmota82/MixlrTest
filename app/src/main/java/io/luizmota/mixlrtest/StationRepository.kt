package io.luizmota.mixlrtest

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

class StationRepository(baseUrl: String? = null) {
    private val networkService: StationService by lazy {
        val url = baseUrl ?: "https://mixlr-codetest.herokuapp.com/"
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StationService::class.java)
    }

    fun fetchData(
        stationId: Int,
        onCompleted: (StationDetails) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        networkService.get(stationId = stationId)
            .enqueue(object : Callback<StationResult> {
                override fun onFailure(call: Call<StationResult>, t: Throwable) {
                    onError(t)
                }

                override fun onResponse(
                    call: Call<StationResult>,
                    response: Response<StationResult>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onCompleted(it.toStationDetails())
                        }
                    } else {
                        error(response.message())
                    }
                }
            })
    }
}

fun StationResult.toStationDetails() = StationDetails(
    id = data.id,
    name = data.attributes.name,
    schedule = data.attributes.schedule.toShows()
)

private fun Schedule.toShows(): List<Show> = this.data.map {
    Show(
        id = it.id,
        title = it.attributes.title,
        host = it.attributes.host,
        imageUrl = it.attributes.image_url,
        startMediaUrl = it.links.start_url,
        endMediaUrl = it.links.stop_url,
        startTime = it.attributes.starts_at,
        endTime = it.attributes.ends_at,
        isLive = isLive(it, Date()),
        isOnAir = it.attributes.status == ShowStatus.OnAir
    )
}.sortedBy { it.startTime }

private fun isLive(dataX: DataX, now: Date): Boolean =
    dataX.attributes.starts_at.before(now) && dataX.attributes.ends_at.after(now)

enum class ShowStatus(name: String) {
    @SerializedName("on_air")
    OnAir("on_air"),

    @SerializedName("off_air")
    OffAir("off_air")
}

data class StationDetails(
    val id: String,
    val name: String,
    val schedule: List<Show>
)

data class Show(
    val id: String,
    val title: String,
    val host: String,
    val imageUrl: String,
    val startMediaUrl: String,
    val endMediaUrl: String,
    val startTime: Date,
    val endTime: Date,
    val isLive: Boolean,
    val isOnAir: Boolean
)

interface StationService {
    @GET("stations/{stationId}")
    fun get(@Path("stationId") stationId: Int): Call<StationResult>
}
