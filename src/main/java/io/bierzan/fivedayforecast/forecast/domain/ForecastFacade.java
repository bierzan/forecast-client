package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import io.bierzan.fivedayforecast.forecast.dto.Forecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ForecastFacade {

    private final ForecastQuery query;

    public Mono<Forecast> getFiveDayVoivodeshipWeatherForecast(String postalCode) {
        return query.getFiveDayVoivodeshipWeatherForecast(PostalCode.of(postalCode));
    }
}
