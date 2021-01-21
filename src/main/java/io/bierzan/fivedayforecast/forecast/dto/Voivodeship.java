package io.bierzan.fivedayforecast.forecast.dto;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

@Data
public class Voivodeship {
    private String id;
    private String name;
    private String locationKey;
    private Set<String> postalCodesPrefixes = Sets.newConcurrentHashSet();

    public void addPostalCodePrefix(String prefix){
        postalCodesPrefixes.add(prefix);
    }
}
