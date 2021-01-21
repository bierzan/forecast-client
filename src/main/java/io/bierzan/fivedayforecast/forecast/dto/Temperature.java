package io.bierzan.fivedayforecast.forecast.dto;

import lombok.Data;

@Data
public class Temperature {
    private double min;
    private double max;
    private TemperatureUnit unit;
}
