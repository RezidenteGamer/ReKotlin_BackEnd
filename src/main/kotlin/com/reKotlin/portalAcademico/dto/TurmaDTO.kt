package com.reKotlin.portalAcademico.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

// DTO para receber dados do front-end (POST/PUT)
data class TurmaRequestDTO(
    @field:NotBlank(message = "Nome é obrigatório")
    val nome: String,

    val descricao: String = "", // Pode ser vazio

    @field:NotNull(message = "Professor ID é obrigatório")
    val professorId: Long
)

// DTO para enviar dados ao front-end (GET)
data class TurmaResponseDTO(
    val id: Long,
    val nome: String,
    val descricao: String,
    val professorId: Long, // ADICIONE ISTO!
    val nomeProfessor: String,
    val quantidadeAlunos: Int
)