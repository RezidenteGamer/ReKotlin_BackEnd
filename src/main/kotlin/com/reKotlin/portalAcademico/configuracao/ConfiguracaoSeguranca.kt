package com.reKotlin.portalAcademico.configuracao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Arrays // Necessário para 'Arrays.asList'
@Configuration
@EnableWebSecurity
class ConfiguracaoSeguranca {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1. FOCO TOTAL: Desabilitar o CSRF. Este deve ser o culpado.
            .csrf { it.disable() }

            // 2. Permite TODOS os requests (sem auth, sem sessao)
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

            // 3. Remove filtros desnecessários para APIs puras
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .cors { } // Mantém o CORS habilitado para usar o Bean (que deve estar logo abaixo)

        return http.build()
    }

    // 6. Configuração Global de CORS (a mesma de antes, que está correta)
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuracao = CorsConfiguration()

        configuracao.allowedOrigins = Arrays.asList("http://localhost:5173")
        configuracao.allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuracao.allowedHeaders = Arrays.asList("*") // Permite todos os headers

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuracao) // Aplica a todas as rotas

        return source
    }
}