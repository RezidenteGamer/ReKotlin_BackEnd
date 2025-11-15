package com.reKotlin.portalAcademico.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * DTO para receber credenciais de login do front-end
 */
data class LoginRequestDTO(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    val senha: String,

    @field:NotBlank(message = "Tipo de usuário é obrigatório")
    val tipoUsuario: String // "PROFESSOR" ou "ACADEMICO"
)

/**
 * DTO para retornar dados do usuário autenticado ao front-end
 */
data class LoginResponseDTO(
    val id: Long,
    val email: String,
    val nome: String,
    val tipoUsuario: String, // "PROFESSOR" ou "ACADEMICO"

    // Campos específicos de cada tipo
    val matricula: String? = null, // Apenas para acadêmico
    val departamento: String? = null // Apenas para professor
)