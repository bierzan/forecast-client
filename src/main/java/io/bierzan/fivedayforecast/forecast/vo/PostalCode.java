package io.bierzan.fivedayforecast.forecast.vo;

import io.bierzan.fivedayforecast.forecast.exception.PostalCodeValidationException;

public final class PostalCode {
    private final String value;

    public static PostalCode of(String postalCode) {

        if (isValid(postalCode)) {
            return new PostalCode(postalCode);
        }
        throw new PostalCodeValidationException(
                "Requested postal code is not valid: [" + postalCode + "]"
        );
    }

    private static boolean isValid(String postalCode) {
        return postalCode != null &&
                postalCode.matches("\\d{2}-\\d{3}");
    }

    private PostalCode(String value) {
        this.value = value;
    }

    public String getPrefix() {
        return value.substring(0, 4);
    }

    public String value() {
        return value;
    }
}
