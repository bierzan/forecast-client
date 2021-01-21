package io.bierzan.fivedayforecast.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bierzan.fivedayforecast.forecast.exception.PostalCodeValidationException;
import io.bierzan.fivedayforecast.forecast.exception.ForecastNotFound;
import io.bierzan.fivedayforecast.forecast.exception.VoivodeshipNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Order(-2)
@RequiredArgsConstructor
public class ExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (throwable instanceof ForecastNotFound ||
                throwable instanceof VoivodeshipNotFound) {
            return handleVoivodeshipForecastNotFoundError(throwable, response);
        }

        if (throwable instanceof PostalCodeValidationException) {
            return handlePostalCodeValidationException(throwable, response);
        }

        return handleUnknownError(response);
    }

    private Mono<Void> handleVoivodeshipForecastNotFoundError(Throwable throwable, ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        DataBufferFactory bufferFactory = response.bufferFactory();
        HttpError httpError = HttpError.of(HttpStatus.NOT_FOUND, throwable.getMessage());
        DataBuffer dataBuffer = putHttpErrorIntoDataBuffer(bufferFactory, httpError);
        return response.writeWith(Mono.just(dataBuffer));
    }

    private Mono<Void> handlePostalCodeValidationException(Throwable throwable, ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        DataBufferFactory bufferFactory = response.bufferFactory();
        HttpError httpError = HttpError.of(HttpStatus.BAD_REQUEST, throwable.getMessage());
        DataBuffer dataBuffer = putHttpErrorIntoDataBuffer(bufferFactory, httpError);
        return response.writeWith(Mono.just(dataBuffer));
    }

    private Mono<Void> handleUnknownError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer dataBuffer;
        HttpError httpError = HttpError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");
        dataBuffer = putHttpErrorIntoDataBuffer(bufferFactory, httpError);
        return response.writeWith(Mono.just(dataBuffer));
    }

    private DataBuffer putHttpErrorIntoDataBuffer(DataBufferFactory bufferFactory, HttpError httpError) {
        DataBuffer dataBuffer;
        try {
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(httpError));
        } catch (JsonProcessingException e) {
            dataBuffer = bufferFactory.wrap("".getBytes());
        }
        return dataBuffer;
    }


}