package io.bierzan.fivedayforecast.client.accuweather.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @JsonProperty(value = "Key")
    private String key;
    @JsonProperty(value = "AdministrativeArea")
    private AdministrativeArea administrativeArea;
}
