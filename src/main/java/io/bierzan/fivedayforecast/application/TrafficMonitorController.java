package io.bierzan.fivedayforecast.application;

import io.bierzan.fivedayforecast.forecast.dto.AccuweatherCallsSummary;
import io.bierzan.fivedayforecast.forecast.port.RestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
class TrafficMonitorController {
    private final RestClient restClient;

    @GetMapping("/statistics/requests/accuweather")
    public Mono<AccuweatherCallsSummary> getFiveDayWeatherForecast() {
        return restClient.getRestCallsSummary();
    }
}
