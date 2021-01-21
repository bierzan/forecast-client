package io.bierzan.fivedayforecast.forecast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {
    private List<Day> dailyForecast;
}
