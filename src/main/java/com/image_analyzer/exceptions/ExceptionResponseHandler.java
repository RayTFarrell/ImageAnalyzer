package com.image_analyzer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@ControllerAdvice
public class ExceptionResponseHandler {

    @ExceptionHandler(ImageProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleControllerException(
            ImageProcessingException imageProcessingException,
            WebRequest request) {
        return buildErrorResponse(imageProcessingException, imageProcessingException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);

    }
    @ExceptionHandler(InvalidParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIOException(
            InvalidParametersException invalidParametersException,
            WebRequest request) {
        return buildErrorResponse(invalidParametersException, invalidParametersException.getMessage(), HttpStatus.BAD_REQUEST, request);

    }
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIOException(
            IOException ioException,
            WebRequest request) {
        return buildErrorResponse(ioException, ioException.getMessage(), HttpStatus.BAD_REQUEST, request);

    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                exception.getMessage()
        );
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
