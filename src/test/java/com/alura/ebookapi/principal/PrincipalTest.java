package com.alura.ebookapi.principal;

import com.alura.ebookapi.client.GutendexClient;
import com.alura.ebookapi.dto.AuthorData;
import com.alura.ebookapi.dto.BookData;
import com.alura.ebookapi.dto.BookResponse;
import com.alura.ebookapi.model.Author;
import com.alura.ebookapi.model.Book;
import com.alura.ebookapi.repository.AuthorRepository;
import com.alura.ebookapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrincipalTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GutendexClient gutendexClient;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private Principal crearPrincipal(String input) {
        Scanner scanner = new Scanner(input);
        return new Principal(bookRepository, authorRepository, gutendexClient, scanner);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void debeListarLibrosCuandoHayLibros() {
        Author author = new Author();
        author.setName("Austen, Jane");

        Book book = new Book();
        book.setTitle("Pride and Prejudice");
        book.setLanguage("en");
        book.setDownloadCount(49000);
        book.setAuthor(author);

        when(bookRepository.findAll()).thenReturn(List.of(book));

        Principal principal = crearPrincipal("2\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Pride and Prejudice");
    }

    @Test
    void debeMostrarMensajeWhenNoHayLibros() {
        when(bookRepository.findAll()).thenReturn(List.of());

        Principal principal = crearPrincipal("2\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No hay libros registrados aún");
    }

    @Test
    void debeListarAutoresCuandoHayAutores() {
        Author author = new Author();
        author.setName("Austen, Jane");
        author.setBirthYear(1775);
        author.setDeathYear(1817);

        when(authorRepository.findAll()).thenReturn(List.of(author));

        Principal principal = crearPrincipal("3\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Austen, Jane");
    }

    @Test
    void debeMostrarMensajeCuandoNoHayAutores() {
        when(authorRepository.findAll()).thenReturn(List.of());

        Principal principal = crearPrincipal("3\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No hay autores registrados aún");
    }

    @Test
    void debeBuscarYGuardarLibroNuevo() throws Exception {
        AuthorData authorData = new AuthorData();
        authorData.setName("Austen, Jane");
        authorData.setBirthYear(1775);
        authorData.setDeathYear(1817);

        BookData bookData = new BookData();
        bookData.setTitle("Pride and Prejudice");
        bookData.setAuthors(List.of(authorData));
        bookData.setLanguages(List.of("en"));
        bookData.setDownloadCount(49000);

        BookResponse response = new BookResponse();
        response.setCount(1);
        response.setResults(List.of(bookData));

        when(gutendexClient.buscarLibros(anyString())).thenReturn(response);
        when(bookRepository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
        when(authorRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        Author savedAuthor = new Author();
        savedAuthor.setName("Austen, Jane");
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        Book savedBook = new Book();
        savedBook.setTitle("Pride and Prejudice");
        savedBook.setLanguage("en");
        savedBook.setDownloadCount(49000);
        savedBook.setAuthor(savedAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Principal principal = crearPrincipal("1\nPride and Prejudice\n0\n");
        principal.exibirMenu();

        verify(bookRepository).save(any(Book.class));
        assertThat(getOutput()).contains("guardado exitosamente");
    }

    @Test
    void debeMostrarMensajeSiLibroYaExiste() throws Exception {
        AuthorData authorData = new AuthorData();
        authorData.setName("Austen, Jane");

        BookData bookData = new BookData();
        bookData.setTitle("Pride and Prejudice");
        bookData.setAuthors(List.of(authorData));
        bookData.setLanguages(List.of("en"));
        bookData.setDownloadCount(49000);

        BookResponse response = new BookResponse();
        response.setCount(1);
        response.setResults(List.of(bookData));

        when(gutendexClient.buscarLibros(anyString())).thenReturn(response);
        when(bookRepository.existsByTitleIgnoreCase(anyString())).thenReturn(true);

        Principal principal = crearPrincipal("1\nPride and Prejudice\n0\n");
        principal.exibirMenu();

        verify(bookRepository, never()).save(any());
        assertThat(getOutput()).contains("ya está registrado");
    }

    @Test
    void debeMostrarMensajeSiNoSeEncuentraLibroEnApi() throws Exception {
        BookResponse response = new BookResponse();
        response.setCount(0);
        response.setResults(List.of());

        when(gutendexClient.buscarLibros(anyString())).thenReturn(response);

        Principal principal = crearPrincipal("1\nLibro Inexistente\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No se encontró ningún libro");
    }

    @Test
    void debeMostrarErrorSiTituloEsBlanco() {
        Principal principal = crearPrincipal("1\n   \n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("título no puede estar vacío");
        verifyNoInteractions(gutendexClient);
    }

    @Test
    void debeListarAutoresVivosEnAnio() {
        Author austen = new Author();
        austen.setName("Austen, Jane");
        austen.setBirthYear(1775);
        austen.setDeathYear(1817);

        when(authorRepository.findByBirthYearLessThanEqual(1800))
                .thenReturn(List.of(austen));

        Principal principal = crearPrincipal("4\n1800\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Austen, Jane");
    }

    @Test
    void debeMostrarErrorSiAnioInvalido() {
        Principal principal = crearPrincipal("4\nabc\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Año inválido");
        verifyNoInteractions(authorRepository);
    }

    @Test
    void debeListarLibrosPorIdioma() {
        Book book = new Book();
        book.setTitle("Pride and Prejudice");
        book.setLanguage("en");

        when(bookRepository.findByLanguage("en")).thenReturn(List.of(book));

        Principal principal = crearPrincipal("5\nen\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Pride and Prejudice");
        assertThat(getOutput()).contains("Cantidad de libros en idioma 'en': 1");
    }

    @Test
    void debeMostrarMensajeSiNoHayLibrosEnIdioma() {
        when(bookRepository.findByLanguage(anyString())).thenReturn(List.of());

        Principal principal = crearPrincipal("5\nfr\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No hay libros registrados en ese idioma");
    }

    @Test
    void debeGenerarEstadisticasDeDescargas() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setDownloadCount(10000);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setDownloadCount(5000);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        Principal principal = crearPrincipal("6\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Estadísticas de descargas");
        assertThat(getOutput()).contains("Total de libros");
    }

    @Test
    void debeMostrarMensajeSiNoHayLibrosParaEstadisticas() {
        when(bookRepository.findAll()).thenReturn(List.of());

        Principal principal = crearPrincipal("6\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No hay libros registrados para generar estadísticas");
    }

    @Test
    void debeListarTop10LibrosMasDescargados() {
        Book book = new Book();
        book.setTitle("Top Book");
        book.setDownloadCount(99999);

        when(bookRepository.findTop10ByOrderByDownloadCountDesc()).thenReturn(List.of(book));

        Principal principal = crearPrincipal("7\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Top Book");
    }

    @Test
    void debeBuscarAutorPorNombre() {
        Author author = new Author();
        author.setName("Austen, Jane");

        when(authorRepository.findByNameContainingIgnoreCase("Austen")).thenReturn(List.of(author));

        Principal principal = crearPrincipal("8\nAusten\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Austen, Jane");
    }

    @Test
    void debeMostrarMensajeSiAutorNoEncontrado() {
        when(authorRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of());

        Principal principal = crearPrincipal("8\nNombre Inexistente\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("No se encontraron autores con ese nombre");
    }

    @Test
    void debeListarAutoresPorRangoDeAnios() {
        Author author = new Author();
        author.setName("Dickens, Charles");
        author.setBirthYear(1812);

        when(authorRepository.findByBirthYearBetween(1800, 1850)).thenReturn(List.of(author));

        Principal principal = crearPrincipal("9\n1800\n1850\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Dickens, Charles");
    }

    @Test
    void debeMostrarMensajeDeHastaLuego() {
        Principal principal = crearPrincipal("0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("¡Hasta luego!");
    }

    @Test
    void debeIgnorarOpcionInvalida() {
        Principal principal = crearPrincipal("99\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Opción inválida");
    }

    @Test
    void debeManjarEntradaNoNumerica() {
        Principal principal = crearPrincipal("abc\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("número válido");
    }

    @Test
    void debeMostrarErrorDeConexionSiApiFalla() throws Exception {
        when(gutendexClient.buscarLibros(anyString()))
                .thenThrow(new IOException("Connection refused"));

        Principal principal = crearPrincipal("1\nalgún libro\n0\n");
        principal.exibirMenu();

        assertThat(getOutput()).contains("Error de conexión");
    }
}
