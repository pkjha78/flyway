package com.flyway.migration.app.flyway.controller.rest.v1;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.flyway.migration.app.flyway.entity.Book;
import com.flyway.migration.app.flyway.exception.BookAlreadyExistsException;
import com.flyway.migration.app.flyway.exception.BookNotFoundException;
import com.flyway.migration.app.flyway.repository.BookRepository;

@RestController
@RequestMapping(value = "/rest/v1")
public class BookRestControllerV1 {
	
	private static final int MAX_PAGE_SIZE = 50;
	
	@Autowired
	private BookRepository bookRepository;
	
	@GetMapping("/books")
	public ResponseEntity<List<Book>> getAllBooks(
			 @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable,
			 @RequestParam(required = false, defaultValue = "id") String sort,
	         @RequestParam(required = false, defaultValue = "asc") String order){
		final PageRequest pr = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("asc" .equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC, sort)
        );

        Page<Book> booksPage = bookRepository.findAll(pr);

        if (booksPage.getContent().isEmpty()) {
            return new ResponseEntity<List<Book>>(HttpStatus.NO_CONTENT);
        } else {
            long totalBooks = booksPage.getTotalElements();
            int nbPageBooks = booksPage.getNumberOfElements();

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(totalBooks));

            if (nbPageBooks < totalBooks) {
                headers.add("first", buildPageUri(PageRequest.of(0, booksPage.getSize())));
                headers.add("last", buildPageUri(PageRequest.of(booksPage.getTotalPages() - 1, booksPage.getSize())));

                if (booksPage.hasNext()) {
                    headers.add("next", buildPageUri(booksPage.nextPageable()));
                }

                if (booksPage.hasPrevious()) {
                    headers.add("prev", buildPageUri(booksPage.previousPageable()));
                }

                return new ResponseEntity<>(booksPage.getContent(), headers, HttpStatus.PARTIAL_CONTENT);
            } else {
                return new ResponseEntity<List<Book>>(booksPage.getContent(), headers, HttpStatus.OK);
            }
        }
	}
	
	@GetMapping("/books/{id}")
	public ResponseEntity<Book> getBooksById(@PathVariable Long id){
		return bookRepository.findById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseThrow(() -> new BookNotFoundException(id));
	}
	
	@PostMapping("/books")
	public ResponseEntity<Book> addBook(@RequestBody Book book, UriComponentsBuilder ucBuilder){
		if (bookRepository.findById(book.getId()).isPresent()) {
            throw new BookAlreadyExistsException(book.getId());
        }
        bookRepository.save(book);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/rest/v1/books/{id}").buildAndExpand(book.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}
	
	@PutMapping("/books/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable long id,
			@RequestBody Book vbook){
		return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.save(vbook);
                    return new ResponseEntity<>(book, HttpStatus.OK);
                })
                .orElseThrow(() -> new BookNotFoundException(id));		
	}
	
	@PatchMapping("/books/{id}")
    public ResponseEntity<Book> updateBookDescription(@PathVariable Long id, @RequestBody String description) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setDescription(description);
                    bookRepository.save(book);

                    return new ResponseEntity<>(book, HttpStatus.OK);
                })
                .orElseThrow(() -> new BookNotFoundException(id));
    }
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("/books/{id}")
	public ResponseEntity<?> deleteBook(@PathVariable long id){
		 return bookRepository.findById(id)
	                .map(book -> {
	                    bookRepository.delete(book);
	                    return new ResponseEntity(HttpStatus.NO_CONTENT);
	                })
	                .orElseThrow(() -> new BookNotFoundException(id));
	}

	private String buildPageUri(Pageable page) {
        return fromUriString("/rest/v1/books")
                .query("page={page}&size={size}")
                .buildAndExpand(page.getPageNumber(), page.getPageSize())
                .toUriString();
    }
}
