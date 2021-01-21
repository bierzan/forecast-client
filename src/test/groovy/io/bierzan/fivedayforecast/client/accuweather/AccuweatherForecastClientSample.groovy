package io.bierzan.fivedayforecast.client.accuweather

import com.fasterxml.jackson.core.type.TypeReference
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import io.bierzan.fivedayforecast.AcceptanceTestUtil
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuForecast
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast
import io.bierzan.fivedayforecast.samples.SampleVoivodeships
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class AccuweatherForecastClientSample extends Specification implements SampleVoivodeships {
    private static String RESPONSE_BODY = new String(Files.readAllBytes(Paths.get("src/test/resources/forecast.json")))
    private static String API_KEY = "123";

    private Set<Voivodeship> voivodeshipsToGetForecast = Sets.newHashSet()
    private List<String> expectedRequestUrl = Lists.newArrayList()
    private MockWebServer mockWebServer = new MockWebServer()
    private WebClient webClient
    private AccuweatherForecastProvider forecastProvider
    private AccuweatherForecastMapper forecastMapper = new AccuweatherForecastMapper()


    def setup() {
        webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build()
        forecastProvider = new AccuweatherForecastProvider(forecastMapper, webClient, API_KEY)
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "should invoke external API to get voivodeships forecasts"() {
        given: "voivodeships I want to get forecast for"
        setVoivodeshipsToRequestForecast(mazowieckieVoivodeship, lodzkieVoivodeship, zachodniopomorskie)
        and: "expected endpoints to compare with actual endpoints"
        generateExpectedEndpointForVoivodeshipForecastRequest()

        and: "expected response from external API for each forecast request"
        expectedRequestUrl.each {
            mockWebServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .setBody(RESPONSE_BODY))
        }

        when: "I invoke getLocationByPostalCode"
        def response = forecastProvider.getVoivodeshipsForecasts(voivodeshipsToGetForecast)
                .collectList()
                .block()

        then: "I confirm proper request url"
        collectAllInvokedUrls().sort() == expectedRequestUrl.sort()
        and: "I get expected response object"
        response.sort() == getVoivodeshipForecast().sort()
    }

    private List<String> collectAllInvokedUrls() {
        return voivodeshipsToGetForecast.stream()
                .map({ v -> mockWebServer.takeRequest().getRequestUrl().toString() })
                .collect(Collectors.toList())
    }

    private generateExpectedEndpointForVoivodeshipForecastRequest() {
        expectedRequestUrl.addAll(voivodeshipsToGetForecast.stream()
                .map({ voivodeship -> voivodeship.getLocationKey() })
                .map({ key -> getExpectedRequestURI(key) })
                .toArray() as String[])

    }

    def setVoivodeshipsToRequestForecast(Voivodeship... voivodeships) {
        voivodeshipsToGetForecast.addAll(voivodeships)
    }

    def getExpectedRequestURI(String locationKey) {
        def scheme = "http"
        def host = mockWebServer.getHostName()
        def port = mockWebServer.getPort()
        def path = "/forecasts/v1/daily/5day/" + locationKey
        def query = "apikey=" + API_KEY + "&language=pl-pl&metric=true"
        return new URI(scheme, null, host, port, path, query, null).toString()
    }

    def getVoivodeshipForecast() {
        Forecast forecast = forecastMapper.forecastFrom(AcceptanceTestUtil.MAPPER.readValue(RESPONSE_BODY, new TypeReference<AccuForecast>() {}));
        return voivodeshipsToGetForecast.stream()
                .map({ voivodeship -> VoivodeshipForecast.of(voivodeship, forecast) })
                .collect(Collectors.toList())
    }
}
