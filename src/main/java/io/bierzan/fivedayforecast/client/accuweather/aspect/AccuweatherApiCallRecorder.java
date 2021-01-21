package io.bierzan.fivedayforecast.client.accuweather.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AccuweatherApiCallRecorder {
    private final Counter counter;

    @After("@annotation(AccuweatherLocationCall)")
    public void addLocationCall() {
        counter.addLocationCall();
    }

    @After("@annotation(AccuweatherForecastCall)")
    public void addForecastCall() {
        counter.addForecastCall();
    }
}
