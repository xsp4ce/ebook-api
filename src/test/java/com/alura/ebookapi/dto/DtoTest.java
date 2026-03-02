package com.alura.ebookapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void debeDeserializarAuthorData() throws Exception {
        String json = """
                {
                    "name": "Dickens, Charles",
                    "birth_year": 1812,
                    "death_year": 1870
                }
                """;

        AuthorData author = objectMapper.readValue(json, AuthorData.class);

        assertThat(author.getName()).isEqualTo("Dickens, Charles");
        assertThat(author.getBirthYear()).isEqualTo(1812);
        assertThat(author.getDeathYear()).isEqualTo(1870);
    }

    @Test
    void debeIgnorarCamposDesconocidosEnAuthorData() throws Exception {
        String json = """
                {
                    "name": "Twain, Mark",
                    "birth_year": 1835,
                    "death_year": 1910,
                    "unknown_field": "ignored"
                }
                """;

        AuthorData author = objectMapper.readValue(json, AuthorData.class);

        assertThat(author.getName()).isEqualTo("Twain, Mark");
    }

    @Test
    void debeDeserializarAuthorDataSinAnioFallecimiento() throws Exception {
        String json = """
                {
                    "name": "Author Vivo",
                    "birth_year": 1980,
                    "death_year": null
                }
                """;

        AuthorData author = objectMapper.readValue(json, AuthorData.class);

        assertThat(author.getName()).isEqualTo("Author Vivo");
        assertThat(author.getBirthYear()).isEqualTo(1980);
        assertThat(author.getDeathYear()).isNull();
    }

    @Test
    void debeDeserializarBookData() throws Exception {
        String json = """
                {
                    "id": 1342,
                    "title": "Pride and Prejudice",
                    "authors": [
                        {"name": "Austen, Jane", "birth_year": 1775, "death_year": 1817}
                    ],
                    "languages": ["en"],
                    "download_count": 49000
                }
                """;

        BookData book = objectMapper.readValue(json, BookData.class);

        assertThat(book.getId()).isEqualTo(1342);
        assertThat(book.getTitle()).isEqualTo("Pride and Prejudice");
        assertThat(book.getAuthors()).hasSize(1);
        assertThat(book.getAuthors().get(0).getName()).isEqualTo("Austen, Jane");
        assertThat(book.getLanguages()).containsExactly("en");
        assertThat(book.getDownloadCount()).isEqualTo(49000);
    }

    @Test
    void debeDeserializarBookResponse() throws Exception {
        String json = """
                {
                    "count": 1,
                    "next": null,
                    "previous": null,
                    "results": [
                        {
                            "id": 1342,
                            "title": "Pride and Prejudice",
                            "authors": [
                                {"name": "Austen, Jane", "birth_year": 1775, "death_year": 1817}
                            ],
                            "languages": ["en"],
                            "download_count": 49000
                        }
                    ]
                }
                """;

        BookResponse response = objectMapper.readValue(json, BookResponse.class);

        assertThat(response.getCount()).isEqualTo(1);
        assertThat(response.getNext()).isNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResults().get(0).getTitle()).isEqualTo("Pride and Prejudice");
    }

    @Test
    void debeDeserializarBookResponseVacia() throws Exception {
        String json = """
                {
                    "count": 0,
                    "next": null,
                    "previous": null,
                    "results": []
                }
                """;

        BookResponse response = objectMapper.readValue(json, BookResponse.class);

        assertThat(response.getCount()).isEqualTo(0);
        assertThat(response.getResults()).isEmpty();
    }

    @Test
    void debeIgnorarCamposDesconocidosEnBookData() throws Exception {
        String json = """
                {
                    "id": 1,
                    "title": "Test Book",
                    "authors": [],
                    "languages": ["es"],
                    "download_count": 100,
                    "bookshelves": ["Fiction"],
                    "formats": {}
                }
                """;

        BookData book = objectMapper.readValue(json, BookData.class);

        assertThat(book.getTitle()).isEqualTo("Test Book");
        assertThat(book.getLanguages()).containsExactly("es");
    }
}
