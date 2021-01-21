package io.bierzan.fivedayforecast.forecast.domain

import com.google.common.collect.Sets
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast
import io.bierzan.fivedayforecast.forecast.event.ClientResponseEventPublisher
import io.bierzan.fivedayforecast.forecast.exception.ForecastNotFound
import io.bierzan.fivedayforecast.forecast.port.RestClient
import io.bierzan.fivedayforecast.samples.SampleForecasts
import io.bierzan.fivedayforecast.samples.SampleVoivodeships
import org.assertj.core.util.Lists
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.assertj.core.api.Assertions.catchThrowable

class ForecastClientTest extends Specification implements SampleVoivodeships, SampleForecasts {
    private final VoivodeshipForecast forecastForLodzkie = VoivodeshipForecast.of(lodzkieVoivodeship, basicForecast)
    private final VoivodeshipForecast forecastForMazowieckie = VoivodeshipForecast.of(mazowieckieVoivodeship, basicForecast)


    private final voivodeshipForecast = VoivodeshipForecast.of(lodzkieVoivodeship, basicForecast)
    private final RestClient restClient = Mock()
    private final ClientResponseEventPublisher eventPublisher = Mock()

    private final ForecastClient forecastClient = new ForecastClient(restClient, eventPublisher)

    def "should return forecast if resClient answered with forecast"() {
        given: "rest client response with forecast data"
        restClient.getVoivodeshipForecast(lodzkieVoivodeship) >> Mono.just(voivodeshipForecast)
        when: "I invoke getVoivodeshipForecast"
        def response = forecastClient.getVoivodeshipForecast(lodzkieVoivodeship)
        then: "I get forecast"
        def forecast = response.block()
        forecast == voivodeshipForecast
    }

    def "should throw exception forecast if resClient responded with null"() {
        given: "rest client response with location data"
        restClient.getVoivodeshipForecast(lodzkieVoivodeship) >> Mono.justOrEmpty(null)
        when: "I invoke getVoivodeship"
        def response = forecastClient.getVoivodeshipForecast(lodzkieVoivodeship)
        then: "I get voivodeship with data provided by rest client"
        def exception = catchThrowable({ -> response.block() })
        exception instanceof ForecastNotFound
        exception.getMessage() == String.format(
                "Forecast for voivodeship: [name: %s; id: %s] was not found",
                lodzkieVoivodeship.getName(),
                lodzkieVoivodeship.getId()
        )
    }

    def "should return list of forecast"() {
        given: "rest client response with forecast data"
        def voivodeships = Sets.newHashSet(lodzkieVoivodeship, mazowieckieVoivodeship)
        def voivodeshipForecasts = Lists.newArrayList(forecastForLodzkie, forecastForMazowieckie)
        restClient.getVoivodeshipsForecasts(voivodeships) >> voivodeshipForecasts
        when: "I invoke getVoivodeshipForecast"
        def response = forecastClient.getVoivodeshipsForecasts(voivodeships)
        then: "I get forecasts"
        response.sort() == voivodeshipForecasts.sort()
    }
}