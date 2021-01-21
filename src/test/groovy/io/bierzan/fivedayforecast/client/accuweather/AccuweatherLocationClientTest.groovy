package io.bierzan.fivedayforecast.client.accuweather

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.bierzan.fivedayforecast.client.accuweather.dto.Location
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class AccuweatherLocationClientTest extends Specification {

    private static String RESPONSE_BODY = new String(Files.readAllBytes(Paths.get("src/test/resources/location00-000.json")))
    private static ObjectMapper MAPPER = new ObjectMapper()
    private static String API_KEY = "123";

    private MockWebServer mockWebServer = new MockWebServer();
    private WebClient webClient
    private AccuweatherLocationProvider locationProvider

    def setup() {
        webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build()
        locationProvider = new AccuweatherLocationProvider(webClient, API_KEY)
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "should invoke external API to get location data"() {
        given: "postal code and endpoint I need to use to find location data"
        def postalCode = "00-000"
        def endpoint = getExpectedRequestURI(postalCode)
        and: "expected response from external API"
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(RESPONSE_BODY));

        when: "I invoke getLocationByPostalCode"
        def response = locationProvider.getLocationByPostalCode(postalCode).block()

        then: "I confirm proper request url"
        mockWebServer.takeRequest().getRequestUrl().toString() == endpoint
        and: "I get expected response object"
        response == MAPPER.readValue(RESPONSE_BODY, new TypeReference<List<Location>>() {})
    }

    def getExpectedRequestURI(String postalCode) {
        def scheme = "http"
        def host = mockWebServer.getHostName()
        def port = mockWebServer.getPort()
        def path = "/locations/v1/cities/PL/search"
        def query = "q=" + postalCode + "&apikey=" + API_KEY + "&language=pl-pl&metric=true&countryCode=PL"
        return new URI(scheme, null, host, port, path, query, null).toString()
    }

}
