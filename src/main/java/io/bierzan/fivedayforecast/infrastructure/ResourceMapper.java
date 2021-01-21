package io.bierzan.fivedayforecast.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bierzan.fivedayforecast.forecast.dto.RepoSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class ResourceMapper {

    private final String snapshotPath;
    private final ObjectMapper objectMapper;

    public ResourceMapper(
            @Value("${forecast.repository.snapshot.path}") String snapshotPath,
            ObjectMapper objectMapper
    ) {
        this.snapshotPath = snapshotPath;
        this.objectMapper = objectMapper;
    }

    public RepoSnapshot readForecastsFromSnapshotFile() {
        log.info("Reading repository snapshot file form path: {}", snapshotPath);
        try {
            byte[] file = Files.readAllBytes(Paths.get(snapshotPath));
            return objectMapper.readValue(file, new TypeReference<RepoSnapshot>() {});
        } catch (IOException e) {
            log.warn("{}: {}", e.getClass(), e.getMessage());
            log.warn("No repository snapshot found. Application runs with empty repository");
            return null;
        }
    }

    public void saveSnapshotFile(RepoSnapshot snapshot) {
        try {
            log.info("Saving repository snapshot file; path: {}", snapshotPath);
            objectMapper.writeValue(new File(snapshotPath), snapshot);
        } catch (IOException e) {
            log.warn("Repository snapshot save failed");
            e.printStackTrace();
        }

    }
}
