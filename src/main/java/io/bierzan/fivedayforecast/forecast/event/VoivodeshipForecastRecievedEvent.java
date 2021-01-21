package io.bierzan.fivedayforecast.forecast.event;

import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class VoivodeshipForecastRecievedEvent extends ApplicationEvent {
    private final VoivodeshipForecast voivodeshipForecast;

    public VoivodeshipForecastRecievedEvent(ClientResponseEventPublisher source, VoivodeshipForecast voivodeshipForecast) {
        super(source);
        this.voivodeshipForecast = voivodeshipForecast;
    }
}
