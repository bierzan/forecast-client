package io.bierzan.fivedayforecast.client.accuweather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdministrativeArea {
    @JsonProperty(value = "ID")
    private String id;
    @JsonProperty(value = "LocalizedName")
    private String name;
}
