package com.alura.ebookapi.repository;

import com.alura.ebookapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitleIgnoreCase(String title);

    List<Book> findByLanguage(String language);

    List<Book> findTop10ByOrderByDownloadCountDesc();
}
