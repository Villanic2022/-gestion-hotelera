package com.miempresa.gestion_hotelera.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Check-in Simples- API")
                        .description("Backend para gestión de hoteles/cabañas: reservas, huéspedes, pagos, disponibilidad.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Check-in Simples")
                                .email("villanicolas86@gmail.com")
                        )
                );
    }
}
