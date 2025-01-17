package biblioteca.azure_basic_app.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permitir todos los endpoints
                        .allowedOriginPatterns("*") // Permitir todos los orígenes
                        .allowedMethods("GET", "POST", "OPTIONS", "PUT") // Métodos permitidos
                        .allowedHeaders("*"); // Permitir todos los encabezados
                        
            }
        };
    }
}