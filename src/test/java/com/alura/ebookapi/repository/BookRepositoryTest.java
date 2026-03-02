package com.alura.ebookapi.repository;

import com.alura.ebookapi.model.Author;
import com.alura.ebookapi.model.Book;
import com.alura.ebookapi.principal.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookRepositoryTest {

    @MockitoBean
    Principal principal;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author austen;

    @BeforeEach
    void setUp() {
        austen = new Author();
        austen.setName("Austen, Jane");
        austen.setBirthYear(1775);
        austen.setDeathYear(1817);
        authorRepository.save(austen);

        Author dickens = new Author();
        dickens.setName("Dickens, Charles");
        dickens.setBirthYear(1812);
        dickens.setDeathYear(1870);
        authorRepository.save(dickens);

        Book book1 = new Book();
        book1.setTitle("Pride and Prejudice");
        book1.setLanguage("en");
        book1.setDownloadCount(49000);
        book1.setAuthor(austen);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Sense and Sensibility");
        book2.setLanguage("en");
        book2.setDownloadCount(18000);
        book2.setAuthor(austen);
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("Oliver Twist");
        book3.setLanguage("en");
        book3.setDownloadCount(12000);
        book3.setAuthor(dickens);
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setTitle("Don Quijote");
        book4.setLanguage("es");
        book4.setDownloadCount(25000);
        book4.setAuthor(austen);
        bookRepository.save(book4);
    }

    @Test
    void debeVerificarSiLibroExistePorTitulo() {
        assertThat(bookRepository.existsByTitleIgnoreCase("Pride and Prejudice")).isTrue();
        assertThat(bookRepository.existsByTitleIgnoreCase("pride and prejudice")).isTrue();
        assertThat(bookRepository.existsByTitleIgnoreCase("Libro Inexistente")).isFalse();
    }

    @Test
    void debeListarLibrosPorIdioma() {
        List<Book> librosEn = bookRepository.findByLanguage("en");
        List<Book> librosEs = bookRepository.findByLanguage("es");
        List<Book> librosFr = bookRepository.findByLanguage("fr");

        assertThat(librosEn).hasSize(3);
        assertThat(librosEs).hasSize(1);
        assertThat(librosFr).isEmpty();
    }

    @Test
    void debeListarTop10LibrosMasDescargados() {
        List<Book> top10 = bookRepository.findTop10ByOrderByDownloadCountDesc();

        assertThat(top10).hasSize(4);
        assertThat(top10.get(0).getTitle()).isEqualTo("Pride and Prejudice");
        assertThat(top10.get(1).getTitle()).isEqualTo("Don Quijote");
        assertThat(top10.get(2).getTitle()).isEqualTo("Sense and Sensibility");
        assertThat(top10.get(3).getTitle()).isEqualTo("Oliver Twist");
    }

    @Test
    void debeRetornarMaximo10EnTop10() {
        for (int i = 1; i <= 8; i++) {
            Book extra = new Book();
            extra.setTitle("Extra Book " + i);
            extra.setLanguage("en");
            extra.setDownloadCount(i * 100);
            extra.setAuthor(austen);
            bookRepository.save(extra);
        }

        List<Book> top10 = bookRepository.findTop10ByOrderByDownloadCountDesc();

        assertThat(top10).hasSize(10);
    }

    @Test
    void debePersistirLibroConAutor() {
        Author autor = new Author();
        autor.setName("Homer");
        autor.setBirthYear(-800);
        authorRepository.save(autor);

        Book libro = new Book();
        libro.setTitle("The Iliad");
        libro.setLanguage("en");
        libro.setDownloadCount(5000);
        libro.setAuthor(autor);
        bookRepository.save(libro);

        assertThat(bookRepository.existsByTitleIgnoreCase("The Iliad")).isTrue();
        assertThat(bookRepository.findByLanguage("en")).anyMatch(b -> b.getTitle().equals("The Iliad"));
    }
}
