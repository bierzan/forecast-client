package io.bierzan.fivedayforecast.forecast.dto;

import lombok.Data;

@Data
public class AccuweatherCallsSummary {
    private int weatherCalls;
    private int locationCalls;
    private int total;
}
