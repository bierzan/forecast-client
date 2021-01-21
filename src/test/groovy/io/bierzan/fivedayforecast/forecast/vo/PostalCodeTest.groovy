package io.bierzan.fivedayforecast.forecast.vo

import io.bierzan.fivedayforecast.forecast.exception.PostalCodeValidationException
import spock.lang.Specification

import static org.assertj.core.api.Assertions.catchThrowable

class PostalCodeTest extends Specification {


    public static final String POSTAL_CODE_VALUE = "00-000"

    def "should create PostalCode"() {
        when:
        def postalCode = PostalCode.of(POSTAL_CODE_VALUE)
        then:
        postalCode.value() == POSTAL_CODE_VALUE
    }

    def "should throw exception for invalid postal code value"() {
        def invalidCode = "xxx"
        when:
        Throwable exception = catchThrowable({ ->
            PostalCode.of(invalidCode)
        })
        then:
        exception instanceof PostalCodeValidationException
        exception.getMessage() == "Requested postal code is not valid: [" + invalidCode + "]"
    }

    def "should throw exception for null postal code value"() {
        when:
        Throwable exception = catchThrowable({ ->
            PostalCode.of(null)
        })
        then:
        exception instanceof PostalCodeValidationException
        exception.getMessage() == "Requested postal code is not valid: [" + null + "]"
    }
}
