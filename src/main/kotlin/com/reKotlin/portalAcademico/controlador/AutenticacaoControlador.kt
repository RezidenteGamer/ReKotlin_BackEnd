package com.reKotlin.portalAcademico.controlador

import com.reKotlin.portalAcademico.dto.LoginRequestDTO
import com.reKotlin.portalAcademico.dto.LoginResponseDTO
import com.reKotlin.portalAcademico.servico.AutenticacaoServico
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para autenticação de usuários.
 * Expõe endpoints para login e validação de credenciais.
 */
@RestController
@RequestMapping("/api/auth")
class AutenticacaoControlador(
    private val autenticacaoServico: AutenticacaoServico
) {

    /**
     * Endpoint para realizar login.
     *
     * POST /api/auth/login
     * Body: { "email": "...", "senha": "...", "tipoUsuario": "PROFESSOR" }
     *
     * @param request Credenciais do usuário
     * @return LoginResponseDTO com dados do usuário autenticado
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestDTO): ResponseEntity<LoginResponseDTO> {
        return try {
            val resposta = autenticacaoServico.fazerLogin(request)
            ResponseEntity.ok(resposta)
        } catch (e: IllegalArgumentException) {
            // Retorna 401 (Unauthorized) se credenciais inválidas
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}