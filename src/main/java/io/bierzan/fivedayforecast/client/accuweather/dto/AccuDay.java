package io.bierzan.fivedayforecast.client.accuweather.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class AccuDay {
    private LocalDate date;
    private AccuTemperature temperature;
}
