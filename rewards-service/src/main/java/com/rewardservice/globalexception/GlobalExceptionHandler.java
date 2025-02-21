package com.rewardservice.globalexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RewardServiceException and returns a NOT_FOUND response.
     *
     * @param ex the RewardServiceException to handle
     * @return a ResponseEntity containing the exception message and a NOT_FOUND status
     */
    @ExceptionHandler(RewardServiceException.class)
    public ResponseEntity<String> handleIllegalArgumentsException(RewardServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException and returns a BAD_REQUEST response.
     *
     * @param ex the IllegalArgumentException
     * @return a ResponseEntity with the exception message and BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentsException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param ex the Exception
     * @return a ResponseEntity with the exception message and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
