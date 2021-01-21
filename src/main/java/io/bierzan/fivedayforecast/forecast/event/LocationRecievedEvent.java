package io.bierzan.fivedayforecast.forecast.event;

import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LocationRecievedEvent extends ApplicationEvent {
    private final String voivodeshipId;
    private final PostalCode postalCode;

    public LocationRecievedEvent(Object source, String voivodeshipId, PostalCode postalCode) {
        super(source);
        this.voivodeshipId = voivodeshipId;
        this.postalCode = postalCode;
    }
}
