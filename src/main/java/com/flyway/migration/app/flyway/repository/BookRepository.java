package com.flyway.migration.app.flyway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flyway.migration.app.flyway.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
