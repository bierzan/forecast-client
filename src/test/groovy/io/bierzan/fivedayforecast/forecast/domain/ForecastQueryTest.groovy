package io.bierzan.fivedayforecast.forecast.domain


import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast
import io.bierzan.fivedayforecast.forecast.exception.ForecastNotFound
import io.bierzan.fivedayforecast.forecast.exception.VoivodeshipNotFound
import io.bierzan.fivedayforecast.forecast.vo.PostalCode
import io.bierzan.fivedayforecast.samples.SampleForecasts
import io.bierzan.fivedayforecast.samples.SampleVoivodeships
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.assertj.core.api.Assertions.catchThrowable

class ForecastQueryTest extends Specification implements SampleVoivodeships, SampleForecasts {
    public static final PostalCode POSTAL_CODE = PostalCode.of("00-000")
    public static final String VOIVODESHIP_NOT_FOUND_MSG = "Voivodeship not found"
    public static final String FORECAST_NOT_FOUND_MSG = "Forecast not found"
    private VoivodeshipForecast voivodeshipForecast = new VoivodeshipForecast(lodzkieVoivodeship, basicForecast)
    private ForecastRepository repository = Mock()
    private LocationClient locationClient = Mock()
    private ForecastClient forecastClient = Mock()

    private ForecastQuery forecastQuery = new ForecastQuery(repository, locationClient, forecastClient)

    def "should return forecast if it can be found in repo by postal code"() {
        given: "repository with requested forecast"
        repository.findForecastByPostalCode(POSTAL_CODE) >> Optional.of(voivodeshipForecast)
        when:
        def forecast = forecastQuery.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE)
        then:
        forecast.block() == basicForecast;
    }

    def "should return forecast if it can be found only by accuweather voivodeship id"() {
        given: "no forecast can be found by postal code"
        repository.findForecastByPostalCode(POSTAL_CODE) >> Optional.ofNullable(null)
        and: "I get voivodeship data from accuweather api"
        locationClient.getVoivodeship(POSTAL_CODE) >> Mono.just(lodzkieVoivodeship)
        and: "I found forecast by voivodeship id"
        repository.findForecastByVoivodeshipId(lodzkieVoivodeship.id) >> Optional.of(voivodeshipForecast)
        when:
        def forecast = forecastQuery.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE)
        then:
        forecast.block() == basicForecast;
    }

    def "should return forecast only from accuweather api"() {
        given: "no forecast can be found in repo"
        repository.findForecastByPostalCode(POSTAL_CODE) >> Optional.ofNullable(null)
        repository.findForecastByVoivodeshipId(_ as String) >> Optional.ofNullable(null)
        and: "I get voivodeship data from accuweather api"
        locationClient.getVoivodeship(POSTAL_CODE) >> Mono.just(lodzkieVoivodeship)
        and: "I get forecast from accuweather api based on voivodeship location key"
        forecastClient.getVoivodeshipForecast(lodzkieVoivodeship) >> Mono.just(voivodeshipForecast)
        when:
        def forecast = forecastQuery.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE)
        then:
        forecast.block() == basicForecast;
    }

    def "should throw Voivodeship not found exception if location can't be determined"() {
        given: "no forecast can be found in repo"
        repository.findForecastByPostalCode(POSTAL_CODE) >> Optional.ofNullable(null)
        repository.findForecastByVoivodeshipId(_ as String) >> Optional.ofNullable(null)
        and: "I get exception from location client"
        locationClient.getVoivodeship(POSTAL_CODE) >> Mono.error(new VoivodeshipNotFound(VOIVODESHIP_NOT_FOUND_MSG))
        when:
        def exception = catchThrowable({ -> forecastQuery.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE).block() })
        then:
        exception instanceof VoivodeshipNotFound
        exception.getMessage() == VOIVODESHIP_NOT_FOUND_MSG;
    }

    def "should throw forecast not found exception if forecast for voivodeship can't be determined"() {
        given: "no forecast can be found in repo"
        repository.findForecastByPostalCode(POSTAL_CODE) >> Optional.ofNullable(null)
        repository.findForecastByVoivodeshipId(_ as String) >> Optional.ofNullable(null)
        and: "I get empty list of location from accuweather api"
        locationClient.getVoivodeship(POSTAL_CODE) >> Mono.just(lodzkieVoivodeship)
        and: "I get forecast from accuweather api based on voivodeship location key"
        forecastClient.getVoivodeshipForecast(lodzkieVoivodeship) >> Mono.error(new ForecastNotFound(FORECAST_NOT_FOUND_MSG))
        when:
        def exception = catchThrowable({ -> forecastQuery.getFiveDayVoivodeshipWeatherForecast(POSTAL_CODE).block() })
        then:
        exception instanceof ForecastNotFound
        exception.getMessage() == FORECAST_NOT_FOUND_MSG;
    }
}
