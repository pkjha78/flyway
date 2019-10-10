package com.flyway.migration.app.flyway.controller.rest;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flyway.migration.app.flyway.entity.Book;
import com.flyway.migration.app.flyway.repository.BookRepository;

@RestController
public class BookRestController {
	
	@Autowired
	private BookRepository bookRepo;
	
	@GetMapping("/books")
	public ResponseEntity<List<Book>> getAllBooks(){
		//return new ResponseEntity<>(bookRepo.findAll(), HttpStatus.OK);
		return ResponseEntity.ok(bookRepo.findAll());
	}
	
	@GetMapping("/books/{id}")
	public ResponseEntity<Book> getBooksById(@PathVariable Long id){
		Optional<Book> book = bookRepo.findById(id);
		if(!book.isPresent())
			ResponseEntity.badRequest().build();
		//return new ResponseEntity<Book>(bookRepo.findById(id).get(), HttpStatus.OK);
		return ResponseEntity.ok(book.get());
	}
	
	@PostMapping("/books")
	public ResponseEntity<Book> addBook(@RequestBody Book book){
		try {
			bookRepo.save(book);
            return ResponseEntity.ok().build();
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
	}
	
	@PutMapping("/books/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable long id,
			@RequestBody Book book){
		/*
		Book findBook = bookRepo.findById(id).get();
		findBook.setDescription(book.getDescription());
		findBook.setTitle(book.getTitle());
		return new ResponseEntity<Book>(bookRepo.save(findBook), HttpStatus.OK);
		*/
		if(bookRepo.findById(id).isPresent())
			ResponseEntity.badRequest().build();		
		return ResponseEntity.ok(bookRepo.save(book));
	}
	
	@DeleteMapping("/books/{id}")
	public ResponseEntity deleteBook(@PathVariable long id){
		/*bookRepo.deleteById(id);;
		return ResponseEntity.noContent().build();
		*/
		if(bookRepo.findById(id).isPresent())
			ResponseEntity.badRequest().build();
		bookRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}
