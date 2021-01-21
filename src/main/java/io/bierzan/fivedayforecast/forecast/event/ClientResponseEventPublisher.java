package io.bierzan.fivedayforecast.forecast.event;

import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientResponseEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishLocationRecievedEvent(String voivodeshipId, PostalCode postalCode){
        eventPublisher.publishEvent(new LocationRecievedEvent(this, voivodeshipId, postalCode));
    }

    public void publishVoivodeshipForecastRecievedEvent(VoivodeshipForecast voivodeshipForecast) {
        eventPublisher.publishEvent(new VoivodeshipForecastRecievedEvent(this, voivodeshipForecast));
    }
}
