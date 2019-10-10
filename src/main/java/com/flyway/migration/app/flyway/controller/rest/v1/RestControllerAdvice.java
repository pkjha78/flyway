package com.flyway.migration.app.flyway.controller.rest.v1;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.flyway.migration.app.flyway.exception.BookAlreadyExistsException;
import com.flyway.migration.app.flyway.exception.BookNotFoundException;
import com.flyway.migration.app.flyway.exception.ErrorDetails;

@ControllerAdvice
@RequestMapping(produces = "application/json")
@RestController
public class RestControllerAdvice {

    @ResponseBody
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorDetails bookNotFoundExceptionHandler(BookNotFoundException ex,  WebRequest request) {
        return new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(true));        
    }

    @ResponseBody
    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorDetails bookIsbnAlreadyExistsExceptionHandler(BookAlreadyExistsException ex, WebRequest request) {
        return new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(true));
    }
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
      ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
          request.getDescription(false));
      return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
