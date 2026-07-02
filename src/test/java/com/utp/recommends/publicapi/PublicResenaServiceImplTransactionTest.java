package com.utp.recommends.publicapi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.utp.recommends.publicapi.service.PublicResenaServiceImpl;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class PublicResenaServiceImplTransactionTest {

    @Test
    void listarRunsInsideReadOnlyTransaction() throws Exception {
        Method method = PublicResenaServiceImpl.class.getMethod("listar", Long.class, Long.class, org.springframework.data.domain.Pageable.class);

        Transactional transactional = method.getAnnotation(Transactional.class);

        assertTrue(transactional != null && transactional.readOnly(),
            "listar debe ejecutar dentro de una transaccion readOnly para resolver asociaciones lazy del listado publico");
    }
}
