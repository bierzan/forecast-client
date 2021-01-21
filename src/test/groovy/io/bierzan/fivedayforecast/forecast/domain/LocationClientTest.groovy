package io.bierzan.fivedayforecast.forecast.domain

import io.bierzan.fivedayforecast.client.accuweather.dto.AdministrativeArea
import io.bierzan.fivedayforecast.client.accuweather.dto.Location
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship
import io.bierzan.fivedayforecast.forecast.event.ClientResponseEventPublisher
import io.bierzan.fivedayforecast.forecast.exception.VoivodeshipNotFound
import io.bierzan.fivedayforecast.forecast.port.RestClient
import io.bierzan.fivedayforecast.forecast.vo.PostalCode
import io.bierzan.fivedayforecast.samples.SampleVoivodeships
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.assertj.core.api.Assertions.catchThrowable

class LocationClientTest extends Specification implements SampleVoivodeships {
    private static final POSTAL_CODE = PostalCode.of("00-000")
    private final Location location = locationFromSampleVoivodeship(lodzkieVoivodeship);
    private final RestClient restClient = Mock()
    private final ClientResponseEventPublisher eventPublisher = Mock()

    private final LocationClient locationClient = new LocationClient(restClient, eventPublisher)

    def "resClient answered with location scenario"() {
        given: "rest client response with location data"
        restClient.getLocation(POSTAL_CODE.value()) >> Mono.justOrEmpty(location)
        when: "I invoke getVoivodeship"
        def response = locationClient.getVoivodeship(POSTAL_CODE)
        then: "I get voivodeship with data provided by rest client"
        def voivodeship = response.block()
        voivodeship.id == lodzkieVoivodeship.id
        voivodeship.name == lodzkieVoivodeship.name
        voivodeship.locationKey == lodzkieVoivodeship.locationKey
        and: "Voivodeship contains postal code prefix inside prefixes list"
        voivodeship.postalCodesPrefixes.contains(POSTAL_CODE.prefix)
        voivodeship.postalCodesPrefixes.size() == 1
    }

    def "rest client responded with null"() {
        given: "I get no response from rest client"
        restClient.getLocation(_ as String) >> Mono.justOrEmpty(null)
        when: "I invoke getVoivodeship"
        def voivodeship = locationClient.getVoivodeship(POSTAL_CODE)
        then: "I see it responded with exception"
        def exception = catchThrowable({ -> voivodeship.block() })
        exception instanceof VoivodeshipNotFound
        exception.getMessage() == "Voivodeship for postal code: [" + POSTAL_CODE.value() + "]was not found"
    }

    def locationFromSampleVoivodeship(Voivodeship voivodeship) {
        Location location = new Location();
        location.setKey(voivodeship.locationKey)
        location.setAdministrativeArea(new AdministrativeArea())
        location.getAdministrativeArea().setId(voivodeship.id)
        location.getAdministrativeArea().setName(voivodeship.name)
        return location;
    }

}
