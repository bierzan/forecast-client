package io.bierzan.fivedayforecast.forecast.dto;

public enum TemperatureUnit {
    C("Celsius");

    private String name;

    TemperatureUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
