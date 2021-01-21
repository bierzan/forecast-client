package io.bierzan.fivedayforecast.client.accuweather;

import io.bierzan.fivedayforecast.client.accuweather.aspect.AccuweatherForecastCall;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuForecast;
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;

@Component
@PropertySource("classpath:accuweather.properties")
class AccuweatherForecastProvider {
    private final AccuweatherForecastMapper forecastMapper;
    private final WebClient webClient;
    private final String apiKey;

    public AccuweatherForecastProvider(
            AccuweatherForecastMapper forecastMapper,
            WebClient webClient,
            @Value("${api.key}") String apiKey
    ) {
        this.forecastMapper = forecastMapper;
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public Flux<VoivodeshipForecast> getVoivodeshipsForecasts(Set<Voivodeship> voivodeships) {
        return Flux.fromIterable(voivodeships)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::getForecastForVoivodeship)
                .sequential();
    }

    @AccuweatherForecastCall
    public Mono<VoivodeshipForecast> getForecastForVoivodeship(Voivodeship voivodeship) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("forecasts/v1/daily/5day/{locationCode}")
                        .queryParam("apikey", apiKey)
                        .queryParam("language", "pl-pl")
                        .queryParam("metric", "true")
                        .build(voivodeship.getLocationKey()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AccuForecast>() {
                })
                .map(forecastMapper::forecastFrom)
                .map(forecast -> VoivodeshipForecast.of(voivodeship, forecast));
    }
}
