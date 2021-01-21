package io.bierzan.fivedayforecast.forecast.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VoivodeshipForecast {
    private Voivodeship voivodeship;
    private Forecast forecast;

    public static VoivodeshipForecast of(Voivodeship voivodeship, Forecast forecast){
        return new VoivodeshipForecast(voivodeship, forecast);
    }

    public boolean coversByPostalCodePrefix(String prefix) {
        return voivodeship.getPostalCodesPrefixes().contains(prefix);
    }

    public void appendPostalCodePrefix(String prefix) {
        voivodeship.getPostalCodesPrefixes().add(prefix);
    }
}
