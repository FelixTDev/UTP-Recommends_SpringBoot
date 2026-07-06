package com.utp.recommends.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class CorsConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
        .withUserConfiguration(CorsConfig.class);

    @Test
    void usesLocalOriginsByDefault() {
        contextRunner
            .withPropertyValues("app.cors.allowed-origins=http://localhost:4200,http://127.0.0.1:4200")
            .run(context -> {
                CorsProperties properties = context.getBean(CorsProperties.class);

                assertThat(properties.allowedOrigins())
                    .containsExactly("http://localhost:4200", "http://127.0.0.1:4200");
            });
    }

    @Test
    void usesConfiguredOriginsWhenProvided() {
        contextRunner
            .withPropertyValues("app.cors.allowed-origins=https://frontend.vercel.app,https://preview.vercel.app")
            .run(context -> {
                CorsProperties properties = context.getBean(CorsProperties.class);

                assertThat(properties.allowedOrigins())
                    .isEqualTo(List.of("https://frontend.vercel.app", "https://preview.vercel.app"));
            });
    }
}
