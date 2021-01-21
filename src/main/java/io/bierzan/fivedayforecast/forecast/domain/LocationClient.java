package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.client.accuweather.dto.Location;
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.event.ClientResponseEventPublisher;
import io.bierzan.fivedayforecast.forecast.exception.VoivodeshipNotFound;
import io.bierzan.fivedayforecast.forecast.port.RestClient;
import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class LocationClient {
    private final RestClient restClient;
    private final ClientResponseEventPublisher eventPublisher;

    public Mono<Voivodeship> getVoivodeship(PostalCode postalCode) {
        return restClient.getLocation(postalCode.value())
                .flatMap(location -> Mono.justOrEmpty(convertToVoivodeship(location, postalCode)))
                .switchIfEmpty(Mono.error(new VoivodeshipNotFound(
                        "Voivodeship for postal code: [" + postalCode.value() + "]was not found"
                )))
                .doOnSuccess(voivodeship -> eventPublisher.publishLocationRecievedEvent(voivodeship.getId(), postalCode));
    }

    private Voivodeship convertToVoivodeship(Location location, PostalCode postalCode) {
        Voivodeship voivodeship = new Voivodeship();
        voivodeship.setId(location.getAdministrativeArea().getId());
        voivodeship.setName(location.getAdministrativeArea().getName());
        voivodeship.addPostalCodePrefix(postalCode.getPrefix());
        voivodeship.setLocationKey(location.getKey());
        return voivodeship;
    }
}
