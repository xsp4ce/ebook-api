package com.alura.ebookapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    private String language;
    private Integer downloadCount;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book() {}

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    @Override
    public String toString() {
        return """
                ----- LIBRO -----
                Título    : %s
                Autor     : %s
                Idioma    : %s
                Descargas : %d
                -----------------
                """.formatted(
                title,
                author != null ? author.getName() : "Desconocido",
                language,
                downloadCount
        );
    }
}
