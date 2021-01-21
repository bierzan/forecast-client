package io.bierzan.fivedayforecast

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bierzan.fivedayforecast.forecast.dto.AccuweatherCallsSummary
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.TestPropertySources
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static AcceptanceTestUtil.readAccuLocationResponse
import static AcceptanceTestUtil.readFromAccuResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySources([
        @TestPropertySource("classpath:forecast.test.properties"),
        @TestPropertySource("classpath:accuweather.test.properties")
])
@ContextConfiguration(initializers = [WireMockInitializer.class])
class AcceptanceTest extends Specification {
    static String LOCATION_00_000_RESPONSE = new String(Files.readAllBytes(Paths.get("src/test/resources/location00-000.json")))
    static String LOCATION_01_000_RESPONSE = new String(Files.readAllBytes(Paths.get("src/test/resources/location01-000.json")))
    static String FORECAST_RESPONSE_BODY = new String(Files.readAllBytes(Paths.get("src/test/resources/forecast.json")))
    public static final String API_KEY = "123"
    public static final String POSTAL_CODE_00_000 = "00-000"
    public static final String POSTAL_CODE_01_000 = "01-000"

    private WebTestClient.ResponseSpec trafficMonitorResponse;
    private WebTestClient.ResponseSpec forecastResponse;
    private Forecast expectedForecast = readFromAccuResponse(FORECAST_RESPONSE_BODY);

    @Autowired
    private WireMockServer wireMockServer

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebClient webClient

    def setup() {
        stubForAccuweatherForecastCall()
        stubForAccuweatherLocationCallWithPostalCode(POSTAL_CODE_00_000)
        stubForAccuweatherLocationCallWithPostalCode(POSTAL_CODE_01_000)
    }

    def "getting weather forecast with request to accuweather reduction"() {

        when: "I check number of requests to accuweather by calling /statistics/requests/accuweather"
        getAccuweatherCallsSummary()

        then: "I see no calls to Accuweather"
        trafficMonitorResponse.expectStatus().isOk()
        getCallSummaryFromResponse().getLocationCalls() == 0
        getCallSummaryFromResponse().getWeatherCalls() == 0
        getCallSummaryFromResponse().getTotal() == 0

        when: "I ask for weather forecast by calling /forecasts/voivodeships/5day/ with city postal code"
        performGetVoivodeshipForecastWith(POSTAL_CODE_00_000)

        then: "I get weather forecast for next 5 days"
        forecastResponse.expectStatus().isOk()
        getVoivodeshipForecastFromResponse() == expectedForecast

        when: "I check number of forecast request invocations again - /statistics/requests/accuweather"
        getAccuweatherCallsSummary()

        then: "I see that invocations count increased"
        trafficMonitorResponse.expectStatus().isOk()
        getCallSummaryFromResponse().getLocationCalls() == 1
        getCallSummaryFromResponse().getWeatherCalls() == 1
        getCallSummaryFromResponse().getTotal() == 2

        when: "I ask again for the same postal code"
        performGetVoivodeshipForecastWith(POSTAL_CODE_00_000)

        then: "I get the same response again"
        forecastResponse.expectStatus().isOk()
        getVoivodeshipForecastFromResponse() == expectedForecast

        when: "I check number of accuweather invocations again"
        getAccuweatherCallsSummary()

        then: "I see no increase as last call recieved forecast from local repo"
        getCallSummaryFromResponse().getLocationCalls() == 1
        getCallSummaryFromResponse().getWeatherCalls() == 1
        getCallSummaryFromResponse().getTotal() == 2

        when: "I ask again for the different postal code that belongs to same voivodeship"
        performGetVoivodeshipForecastWith(POSTAL_CODE_01_000)

        then: "I get the same weather forecast as it's same voivodeship"
        forecastResponse.expectStatus().isOk()
        getVoivodeshipForecastFromResponse() == expectedForecast

        when: "I check number of accuweather invocations again"
        getAccuweatherCallsSummary()

        then: "I see increased location call counter and weather counter unchanged"
        getCallSummaryFromResponse().getLocationCalls() == 2
        getCallSummaryFromResponse().getWeatherCalls() == 1
        getCallSummaryFromResponse().getTotal() == 3

        when: "I ask again again for second postal code"
        performGetVoivodeshipForecastWith(POSTAL_CODE_01_000)

        and: "check counter"
        getAccuweatherCallsSummary()

        then: "I see no new calls as second postal code was binded to previously saved voivodeship"
        getCallSummaryFromResponse().getLocationCalls() == 2
        getCallSummaryFromResponse().getWeatherCalls() == 1
        getCallSummaryFromResponse().getTotal() == 3
    }

    private WebTestClient.ResponseSpec getAccuweatherCallsSummary() {
        trafficMonitorResponse = webTestClient.get()
                .uri("/statistics/requests/accuweather")
                .exchange()
    }

    private WebTestClient.ResponseSpec performGetVoivodeshipForecastWith(String postalCode) {
        forecastResponse = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + postalCode)
                .exchange()
    }

    private AccuweatherCallsSummary getCallSummaryFromResponse() {
        trafficMonitorResponse.expectBody(AccuweatherCallsSummary)
                .returnResult()
                .responseBody
    }

    private Forecast getVoivodeshipForecastFromResponse() {
        forecastResponse.expectBody(Forecast.class)
                .returnResult()
                .responseBody
    }

    private StubMapping stubForAccuweatherLocationCallWithPostalCode(String postalCode) {
        def url = get("/locations/v1/cities/PL/search?q=" + postalCode + "&apikey=" + API_KEY + "&language=pl-pl&metric=true&countryCode=PL")

        switch (postalCode) {
            case POSTAL_CODE_00_000:
                wireMockServer.stubFor(
                        url.willReturn(aResponse()
                                .withHeader("Content-type", "application/json")
                                .withBody(LOCATION_00_000_RESPONSE)
                                .withStatus(200)))
                break
            case POSTAL_CODE_01_000:
                wireMockServer.stubFor(
                        url.willReturn(aResponse()
                                .withHeader("Content-type", "application/json")
                                .withBody(LOCATION_01_000_RESPONSE)
                                .withStatus(200)))
        }
    }

    private stubForAccuweatherForecastCall() {
        wireMockServer.stubFor(
                get("/forecasts/v1/daily/5day/" + readAccuLocationResponse(LOCATION_00_000_RESPONSE).getKey() + "?apikey=123&language=pl-pl&metric=true")
                        .willReturn(aResponse()
                                .withHeader("Content-type", "application/json")
                                .withBody(FORECAST_RESPONSE_BODY)
                                .withStatus(200)))
    }

}
