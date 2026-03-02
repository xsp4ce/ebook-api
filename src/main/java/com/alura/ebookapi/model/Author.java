package com.alura.ebookapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer birthYear;
    private Integer deathYear;

    public Author() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }

    public Integer getDeathYear() { return deathYear; }
    public void setDeathYear(Integer deathYear) { this.deathYear = deathYear; }

    @Override
    public String toString() {
        return """
                ----- AUTOR -----
                Nombre       : %s
                Nacimiento   : %s
                Fallecimiento: %s
                -----------------
                """.formatted(
                name,
                birthYear != null ? birthYear.toString() : "Desconocido",
                deathYear != null ? deathYear.toString() : "Vivo"
        );
    }
}
