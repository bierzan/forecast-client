package io.bierzan.fivedayforecast.client.accuweather;

import io.bierzan.fivedayforecast.client.accuweather.aspect.Counter;
import io.bierzan.fivedayforecast.forecast.dto.AccuweatherCallsSummary;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
class AccuweatherCallCounter implements Counter {
    private final AtomicInteger weatherCalls;
    private final AtomicInteger locationCalls;

    public AccuweatherCallCounter() {
        this.weatherCalls = new AtomicInteger(0);
        this.locationCalls = new AtomicInteger(0);
    }

    @Override
    public void addLocationCall() {
        locationCalls.incrementAndGet();
    }

    @Override
    public void addForecastCall() {
        weatherCalls.incrementAndGet();
    }

    public AccuweatherCallsSummary getSummary() {
        AccuweatherCallsSummary summary = new AccuweatherCallsSummary();
        summary.setLocationCalls(locationCalls.get());
        summary.setWeatherCalls(weatherCalls.get());
        summary.setTotal(locationCalls.get() + weatherCalls.get());
        return summary;
    }
}
