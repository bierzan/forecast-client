package io.bierzan.fivedayforecast.forecast.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class RepoSnapshot {
    private LocalDateTime date;
    private List<VoivodeshipForecast> voivodeshipForecasts;

    public static RepoSnapshot of(Collection<VoivodeshipForecast> voivodeshipForecasts) {
        RepoSnapshot snapshot = new RepoSnapshot();
        snapshot.setVoivodeshipForecasts(new ArrayList<>(voivodeshipForecasts));
        snapshot.setDate(LocalDateTime.now());
        return snapshot;
    }
}
