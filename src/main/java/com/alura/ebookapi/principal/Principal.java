package com.alura.ebookapi.principal;

import com.alura.ebookapi.client.GutendexClient;
import com.alura.ebookapi.dto.AuthorData;
import com.alura.ebookapi.dto.BookData;
import com.alura.ebookapi.dto.BookResponse;
import com.alura.ebookapi.model.Author;
import com.alura.ebookapi.model.Book;
import com.alura.ebookapi.repository.AuthorRepository;
import com.alura.ebookapi.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;

@Service
public class Principal {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GutendexClient client;
    private final Scanner scanner = new Scanner(System.in);

    public Principal(BookRepository bookRepository, AuthorRepository authorRepository, GutendexClient client) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.client = client;
    }

    public void exibirMenu() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("""
                    \n========== LiterAlura ==========
                    1 - Buscar libro por título
                    2 - Listar todos los libros
                    3 - Listar autores
                    4 - Listar autores vivos en un año
                    5 - Listar libros por idioma
                    6 - Estadísticas de descargas
                    7 - Top 10 libros más descargados
                    8 - Buscar autor por nombre
                    9 - Listar autores por rango de años
                    0 - Salir
                    =================================
                    """);
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
                continue;
            }

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivosEnAnio();
                case 5 -> listarLibrosPorIdioma();
                case 6 -> generarEstadisticas();
                case 7 -> listarTop10LibrosMasDescargados();
                case 8 -> buscarAutorPorNombre();
                case 9 -> listarAutoresPorRangoDeAnios();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }

    private void buscarLibro() {
        System.out.print("Ingresa el título del libro a buscar: ");
        String titulo = scanner.nextLine().trim();

        if (titulo.isBlank()) {
            System.out.println("El título no puede estar vacío.");
            return;
        }

        try {
            BookResponse response = client.buscarLibros(titulo);

            if (response.getResults().isEmpty()) {
                System.out.println("No se encontró ningún libro con ese título en la API.");
                return;
            }

            BookData bookData = response.getResults().get(0);

            if (bookRepository.existsByTitleIgnoreCase(bookData.getTitle())) {
                System.out.println("El libro '" + bookData.getTitle() + "' ya está registrado.");
                return;
            }

            if (bookData.getAuthors().isEmpty()) {
                System.out.println("El libro no tiene autor registrado en la API.");
                return;
            }

            if (bookData.getLanguages() == null || bookData.getLanguages().isEmpty()) {
                System.out.println("El libro no tiene idioma registrado en la API.");
                return;
            }

            AuthorData authorData = bookData.getAuthors().get(0);
            Author author = authorRepository.findByNameIgnoreCase(authorData.getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        newAuthor.setName(authorData.getName());
                        newAuthor.setBirthYear(authorData.getBirthYear());
                        newAuthor.setDeathYear(authorData.getDeathYear());
                        return authorRepository.save(newAuthor);
                    });

            Book book = new Book();
            book.setTitle(bookData.getTitle());
            book.setLanguage(bookData.getLanguages().get(0));
            book.setDownloadCount(bookData.getDownloadCount());
            book.setAuthor(author);
            bookRepository.save(book);

            System.out.println("\nLibro guardado exitosamente:");
            System.out.println(book);

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("La búsqueda fue interrumpida.");
            Thread.currentThread().interrupt();
        }
    }

    private void listarLibros() {
        List<Book> libros = bookRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
            return;
        }

        System.out.println("\n===== Libros registrados =====");
        libros.forEach(System.out::println);
    }

    private void listarAutores() {
        List<Author> autores = authorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados aún.");
            return;
        }

        System.out.println("\n===== Autores registrados =====");
        autores.forEach(System.out::println);
    }

    private void listarAutoresVivosEnAnio() {
        System.out.print("Ingresa el año a consultar: ");
        String input = scanner.nextLine().trim();

        if (input.isBlank()) {
            System.out.println("El año no puede estar vacío.");
            return;
        }

        try {
            int anio = Integer.parseInt(input);

            if (anio < 0 || anio > 2026) {
                System.out.println("Por favor, ingresa un año válido (entre 0 y 2026).");
                return;
            }

            List<Author> autores = authorRepository.findByBirthYearLessThanEqual(anio)
                    .stream()
                    .filter(a -> a.getDeathYear() == null || a.getDeathYear() >= anio)
                    .toList();

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio + ".");
                return;
            }

            System.out.println("\n===== Autores vivos en " + anio + " (" + autores.size() + ") =====");
            autores.forEach(System.out::println);

        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Por favor, ingresa solo números enteros.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Idiomas disponibles:
                  en - Inglés
                  es - Español
                  fr - Francés
                  pt - Portugués
                """);
        System.out.print("Ingresa el código del idioma: ");
        String idioma = scanner.nextLine().trim().toLowerCase();

        if (idioma.isBlank()) {
            System.out.println("El idioma no puede estar vacío.");
            return;
        }

        List<Book> libros = bookRepository.findByLanguage(idioma);

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en ese idioma.");
            return;
        }

        System.out.println("\nCantidad de libros en idioma '" + idioma + "': " + libros.size());
        System.out.println("===== Libros en idioma '" + idioma + "' =====");
        libros.forEach(System.out::println);
    }

    private void generarEstadisticas() {
        List<Book> libros = bookRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados para generar estadísticas.");
            return;
        }

        DoubleSummaryStatistics stats = libros.stream()
                .filter(b -> b.getDownloadCount() != null)
                .mapToDouble(Book::getDownloadCount)
                .summaryStatistics();

        System.out.println("""

                ===== Estadísticas de descargas =====
                Total de libros  : %d
                Total descargas  : %,.0f
                Promedio         : %,.2f
                Máximo           : %,.0f
                Mínimo           : %,.0f
                =====================================
                """.formatted(
                stats.getCount(),
                stats.getSum(),
                stats.getAverage(),
                stats.getMax(),
                stats.getMin()
        ));
    }

    private void listarTop10LibrosMasDescargados() {
        List<Book> top10 = bookRepository.findTop10ByOrderByDownloadCountDesc();

        if (top10.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
            return;
        }

        System.out.println("\n===== Top 10 libros más descargados =====");
        top10.forEach(System.out::println);
    }

    private void buscarAutorPorNombre() {
        System.out.print("Ingresa el nombre del autor a buscar: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isBlank()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        List<Author> autores = authorRepository.findByNameContainingIgnoreCase(nombre);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con ese nombre en la base de datos.");
            return;
        }

        System.out.println("\n===== Autores encontrados =====");
        autores.forEach(System.out::println);
    }

    private void listarAutoresPorRangoDeAnios() {
        System.out.print("Ingresa el año de inicio: ");
        String inputInicio = scanner.nextLine().trim();
        System.out.print("Ingresa el año de fin: ");
        String inputFin = scanner.nextLine().trim();

        try {
            int inicio = Integer.parseInt(inputInicio);
            int fin = Integer.parseInt(inputFin);

            if (inicio > fin) {
                System.out.println("El año de inicio no puede ser mayor al año de fin.");
                return;
            }

            List<Author> autores = authorRepository.findByBirthYearBetween(inicio, fin);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores nacidos entre " + inicio + " y " + fin + ".");
                return;
            }

            System.out.println("\n===== Autores nacidos entre " + inicio + " y " + fin + " =====");
            autores.forEach(System.out::println);

        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Por favor, ingresa solo números enteros.");
        }
    }
}
