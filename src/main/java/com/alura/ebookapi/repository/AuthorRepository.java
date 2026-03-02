package com.alura.ebookapi.repository;

import com.alura.ebookapi.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByNameIgnoreCase(String name);

    List<Author> findByBirthYearLessThanEqual(Integer year);

    List<Author> findByNameContainingIgnoreCase(String name);

    List<Author> findByBirthYearBetween(Integer start, Integer end);
}
