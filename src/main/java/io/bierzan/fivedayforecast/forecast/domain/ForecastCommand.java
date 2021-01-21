package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.forecast.dto.Voivodeship;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.event.LocationRecievedEvent;
import io.bierzan.fivedayforecast.forecast.event.VoivodeshipForecastRecievedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
class ForecastCommand {

    private final ForecastClient forecastClient;
    private final ForecastRepository repository;

    /**
     * Method to update forecasts for Voivodeships stored in Repository.
     * New forecast data is provided by external api
     * Runs at time configured in forecast.properties
     */
    @Scheduled(cron = "${forecast.update.frequency.cron}")
    protected void udateVoivodeshipForecasts() {
        Set<Voivodeship> voivodeships = repository.findAllVoivodeshipForecasts().stream()
                .map(VoivodeshipForecast::getVoivodeship)
                .collect(Collectors.toSet());
        List<VoivodeshipForecast> voivodeshipsForecasts = forecastClient.getVoivodeshipsForecasts(voivodeships);
        repository.updateAll(voivodeshipsForecasts);
    }

    @EventListener(value = ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        repository.fillRepoWithRepoSnapshotFileData();
    }

    @EventListener(value = LocationRecievedEvent.class)
    public void onLocationRecievedEvent(LocationRecievedEvent event) {
        repository.appendCoveredPostalCodes(event.getVoivodeshipId(), event.getPostalCode());
    }

    @EventListener(value = VoivodeshipForecastRecievedEvent.class)
    public void onVoivodeshipForecastRecievedEvent(VoivodeshipForecastRecievedEvent event) {
        repository.insert(event.getVoivodeshipForecast());
    }
}
