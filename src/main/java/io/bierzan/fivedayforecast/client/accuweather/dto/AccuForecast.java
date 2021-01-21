package io.bierzan.fivedayforecast.client.accuweather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bierzan.fivedayforecast.client.accuweather.DailyForecastDeserializer;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccuForecast {
    @JsonProperty("DailyForecasts")
    @JsonDeserialize(using = DailyForecastDeserializer.class)
    private List<AccuDay> dailyForecast;
}
