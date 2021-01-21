package io.bierzan.fivedayforecast.forecast.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class Day {
    private LocalDate date;
    private Temperature temperature;
}
