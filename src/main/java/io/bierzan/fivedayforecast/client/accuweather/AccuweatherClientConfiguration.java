package io.bierzan.fivedayforecast.client.accuweather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@PropertySource("classpath:accuweather.properties")
class AccuweatherClientConfiguration {

    @Bean
    WebClient webClient(@Value("${host}") String host) {
        return WebClient.builder()
                .baseUrl(host)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                }).build();
    }


    private ExchangeFilterFunction logRequest() {
        return (ClientRequest request, ExchangeFunction next) -> {
            log.info("Request: {} {} {}", request.logPrefix(), request.method(), request.url());
            request.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(request);
        };
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("Response status: {} {}", response.logPrefix(), response.statusCode());
            return Mono.just(response);
        });
    }

}
