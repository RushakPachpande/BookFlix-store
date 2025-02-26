
package com.bookflix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookflix.models.Book;

public interface BooksRepository extends JpaRepository<Book, Integer> {

}
