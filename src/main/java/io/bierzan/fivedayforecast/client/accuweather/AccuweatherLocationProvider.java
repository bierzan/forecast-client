package io.bierzan.fivedayforecast.client.accuweather;

import io.bierzan.fivedayforecast.client.accuweather.aspect.AccuweatherLocationCall;
import io.bierzan.fivedayforecast.client.accuweather.dto.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@PropertySource("classpath:accuweather.properties")
class AccuweatherLocationProvider {
    private final WebClient webClient;
    private final String apiKey;

    public AccuweatherLocationProvider(
            WebClient webClient,
            @Value("${api.key}") String apiKey
    ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @AccuweatherLocationCall
    public Mono<List<Location>> getLocationByPostalCode(String postalCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("locations/v1/cities/PL/search")
                        .queryParam("q", postalCode)
                        .queryParam("apikey", apiKey)
                        .queryParam("language", "pl-pl")
                        .queryParam("metric", "true")
                        .queryParam("countryCode", "PL")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Location>>() {
                });
    }
}
