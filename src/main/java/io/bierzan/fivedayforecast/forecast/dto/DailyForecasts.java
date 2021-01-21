package io.bierzan.fivedayforecast.forecast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyForecasts {
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Temperature temperature;
}
