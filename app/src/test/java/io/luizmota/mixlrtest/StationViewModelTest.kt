package io.luizmota.mixlrtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.observe
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.rules.TestRule
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection

private const val A_STATION_ID = 22

class StationViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private var viewModel: StationViewModel? = null
    private val mockWebServer: MockWebServer = MockWebServer()
    private val lifecycleOwner =
        LifecycleOwner { LifecycleRegistry(mock()).also { it.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) } }

    @Before
    fun setup() {
        mockWebServer.start()
        val repository =
            StationRepository(mockWebServer.url("/").toString())

        viewModel = StationViewModel(repository)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `GIVEN view state WHEN observed THEN state is initial`() {
        Assert.assertEquals(StationViewState.Initial(), viewModel!!.viewState.value)
    }

    @Test
    fun `GIVEN fetching station data WHEN data returned THEN state is Idle`() {
        val body = "station_details.json".toJsonStringFromResource()
        val data = Gson().fromJson(body, StationResult::class.java)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(body)
        )

        viewModel!!.fetchStationData(A_STATION_ID)

        viewModel?.whenStateAvailable<StationViewState.Idle> {
            Assert.assertEquals(StationViewState.Idle(data.toStationDetails()), this)
        }
    }

    @Test
    fun `GIVEN fetching station data THEN state is Loading`() {
        viewModel!!.fetchStationData(A_STATION_ID)

        viewModel?.whenStateAvailable<StationViewState.Loading> {
            Assert.assertEquals(StationViewState.Loading, this)
        }
    }

    @Test
    fun `GIVEN fetching station data WHEN error raised THEN state is Error`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        )

        viewModel!!.fetchStationData(A_STATION_ID)

        viewModel?.whenStateAvailable<StationViewState.Error> {
            Assert.assertEquals(StationViewState.Error("Error"), this)
        }
    }

    private inline fun <reified T : StationViewState> StationViewModel.whenStateAvailable(
        crossinline thenAssertOnState: T.() -> Unit
    ) {
        viewModel!!.viewState.observe(lifecycleOwner, onChanged = {
            when (it) {
                is T -> thenAssertOnState(it)
            }
        })
    }
}

fun String.toJsonStringFromResource(): String {
    val inputStream = ClassLoader.getSystemResourceAsStream(this)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()
    for (l in reader.readLine()) {
        stringBuilder.append(l)
    }
    return stringBuilder.toString()
}
