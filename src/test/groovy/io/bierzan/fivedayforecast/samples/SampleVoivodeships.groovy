package io.bierzan.fivedayforecast.samples

import groovy.transform.CompileStatic
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship

@CompileStatic
trait SampleVoivodeships {
    Voivodeship mazowieckieVoivodeship = getVoivodeship("14", "mazowieckie", "334306_PC")

    Voivodeship lodzkieVoivodeship = getVoivodeship("10", "łódzkie", "352845_PC")

    Voivodeship zachodniopomorskie = getVoivodeship("32", "zachodniopomorskie", "347716_PC")

    static getVoivodeship(String id, String name, String locationKey) {
        Voivodeship voivodeship = new Voivodeship()
        voivodeship.setId(id)
        voivodeship.setName(name)
        voivodeship.setLocationKey(locationKey)
        return voivodeship
    }

}