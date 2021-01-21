package io.bierzan.fivedayforecast.client.accuweather;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuDay;
import io.bierzan.fivedayforecast.client.accuweather.dto.AccuTemperature;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DailyForecastDeserializer extends JsonDeserializer<List<AccuDay>> {
    @Override
    public List<AccuDay> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode[] arrayNode = parser.readValueAs(JsonNode[].class);
        List<AccuDay> dailyForecasts = new ArrayList<>();

        for (JsonNode node : arrayNode) {
            AccuDay day = new AccuDay();
            LocalDate date = LocalDate.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(node.get("Date").asText()));
            day.setDate(date);
            AccuTemperature temperature = new AccuTemperature();
            JsonNode temperatureNode = node.get("Temperature");
            temperature.setMax(temperatureNode.get("Maximum").get("Value").asDouble());
            temperature.setMin(temperatureNode.get("Minimum").get("Value").asDouble());
            temperature.setUnit(temperatureNode.get("Minimum").get("Unit").asText());
            day.setTemperature(temperature);
            dailyForecasts.add(day);
        }
        return dailyForecasts;
    }
}
