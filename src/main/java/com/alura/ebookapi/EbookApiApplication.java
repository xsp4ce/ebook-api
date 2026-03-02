package com.alura.ebookapi;

import com.alura.ebookapi.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EbookApiApplication implements CommandLineRunner {

    private final Principal principal;

    public EbookApiApplication(Principal principal) {
        this.principal = principal;
    }

    public static void main(String[] args) {
        SpringApplication.run(EbookApiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        principal.exibirMenu();
    }
}
