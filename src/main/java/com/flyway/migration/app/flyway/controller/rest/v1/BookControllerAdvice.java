package com.flyway.migration.app.flyway.controller.rest.v1;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.flyway.migration.app.flyway.exception.BookAlreadyExistsException;
import com.flyway.migration.app.flyway.exception.BookNotFoundException;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
public class BookControllerAdvice {

	@ResponseBody
	@ExceptionHandler(BookNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	VndErrors bookNotFoundExceptionHandler(BookNotFoundException ex) {
		return new VndErrors("error", ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(BookAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	VndErrors bookIsbnAlreadyExistsExceptionHandler(BookAlreadyExistsException ex) {
		return new VndErrors("error", ex.getMessage());
	}
}