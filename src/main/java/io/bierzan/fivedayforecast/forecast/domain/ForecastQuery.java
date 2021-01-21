package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.forecast.dto.Forecast;
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
class ForecastQuery {

    private final ForecastRepository repository;
    private final LocationClient locationClient;
    private final ForecastClient forecastClient;

    /**
     * Returns 5 day weather forecast for voivodeship.
     * First it looks for already stored forecast in local repo with postalCode as param.
     * If fails, request to accuweather is send to obtain voivodeship.
     * When location it looks again in local repo by voivodeship.
     * If fails, request is send to accuweather to get forecast by voivodeship location key
     *
     * @param postalCode postal code to matchi with voivodeship
     * @return 5 day forecast wrapped in Mono
     */
    public Mono<Forecast> getFiveDayVoivodeshipWeatherForecast(PostalCode postalCode) {
        return getForecastFromRepoByPostalCode(postalCode)
                .switchIfEmpty(getForecastByCollectingDataFromClient(postalCode))
                .doOnError(Mono::error);
    }

    private Mono<Forecast> getForecastFromRepoByPostalCode(PostalCode postalCode) {
        return Mono.justOrEmpty(repository.findForecastByPostalCode(postalCode))
                .flatMap(voivodeshipForecast -> Mono.just(voivodeshipForecast.getForecast()));
    }

    private Mono<Forecast> getForecastByCollectingDataFromClient(PostalCode postalCode) {
        return Mono.defer(() -> locationClient.getVoivodeship(postalCode))
                .flatMap(this::getVoivodeshipForecast)
                .map(VoivodeshipForecast::getForecast);
    }

    private Mono<VoivodeshipForecast> getVoivodeshipForecast(Voivodeship voivodeship) {
        return Mono.justOrEmpty(repository.findForecastByVoivodeshipId(voivodeship.getId()))
                .switchIfEmpty(Mono.defer(() -> forecastClient.getVoivodeshipForecast(voivodeship)));

    }
}
