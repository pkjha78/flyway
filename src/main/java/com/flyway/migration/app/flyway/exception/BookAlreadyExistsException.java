package com.flyway.migration.app.flyway.exception;

public class BookAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookAlreadyExistsException(Long id) {
		super("book already exists for ID: '" + id + "'");
	}
}
