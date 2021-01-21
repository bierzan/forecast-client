package io.bierzan.fivedayforecast.forecast;

import io.bierzan.fivedayforecast.forecast.domain.ForecastFacade;
import io.bierzan.fivedayforecast.forecast.dto.Forecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/forecasts")
@RequiredArgsConstructor
class ForecastController {

    private final ForecastFacade forecastFacade;

    @GetMapping("/voivodeships/5day/{postalCode}")
    public Mono<Forecast> getFiveDayWeatherForecast(@PathVariable String postalCode) {
        log.info("Request: GET /forecasts/voivodeships/5day/{}", postalCode);
        return forecastFacade.getFiveDayVoivodeshipWeatherForecast(postalCode);
    }
}
