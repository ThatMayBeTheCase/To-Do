package se.todo.todoapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // API:et kräver inloggning (HTTP Basic). Startsidan och de statiska filerna
    // är öppna, så att hälsokontrollen i deployen kan nå "/" utan lösenord.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Ren JSON-API med Basic auth och ingen formulärinloggning,
                // så CSRF-skyddet behövs inte och skulle blockera POST/PUT/DELETE.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
