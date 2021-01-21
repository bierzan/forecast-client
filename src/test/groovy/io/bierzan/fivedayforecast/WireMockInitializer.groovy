package io.bierzan.fivedayforecast

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent

class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        WireMockServer wireMockServer = new WireMockServer(
                new WireMockConfiguration().dynamicPort()
                        .asynchronousResponseEnabled(true)
        );
        wireMockServer.start();
        applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);
        applicationContext.addApplicationListener(
                { applicationEvent ->
                    if (applicationEvent instanceof ContextClosedEvent) {
                        wireMockServer.stop();
                    }
                });

        TestPropertyValues
                .of("host:http://localhost:" + wireMockServer.port())
                .applyTo(applicationContext);

    }
}
