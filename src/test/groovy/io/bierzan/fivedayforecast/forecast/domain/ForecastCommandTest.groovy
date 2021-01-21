package io.bierzan.fivedayforecast.forecast.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.google.common.collect.Lists
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import io.bierzan.fivedayforecast.forecast.dto.Voivodeship
import io.bierzan.fivedayforecast.forecast.dto.VoivodeshipForecast
import io.bierzan.fivedayforecast.infrastructure.ResourceMapper
import io.bierzan.fivedayforecast.samples.SampleForecasts
import io.bierzan.fivedayforecast.samples.SampleVoivodeships
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ForecastCommandTest extends Specification implements SampleForecasts, SampleVoivodeships {

    public static final String SNAPSHOT_PATH = "src/test/resources/snapshotpath.json"
    private final ForecastClient forecastClient = Mock();
    private final ResourceMapper resourceMapper = new ResourceMapper(SNAPSHOT_PATH, prepareObjectMapper());
    private final ForecastRepository repository = new ForecastInMemoryRepository(resourceMapper);

    private final Forecast newForecast = getNewForecast()
    private final VoivodeshipForecast forecastForLodzkie = VoivodeshipForecast.of(lodzkieVoivodeship, basicForecast)
    private final VoivodeshipForecast newForecastForLodzkie = VoivodeshipForecast.of(lodzkieVoivodeship, newForecast)
    private final VoivodeshipForecast forecastForMazowieckie = VoivodeshipForecast.of(mazowieckieVoivodeship, basicForecast)
    private final VoivodeshipForecast newForecastForMazowieckie = VoivodeshipForecast.of(mazowieckieVoivodeship, newForecast)

    private ForecastCommand forecastCommand = new ForecastCommand(forecastClient, repository);

    def cleanup() {
        deleteSnapshotFile();
    }

    def "should update repository with recent forecasts"() {
        given: "repository with some weather forecasts for voivodeship"
        putIntoRepository(forecastForLodzkie, forecastForMazowieckie)
        and: "new forecasts provided by client"
        forecastClient.getVoivodeshipsForecasts(_ as Set<Voivodeship>) >> Lists.newArrayList(newForecastForLodzkie, newForecastForMazowieckie)
        when:
        forecastCommand.udateVoivodeshipForecasts()
        then: "I see i have new Forecasts inside repository record"
        repository.findAllVoivodeshipForecasts().each {
            it.getForecast() == newForecast
        }
        and: "I see repository snapshot saved as json file"
        Files.exists(Paths.get(SNAPSHOT_PATH))
        and: "I see content of snapshot file matches repository"
        getVoivodeshipsForecastsFromSnapshotFile().sort() == repository.findAllVoivodeshipForecasts().sort()
    }

    def "shouldn't update repository if client provided no new forecasts"() {
        given: "repository with some weather forecasts for voivodeship"
        putIntoRepository(forecastForLodzkie, forecastForMazowieckie)
        and: "empty list of forecasts provided by client"
        forecastClient.getVoivodeshipsForecasts(_ as Set<Voivodeship>) >> Collections.emptyList()
        when:
        forecastCommand.udateVoivodeshipForecasts()
        then: "I see i have old Forecasts inside repository record"
        repository.findAllVoivodeshipForecasts().each {
            it.getForecast() == basicForecast
        }
        and: "I see content of snapshot file matches repository"
        getVoivodeshipsForecastsFromSnapshotFile().sort() == repository.findAllVoivodeshipForecasts().sort()
    }

    def "shouldn't update repository with empty forecasts"() {
        given: "repository with some weather forecasts for voivodeship"
        putIntoRepository(forecastForLodzkie, forecastForMazowieckie)
        and: "new forecasts provided by client that contains one missing forecast data"
        newForecastForMazowieckie.setForecast(null)
        forecastClient.getVoivodeshipsForecasts(_ as Set<Voivodeship>) >> Lists.newArrayList(newForecastForLodzkie, newForecastForMazowieckie)
        when:
        forecastCommand.udateVoivodeshipForecasts()
        then: "I see i have new forecastForLodzkie"
        repository.findForecastByVoivodeshipId(lodzkieVoivodeship.id).get() == newForecastForLodzkie
        and: "old forecast for mazowieckie"
        repository.findForecastByVoivodeshipId(mazowieckieVoivodeship.id).get() == forecastForMazowieckie
        and: "I see content of snapshot file matches repository"
        getVoivodeshipsForecastsFromSnapshotFile().sort() == repository.findAllVoivodeshipForecasts().sort()
    }

    void putIntoRepository(VoivodeshipForecast... forecasts) {
        forecasts.each { repository.insert(it) }
    }

    private Forecast getNewForecast() {
        Forecast newForecast = basicForecast
        newForecast.getDailyForecast().each { day -> day.setDate(LocalDate.now()) }
        return newForecast
    }

    private deleteSnapshotFile() {
        Files.deleteIfExists(Paths.get(SNAPSHOT_PATH))
    }

    private List<VoivodeshipForecast> getVoivodeshipsForecastsFromSnapshotFile() {
        return resourceMapper.readForecastsFromSnapshotFile().getVoivodeshipForecasts()
    }

    private ObjectMapper prepareObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .createXmlMapper(false)
                .indentOutput(true)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializers(
                        new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .deserializers(
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .modules(new JavaTimeModule())
                .build();
    }
}
