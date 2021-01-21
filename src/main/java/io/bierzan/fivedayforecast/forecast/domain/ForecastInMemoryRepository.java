package io.bierzan.fivedayforecast.forecast.domain;

import com.google.common.collect.Sets;
import io.bierzan.fivedayforecast.forecast.dto.RepoSnapshot;
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast;
import io.bierzan.fivedayforecast.forecast.vo.PostalCode;
import io.bierzan.fivedayforecast.infrastructure.ResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Repository
class ForecastInMemoryRepository implements ForecastRepository {

    private final ConcurrentMap<String, VoivodeshipForecast> repository;
    private final ResourceMapper resourceMapper;

    public ForecastInMemoryRepository(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
        this.repository = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<VoivodeshipForecast> findForecastByVoivodeshipId(String voivodeshipId) {
        return Optional.ofNullable(repository.get(voivodeshipId));
    }

    @Override
    public Optional<VoivodeshipForecast> findForecastByPostalCode(PostalCode postalCode) {
        String prefix = postalCode.getPrefix();
        VoivodeshipForecast foundForecast = null;
        for (VoivodeshipForecast voivodeshipForecast : repository.values()) {
            if (voivodeshipForecast.coversByPostalCodePrefix(prefix)) {
                foundForecast = voivodeshipForecast;
                break;
            }
        }
        return Optional.ofNullable(foundForecast);
    }

    @Override
    public void appendCoveredPostalCodes(String voivodeshipId, PostalCode postalCode) {
        Optional.ofNullable(repository.get(voivodeshipId))
                .ifPresent(voivodeshipForecast -> {
                    voivodeshipForecast.appendPostalCodePrefix(postalCode.getPrefix());
                    log.info("Postal code prefix was stored in repo: [{}]", postalCode);
                });
    }

    @Override
    public Set<VoivodeshipForecast> findAllVoivodeshipForecasts() {
        return Sets.newHashSet(repository.values());
    }

    @Override
    public String insert(VoivodeshipForecast voivodeshipForecast) {
        VoivodeshipForecast addedVoivodeshipForecast =
                repository.putIfAbsent(voivodeshipForecast.getVoivodeship().getId(), voivodeshipForecast);
        if (addedVoivodeshipForecast != null) {
            log.info("Voivodeship forecast stored into repository: id [{}]", voivodeshipForecast.getVoivodeship().getId());
        }
        return voivodeshipForecast.getVoivodeship().getId();
    }

    @Override
    public void updateAll(List<VoivodeshipForecast> voivodeshipsForecasts) {
        Set<String> updatedVoivodeshipsIds = voivodeshipsForecasts.stream()
                .filter(voivodeshipForecast -> nonNull(voivodeshipForecast.getForecast()))
                .map(this::update)
                .collect(Collectors.toSet());
        saveSnapshotFile();
        log.info("Forecast for voivodeships ids: [{}] was updated",
                String.join(", ", updatedVoivodeshipsIds));
    }

    @Override
    public void fillRepoWithRepoSnapshotFileData(){
        List<VoivodeshipForecast> forecastsFromSnapshot = Optional.ofNullable(resourceMapper.readForecastsFromSnapshotFile())
                .map(RepoSnapshot::getVoivodeshipForecasts)
                .orElseGet(Collections::emptyList);

        forecastsFromSnapshot.stream()
                .map(this::insert)
                .forEach(id -> log.info("Voivodeship forecast deserialized from snapshot file and stored into repository: id [{}]", id));
    }

    private String update(VoivodeshipForecast voivodeshipForecast) {
        repository.replace(voivodeshipForecast.getVoivodeship().getId(), voivodeshipForecast);
        return voivodeshipForecast.getVoivodeship().getId();
    }

    private void saveSnapshotFile() {
        resourceMapper.saveSnapshotFile(RepoSnapshot.of(repository.values()));
    }
}
