package io.bierzan.fivedayforecast.samples

import groovy.transform.CompileStatic
import io.bierzan.fivedayforecast.forecast.dto.Day
import io.bierzan.fivedayforecast.forecast.dto.Forecast
import org.assertj.core.util.Lists

import java.time.LocalDate

@CompileStatic
trait SampleForecasts {
    Forecast basicForecast = makeBasicForecast()

    Forecast makeBasicForecast(){
        Forecast forecast = new Forecast()
        Day day = new Day()
        day.setDate(LocalDate.MIN)
        forecast.setDailyForecast(Lists.newArrayList(day))
        return forecast
    }
}