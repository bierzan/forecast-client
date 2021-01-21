package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.event.ClientResponseEventPublisher;
import io.bierzan.fivedayforecast.forecast.exception.ForecastNotFound;
import io.bierzan.fivedayforecast.forecast.port.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
class ForecastClient {
    private final RestClient restClient;
    private final ClientResponseEventPublisher eventPublisher;

    public List<VoivodeshipForecast> getVoivodeshipsForecasts(Set<Voivodeship> voivodeships) {
        return restClient.getVoivodeshipsForecasts(voivodeships);
    }

    public Mono<VoivodeshipForecast> getVoivodeshipForecast(Voivodeship voivodeship) {
        return restClient.getVoivodeshipForecast(voivodeship)
                .switchIfEmpty(Mono.error(new ForecastNotFound(
                        String.format(
                                "Forecast for voivodeship: [name: %s; id: %s] was not found",
                                voivodeship.getName(),
                                voivodeship.getId()
                        )
                )))
                .doOnSuccess(eventPublisher::publishVoivodeshipForecastRecievedEvent);
    }
}
