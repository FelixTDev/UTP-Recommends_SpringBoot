package com.utp.recommends.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record AdminBootstrapProperties(
    boolean enabled,
    String email,
    String password,
    String nombres,
    String apellidos
) {
}
