package io.bierzan.fivedayforecast.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpError {

    private int code;
    private String status;
    private LocalDateTime time;
    private String message;

    public static HttpError of(HttpStatus status, String message) {
        return new HttpError(status.value(),status.getReasonPhrase(), LocalDateTime.now(), message);
    }
}