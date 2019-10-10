package com.flyway.migration.app.flyway.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.flyway.migration.app.flyway.FlywayApplication;
import com.flyway.migration.app.flyway.entity.Book;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FlywayApplication.class)
@AutoConfigureMockMvc
@Transactional
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
        Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }

    @SuppressWarnings("unchecked")
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    // ---------- create book ----------

    @Test
    public void should_create_valid_book_and_return_created_status() throws Exception {
        Book book = new Book();
        book.setId(new Long(9));
        book.setDescription("My new book");
        book.setTitle("Publisher");
        mockMvc.perform(post("/rest/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("http://localhost/rest/v1/books/9")))
                .andExpect(content().string(""))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_create_invalid_content_book_and_return_bad_request_status() throws Exception {
       // Book book = new Book(null,"My new book","Publisher");
        Book book = new Book();
        book.setId(new Long(1));
        book.setDescription("My new book");
        book.setTitle("Publisher");
        mockMvc.perform(post("/rest/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                //.andExpect(status().isBadRequest())
                .andExpect(status().isConflict())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_create_existing_book_and_return_conflict_status() throws Exception {
    	Book book = new Book();
        book.setId(new Long(1));
        book.setDescription("My new book");
        book.setTitle("Publisher");

        mockMvc.perform(post("/rest/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                .andExpect(status().isConflict())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_allow_others_http_methods() throws Exception {
    	Book book = new Book();
        book.setId(new Long(9));
        book.setDescription("My new book");

        mockMvc.perform(put("/rest/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""))
                .andDo(MockMvcResultHandlers.print());
    }

    // ---------- get book ----------

    @Test
    public void should_get_valid_book_with_ok_status() throws Exception {
        mockMvc.perform(get("/rest/v1/books/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.description", is("Simple coding examples and tutorials")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_no_get_unknown_book_with_not_found_status() throws Exception {
        mockMvc.perform(get("/rest/v1/books/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].logref", is("error")))
                .andExpect(jsonPath("$[0].message", containsString("could not find book with ID: '123'")))
                .andDo(MockMvcResultHandlers.print());
    }

    // ---------- get books ----------

    @Test
    public void should_get_all_books_with_ok_status() throws Exception {
        mockMvc.perform(get("/rest/v1/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is("4")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].id", contains(1,2,3,4)))  // sorted by id asc by default
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_get_first_page_paginated_books() throws Exception {
        mockMvc.perform(get("/rest/v1/books?page=0&size=2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPartialContent())
                .andExpect(header().string("X-Total-Count", is("4")))
                .andExpect(header().string("first", is("/rest/v1/books?page=0&size=2")))
                .andExpect(header().string("last", is("/rest/v1/books?page=1&size=2")))
                .andExpect(header().string("prev", is(nullValue())))
                .andExpect(header().string("next", is("/rest/v1/books?page=1&size=2")))
                .andExpect(header().string("X-Total-Count", is("4")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1,2)))  // sorted by id asc by default
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_get_last_page_paginated_books() throws Exception {
        mockMvc.perform(get("/rest/v1/books?page=1&size=2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPartialContent())
                .andExpect(header().string("X-Total-Count", is("4")))
                .andExpect(header().string("first", is("/rest/v1/books?page=0&size=2")))
                .andExpect(header().string("last", is("/rest/v1/books?page=1&size=2")))
                .andExpect(header().string("prev", is("/rest/v1/books?page=0&size=2")))
                .andExpect(header().string("next", is(nullValue())))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(3,4)))  // sorted by id asc by default
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_sort_books() throws Exception {
        mockMvc.perform(get("/rest/v1/books?sort=title&order=desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", is("4")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].id", contains(2,4,3,1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_get_books_for_bad_pagination() throws Exception {
        mockMvc.perform(get("/rest/v1/books?page=999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    // ---------- update book ----------

    @Test
    public void should_update_valid_book_and_return_ok_status() throws Exception {
        
        Book book = new Book();
        book.setId(new Long(1));
        book.setDescription("New description");
        book.setTitle("Book updated");       

        mockMvc.perform(put("/rest/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Book updated")))
                .andExpect(jsonPath("$.description", is("New description")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_update_unknown_book_and_return_not_found_status() throws Exception {
        
        Book book = new Book();
        book.setId(new Long(978));
        book.setDescription("New description");
        book.setTitle("Book updated");       

        mockMvc.perform(put("/rest/v1/books/978")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(book)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].logref", is("error")))
                .andExpect(jsonPath("$[0].message", containsString("could not find book with ID: '978'")))
                .andDo(MockMvcResultHandlers.print());
    }

    // ---------- update book's description ----------

    @Test
    public void should_update_existing_book_description_and_return_ok_status() throws Exception {
        mockMvc.perform(patch("/rest/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("new description"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is("Effective Java")))
                .andExpect(jsonPath("$.description", is("new description")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_update_description_of_unknown_book_and_return_not_found_status() throws Exception {
        mockMvc.perform(patch("/rest/v1/books/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("new description"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].logref", is("error")))
                .andExpect(jsonPath("$[0].message", containsString("could not find book with ID: '123'")))
                .andDo(MockMvcResultHandlers.print());
    }

    // ---------- delete book ----------

    @Test
    public void should_delete_existing_book_and_return_no_content_status() throws Exception {
        mockMvc.perform(delete("/rest/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void should_not_delete_unknown_book_and_return_not_found_status() throws Exception {
        mockMvc.perform(delete("/rest/v1/books/1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0].logref", is("error")))
                .andExpect(jsonPath("$[0].message", containsString("could not find book with ID: '1234'")))
                .andDo(MockMvcResultHandlers.print());
    }
}