package io.bierzan.fivedayforecast.forecast


import io.bierzan.fivedayforecast.forecast.domain.ForecastFacade
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import io.bierzan.fivedayforecast.forecast.exception.ForecastNotFound
import io.bierzan.fivedayforecast.forecast.exception.VoivodeshipNotFound
import io.bierzan.fivedayforecast.samples.SampleForecasts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.mockito.Mockito.when

@WebFluxTest(controllers = ForecastController.class)
class ForecastControllerTest extends Specification implements SampleForecasts {

    public static final String POSTAL_CODE = "postalCode"
    @Autowired
    private WebTestClient webTestClient

    @MockBean
    private ForecastFacade forecastFacade

    def "should return response with status 200 and body with basic forecast"() {
        given:
        when(forecastFacade.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE))
                .thenReturn(Mono.just(basicForecast))
        when:
        def response = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + POSTAL_CODE)
                .exchange()
        then:
        response.expectStatus().isOk()
                .expectBody(Forecast.class)
                .returnResult().getResponseBody() == basicForecast
    }

    def "should return response with status 404 when forecast is not found"() {
        given:
        when(forecastFacade.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE))
                .thenThrow(new ForecastNotFound(
                        String.format("Forecast for postal code: [%s] not found", POSTAL_CODE)
                ))
        when:
        def response = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + POSTAL_CODE)
                .exchange()
        then:
        response.expectStatus().isNotFound()
                .expectBody()
                .jsonPath(".message")
                .isEqualTo("Forecast for postal code: [" + POSTAL_CODE + "] not found")
    }

    def "should return response with status 404 when voivodeship is not found"() {
        given:
        when(forecastFacade.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE))
                .thenThrow(new VoivodeshipNotFound(
                        String.format("Voivodeship for postal code: [%s] not found", POSTAL_CODE)
                ))
        when:
        def response = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + POSTAL_CODE)
                .exchange()
        then:
        response.expectStatus().isNotFound()
                .expectBody()
                .jsonPath(".message")
                .isEqualTo("Voivodeship for postal code: [" + POSTAL_CODE + "] not found")
    }

    def "should return response with status 500 Unknown error occured"() {
        given:
        when(forecastFacade.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE))
                .thenThrow(new RuntimeException())
        when:
        def response = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + POSTAL_CODE)
                .exchange()
        then:
        response.expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath(".message")
                .isEqualTo("Unknown error")
    }

    def "should return response with status 400 when postal code is not valid"() {
        given:
        def invalidPostalCode = "xxx"
        when(forecastFacade.getFiveDayVoivodeshipWeatherForecast(invalidPostalCode))
                .thenCallRealMethod()
        when:
        def response = webTestClient.get()
                .uri("/forecasts/voivodeships/5day/" + invalidPostalCode)
                .exchange()
        then:
        response.expectStatus().isBadRequest()
                .expectBody()
                .jsonPath(".message")
                .isEqualTo("Requested postal code is not valid: [" + invalidPostalCode + "]")
    }
}
