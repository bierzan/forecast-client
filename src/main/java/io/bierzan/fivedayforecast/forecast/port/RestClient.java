package io.bierzan.fivedayforecast.forecast.port;

import io.bierzan.fivedayforecast.forecast.dto.AccuweatherCallsSummary;
import io.bierzan.fivedayforecast.client.accuweather.dto.Location;
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface RestClient {
    Mono<Location> getLocation(String postalCode);

    List<VoivodeshipForecast> getVoivodeshipsForecasts(Set<Voivodeship> voivodeships);

    Mono<VoivodeshipForecast> getVoivodeshipForecast(Voivodeship voivodeship);

    Mono<AccuweatherCallsSummary> getRestCallsSummary();
}
