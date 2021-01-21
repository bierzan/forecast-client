package io.bierzan.fivedayforecast

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.google.common.collect.Lists
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuForecast
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuTemperature
import io.bierzan.fivedayforecast.client.accuweather.dto.Location
import io.bierzan.fivedayforecast.forecast.dto.Day
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import io.bierzan.fivedayforecast.forecast.dto.Temperature
import io.bierzan.fivedayforecast.forecast.dto.TemperatureUnit
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

import java.time.format.DateTimeFormatter

class AcceptanceTestUtil {
    static ObjectMapper MAPPER = new Jackson2ObjectMapperBuilder()
            .createXmlMapper(false)
            .indentOutput(true)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .serializers(
                    new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .deserializers(
                    new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .modules(
                    new JavaTimeModule())
            .build();

    static AccuForecast readAccuForecastResponse(String accuResponse) {
        return MAPPER.readValue(accuResponse, new TypeReference<AccuForecast>() {})
    }

    static Forecast readFromAccuResponse(String accuResponse) {
        return mapToForecast(readAccuForecastResponse(accuResponse))
    }

    static Location readAccuLocationResponse(String accuResponse) {
        return MAPPER.readValue(accuResponse, new TypeReference<List<Location>>() {}).get(0)
    }

    static Forecast mapToForecast(AccuForecast accuForecast) {
        Forecast forecast = new Forecast();
        forecast.setDailyForecast(Lists.newArrayList());

        accuForecast.getDailyForecast()
                .forEach({ accuDay ->

                    Day day = new Day();
                    day.setDate(accuDay.getDate());

                    Temperature temperature = new Temperature();
                    AccuTemperature accuTemp = accuDay.getTemperature();
                    temperature.setMin(accuTemp.getMin());
                    temperature.setMax(accuTemp.getMax());

                    Arrays.stream(TemperatureUnit.values())
                            .filter({ value -> (value.name() == accuTemp.getUnit()) })
                            .findAny()
                            .ifPresent(temperature.&setUnit);

                    day.setTemperature(temperature);
                    forecast.getDailyForecast().add(day);
                });
        return forecast;
    }
}
