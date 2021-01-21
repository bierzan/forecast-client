package io.bierzan.fivedayforecast.client.accuweather.dto;

import lombok.Data;

@Data
public class AccuTemperature {
    private double min;
    private double max;
    private String unit;
}
