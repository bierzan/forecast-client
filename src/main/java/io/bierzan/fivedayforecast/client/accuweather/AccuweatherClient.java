package io.bierzan.fivedayforecast.client.accuweather;

import io.bierzan.fivedayforecast.client.accuweather.dto.Location;
import io.bierzan.fivedayforecast.forecast.dto.AccuweatherCallsSummary;
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.port.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
class AccuweatherClient implements RestClient {

    private final AccuweatherCallCounter callCounter;
    private final AccuweatherLocationProvider locationProvider;
    private final AccuweatherForecastProvider forecastProvider;

    @Override
    public Mono<Location> getLocation(String postalCode) {
        return locationProvider.getLocationByPostalCode(postalCode)
                .flatMap(locations -> Mono.justOrEmpty(getFirst(locations)));
    }

    private Location getFirst(List<Location> locations) {
        return locations.isEmpty() ? null : locations.get(0);
    }

    @Override
    public List<VoivodeshipForecast> getVoivodeshipsForecasts(Set<Voivodeship> voivodeships) {
        return forecastProvider.getVoivodeshipsForecasts(voivodeships)
                .collectList()
                .block();
    }

    @Override
    public Mono<VoivodeshipForecast> getVoivodeshipForecast(Voivodeship voivodeship) {
        return forecastProvider.getForecastForVoivodeship(voivodeship);
    }

    @Override
    public Mono<AccuweatherCallsSummary> getRestCallsSummary() {
        return Mono.just(callCounter.getSummary());
    }

}
