package com.alura.ebookapi.client;

import com.alura.ebookapi.dto.BookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class GutendexClient {
    private static final String BASE_URL = "https://gutendex.com/books";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GutendexClient() {
        this(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build(), new ObjectMapper());
    }

    GutendexClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public BookResponse buscarLibros(String query) throws IOException, InterruptedException {
        String url = BASE_URL + "?search=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("La API respondió con código HTTP " + response.statusCode());
        }

        if (response.body() == null || response.body().isBlank()) {
            throw new IOException("La API devolvió una respuesta vacía.");
        }

        return objectMapper.readValue(response.body(), BookResponse.class);
    }
}
