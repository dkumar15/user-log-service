package com.starhealth.login.exceptions;


import com.starhealth.login.payloads.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                        WebRequest webRequest) {
        return getErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false), HttpStatus.NOT_FOUND);
//        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
//                webRequest.getDescription(false));
//        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorDetails> invalidEmailException(UsernameNotFoundException exception, WebRequest webRequest) {
        return getErrorDetails(new Date(), exception.getMessage(),  webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> userNotFoundException(UsernameNotFoundException exception, WebRequest webRequest) {
        return getErrorDetails(new Date(), exception.getMessage(),  webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationAPIException.class)
    public ResponseEntity<ErrorDetails> authenticationAPIException(AuthenticationAPIException exception,
                                                                   WebRequest webRequest) {
        return getErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception,
                                                                    WebRequest webRequest) {

        return getErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception,
                                                              WebRequest webRequest) {
        return getErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDetails> getErrorDetails(Date date, String message, String description, HttpStatus httpStatus) {
        ErrorDetails errorDetails = new ErrorDetails(date, message, description);
        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}

