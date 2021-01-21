package io.bierzan.fivedayforecast.client.accuweather;

import com.google.common.collect.Lists;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuDay;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuForecast;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuTemperature;
import io.bierzan.fivedayforecast.forecast.dto.Day;
import io.bierzan.fivedayforecast.forecast.dto.Forecast;
import io.bierzan.fivedayforecast.forecast.dto.Temperature;
import io.bierzan.fivedayforecast.forecast.dto.TemperatureUnit;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
class AccuweatherForecastMapper {

    public Forecast forecastFrom(AccuForecast accuForecast) {
        Forecast forecast = new Forecast();
        forecast.setDailyForecast(Lists.newArrayList());

        accuForecast.getDailyForecast().forEach(accuDay -> {
            Day day = dayFrom(accuDay);
            forecast.getDailyForecast().add(day);
        });

        return forecast;
    }

    private Day dayFrom(AccuDay accuDay) {
        Day day = new Day();
        day.setDate(accuDay.getDate());
        day.setTemperature(temperatureFrom(accuDay.getTemperature()));
        return day;
    }

    private Temperature temperatureFrom(AccuTemperature accuTemperature) {
        Temperature temperature = new Temperature();
        temperature.setMin(accuTemperature.getMin());
        temperature.setMax(accuTemperature.getMax());

        Arrays.stream(TemperatureUnit.values())
                .filter(value -> value.toString().equals(accuTemperature.getUnit()))
                .findAny()
                .ifPresent(temperature::setUnit);

        return temperature;
    }
}
