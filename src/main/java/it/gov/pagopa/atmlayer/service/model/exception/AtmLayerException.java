package it.gov.pagopa.atmlayer.service.model.exception;

import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * Base exception for PDF receipts service exceptions
 */
@Getter
public class AtmLayerException extends Exception {

    /**
     * Error code of this exception
     * -- GETTER --
     * Returns error code
     *
     * @return Error code of this exception
     */
    private final AppErrorCodeEnum errorCode;

    /**
     * Constructs new exception with provided error code and message
     *
     * @param errorCode Error code
     * @param message   Detail message
     */
    public AtmLayerException(AppErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
    }

    /**
     * Constructs new exception with provided error code, message and cause
     *
     * @param errorCode Error code
     * @param message   Detail message
     * @param cause     Exception causing the constructed one
     */
    public AtmLayerException(AppErrorCodeEnum errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode);
    }

}