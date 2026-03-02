package com.alura.ebookapi.client;

import com.alura.ebookapi.dto.BookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GutendexClientTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private GutendexClient gutendexClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BOOK_RESPONSE_JSON = """
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

    @BeforeEach
    void setUp() {
        gutendexClient = new GutendexClient(httpClient, objectMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeBuscarLibrosExitosamente() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(BOOK_RESPONSE_JSON);

        BookResponse response = gutendexClient.buscarLibros("pride and prejudice");

        assertThat(response.getCount()).isEqualTo(1);
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResults().get(0).getTitle()).isEqualTo("Pride and Prejudice");
        assertThat(response.getResults().get(0).getAuthors().get(0).getName()).isEqualTo("Austen, Jane");
        assertThat(response.getResults().get(0).getDownloadCount()).isEqualTo(49000);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeLanzarExcepcionSiHttpStatusNoEs200() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(404);

        assertThatThrownBy(() -> gutendexClient.buscarLibros("libro"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("404");
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeLanzarExcepcionSiCuerpoEsVacio() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("");

        assertThatThrownBy(() -> gutendexClient.buscarLibros("libro"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("vacía");
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeLanzarExcepcionSiCuerpoEsBlanco() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("   ");

        assertThatThrownBy(() -> gutendexClient.buscarLibros("libro"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("vacía");
    }

    @Test
    @SuppressWarnings("unchecked")
    void debePropagarsrInterruptedException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("interrupted"));

        assertThatThrownBy(() -> gutendexClient.buscarLibros("libro"))
                .isInstanceOf(InterruptedException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeDeserializarRespuestaVacia() throws Exception {
        String emptyResults = """
                {"count": 0, "next": null, "previous": null, "results": []}
                """;

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(emptyResults);

        BookResponse response = gutendexClient.buscarLibros("libro inexistente");

        assertThat(response.getCount()).isEqualTo(0);
        assertThat(response.getResults()).isEmpty();
    }
}
