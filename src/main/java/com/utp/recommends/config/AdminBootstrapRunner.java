package com.utp.recommends.config;

import com.utp.recommends.common.validation.ValidationPatterns;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import com.utp.recommends.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final AdminBootstrapProperties properties;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapRunner(
        AdminBootstrapProperties properties,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.properties = properties;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            return;
        }

        validateProperties();

        Usuario admin = usuarioRepository.findByEmail(properties.email()).orElseGet(Usuario::new);
        admin.setEmail(properties.email());
        admin.setNombres(properties.nombres());
        admin.setApellidos(properties.apellidos());
        admin.setRol(RolUsuario.ADMIN);
        admin.setEstado(EstadoUsuario.ACTIVO);
        admin.setPasswordHash(passwordEncoder.encode(properties.password()));
        usuarioRepository.save(admin);
        log.info("Admin bootstrap applied for {}", properties.email());
    }

    private void validateProperties() {
        if (!StringUtils.hasText(properties.email()) || !properties.email().matches(ValidationPatterns.UTP_ADMIN_EMAIL)) {
            throw new IllegalStateException("app.bootstrap.admin.email debe ser un correo UTP válido");
        }
        if (!StringUtils.hasText(properties.password()) || !properties.password().matches(ValidationPatterns.PASSWORD)) {
            throw new IllegalStateException("app.bootstrap.admin.password no cumple la política requerida");
        }
        if (!StringUtils.hasText(properties.nombres()) || !properties.nombres().matches(ValidationPatterns.PERSON_NAME)) {
            throw new IllegalStateException("app.bootstrap.admin.nombres es inválido");
        }
        if (!StringUtils.hasText(properties.apellidos()) || !properties.apellidos().matches(ValidationPatterns.PERSON_NAME)) {
            throw new IllegalStateException("app.bootstrap.admin.apellidos es inválido");
        }
    }
}
