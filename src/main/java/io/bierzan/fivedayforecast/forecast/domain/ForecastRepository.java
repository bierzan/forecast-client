package io.bierzan.fivedayforecast.forecast.domain;

import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.vo.PostalCode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ForecastRepository {
    Optional<VoivodeshipForecast> findForecastByVoivodeshipId(String voivodeshipId);

    Set<VoivodeshipForecast> findAllVoivodeshipForecasts();

    void updateAll(List<VoivodeshipForecast> voivodeshipsForecasts);

    String insert(VoivodeshipForecast voivodeshipForecast);

    Optional<VoivodeshipForecast> findForecastByPostalCode(PostalCode postalCode);

    void appendCoveredPostalCodes(String voivodeshipId, PostalCode postalCode);

    void fillRepoWithRepoSnapshotFileData();
}
