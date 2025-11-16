/**
 * ============================================================================
 * CONFIGURAÇÃO DE SEGURANÇA E CORS
 * ============================================================================
 *
 * Configuração do Spring Security para:
 * - Desabilitar CSRF (não necessário em APIs REST stateless)
 * - Permitir todas requisições (sem autenticação por JWT)
 * - Habilitar CORS para comunicação com front-end
 *
 *
 */

package com.reKotlin.portalAcademico.configuracao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Arrays

/**
 * @Configuration - Marca como classe de configuração Spring
 * @EnableWebSecurity - Habilita configuração customizada de segurança
 */
@Configuration
@EnableWebSecurity
class ConfiguracaoSeguranca {

    /**
     * ========================================================================
     * BEAN: Security Filter Chain
     * ========================================================================
     *
     * Configura regras de segurança da aplicação.
     *
     * CSRF DESABILITADO:
     * - APIs REST stateless não precisam (usam tokens)
     * - Front-end em domínio diferente (CORS resolve)
     *
     * TODAS REQUISIÇÕES PERMITIDAS:
     * - anyRequest().permitAll() → não requer autenticação
     * - Em produção: use JWT + roles
     *
     * HTTP BASIC/FORM LOGIN DESABILITADOS:
     * - Não usamos formulário de login do Spring
     * - Nosso login é customizado (POST /api/auth/login)
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1. DESABILITA CSRF
            .csrf { it.disable() }

            // 2. PERMITE TODAS AS REQUISIÇÕES
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

            // 3. DESABILITA AUTENTICAÇÕES PADRÃO
            .httpBasic { it.disable() }
            .formLogin { it.disable() }

            // 4. HABILITA CORS
            .cors { }

        return http.build()
    }

    /**
     * ========================================================================
     * BEAN: CORS Configuration
     * ========================================================================
     *
     * Configura CORS (Cross-Origin Resource Sharing).
     *
     * POR QUE PRECISAMOS?
     * - Front-end: http://localhost:5173
     * - Back-end: http://localhost:8080
     * - Domínios diferentes = bloqueado por padrão (segurança)
     *
     * CORS permite comunicação entre domínios diferentes.
     *
     * allowedOrigins: ["http://localhost:5173"]
     * - Apenas este domínio pode fazer requisições
     *
     * allowedMethods: GET, POST, PUT, DELETE, OPTIONS
     * - Métodos HTTP permitidos
     *
     * allowedHeaders: "*"
     * - Aceita qualquer header (Content-Type, etc)
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuracao = CorsConfiguration()

        configuracao.allowedOrigins = Arrays.asList("http://localhost:5173")
        configuracao.allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuracao.allowedHeaders = Arrays.asList("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuracao)

        return source
    }
}