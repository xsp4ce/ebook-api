package com.alura.ebookapi;

import com.alura.ebookapi.principal.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class EbookApiApplicationTests {

    @MockitoBean
    Principal principal;

    @Test
    void contextLoads() {
    }
}
