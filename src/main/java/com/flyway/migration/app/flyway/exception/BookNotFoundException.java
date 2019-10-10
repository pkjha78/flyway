package com.flyway.migration.app.flyway.exception;

public class BookNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public BookNotFoundException(Long id) {
		super("could not find book with ID: '" + id + "'");
	}
}
