package com.alura.ebookapi.repository;

import com.alura.ebookapi.model.Author;
import com.alura.ebookapi.principal.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthorRepositoryTest {

    @MockitoBean
    Principal principal;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        Author dickens = new Author();
        dickens.setName("Dickens, Charles");
        dickens.setBirthYear(1812);
        dickens.setDeathYear(1870);
        authorRepository.save(dickens);

        Author twain = new Author();
        twain.setName("Twain, Mark");
        twain.setBirthYear(1835);
        twain.setDeathYear(1910);
        authorRepository.save(twain);

        Author austen = new Author();
        austen.setName("Austen, Jane");
        austen.setBirthYear(1775);
        austen.setDeathYear(1817);
        authorRepository.save(austen);
    }

    @Test
    void debeEncontrarAutorPorNombreIgnorandoCase() {
        Optional<Author> resultado = authorRepository.findByNameIgnoreCase("dickens, charles");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getName()).isEqualTo("Dickens, Charles");
    }

    @Test
    void debeRetornarVacioSiAutorNoExiste() {
        Optional<Author> resultado = authorRepository.findByNameIgnoreCase("Autor Inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    void debeListarAutoresNacidosAntesOEnAnio() {
        List<Author> autores = authorRepository.findByBirthYearLessThanEqual(1812);

        assertThat(autores).hasSizeGreaterThanOrEqualTo(2);
        assertThat(autores).extracting(Author::getName)
                .contains("Dickens, Charles", "Austen, Jane");
    }

    @Test
    void debeListarAutoresQueContienenNombre() {
        // La coma "," aparece en todos los nombres con formato "Apellido, Nombre"
        List<Author> autores = authorRepository.findByNameContainingIgnoreCase(",");

        assertThat(autores).hasSizeGreaterThanOrEqualTo(3);
        assertThat(autores).extracting(Author::getName)
                .contains("Dickens, Charles", "Twain, Mark", "Austen, Jane");
    }

    @Test
    void debeEncontrarAutorUnicoPorFragmentoNombre() {
        List<Author> autores = authorRepository.findByNameContainingIgnoreCase("austen");

        assertThat(autores).hasSize(1);
        assertThat(autores.get(0).getName()).isEqualTo("Austen, Jane");
    }

    @Test
    void debeListarAutoresPorRangoDeAnios() {
        List<Author> autores = authorRepository.findByBirthYearBetween(1800, 1850);

        assertThat(autores).hasSizeGreaterThanOrEqualTo(2);
        assertThat(autores).extracting(Author::getName)
                .contains("Dickens, Charles", "Twain, Mark");
    }

    @Test
    void debeRetornarListaVaciaParaRangoSinAutores() {
        List<Author> autores = authorRepository.findByBirthYearBetween(1900, 1950);

        assertThat(autores).isEmpty();
    }

    @Test
    void debePersistirYRecuperarAutor() {
        Author nuevo = new Author();
        nuevo.setName("Homer");
        nuevo.setBirthYear(-800);
        nuevo.setDeathYear(null);
        authorRepository.save(nuevo);

        Optional<Author> recuperado = authorRepository.findByNameIgnoreCase("Homer");

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getBirthYear()).isEqualTo(-800);
        assertThat(recuperado.get().getDeathYear()).isNull();
    }
}
