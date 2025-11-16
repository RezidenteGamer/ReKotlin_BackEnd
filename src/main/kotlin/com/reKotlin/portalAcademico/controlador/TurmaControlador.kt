/**
 * ============================================================================
 * CONTROLADORES REST - ENDPOINTS DA API
 * ============================================================================
 *
 * Controllers expõem endpoints HTTP que o front-end consome.
 *
 * REQUISITOS IMPLEMENTADOS:
 * API REST completa (GET, POST, PUT, DELETE)
 * Todas funcionalidades acessíveis via HTTP
 *
 * PADRÃO: Controller Layer (MVC)
 *
 */

package com.reKotlin.portalAcademico.controlador

import com.reKotlin.portalAcademico.dto.*
import com.reKotlin.portalAcademico.servico.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * ============================================================================
 * CONTROLADOR: TurmaControlador
 * ============================================================================
 *
 * Expõe TODOS os endpoints relacionados a turmas.
 *
 * @RestController - Combina @Controller + @ResponseBody
 * @RequestMapping("/api/turmas") - Prefixo de todas as rotas
 */
@RestController
@RequestMapping("/api/turmas")
class TurmaControlador(
    private val turmaServico: TurmaServico
) {

    /**
     * POST /api/turmas
     * Cria nova turma
     *
     * @Valid valida automaticamente o DTO
     * ResponseEntity.status(201) retorna HTTP 201 Created
     */
    @PostMapping
    fun criar(@Valid @RequestBody request: TurmaRequestDTO): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.criarTurma(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(turma)
    }

    /**
     * GET /api/turmas
     * Lista todas as turmas
     */
    @GetMapping
    fun listarTodas(): ResponseEntity<List<TurmaResponseDTO>> {
        return ResponseEntity.ok(turmaServico.listarTodas())
    }

    /**
     * GET /api/turmas/buscar?nome=X
     * Busca turmas por nome
     */
    @GetMapping("/buscar")
    fun buscarPorNome(@RequestParam nome: String): ResponseEntity<List<TurmaResponseDTO>> {
        return ResponseEntity.ok(turmaServico.buscarPorNome(nome))
    }

    /**
     * PUT /api/turmas/:id
     * Atualiza turma existente
     */
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: TurmaRequestDTO
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.atualizarTurma(id, request)
        return ResponseEntity.ok(turma)
    }

    /**
     * DELETE /api/turmas/:id
     * Exclui turma
     */
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Unit> {
        turmaServico.excluirTurma(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * POST /api/turmas/:idTurma/matricular/:idAcademico
     * Matricula acadêmico na turma (EXTRA #1)
     */
    @PostMapping("/{idTurma}/matricular/{idAcademico}")
    fun matricularAcademico(
        @PathVariable idTurma: Long,
        @PathVariable idAcademico: Long
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.adicionarAcademico(idTurma, idAcademico)
        return ResponseEntity.ok(turma)
    }

    /**
     * DELETE /api/turmas/:idTurma/remover/:idAcademico
     * Remove acadêmico da turma (EXTRA #2)
     */
    @DeleteMapping("/{idTurma}/remover/{idAcademico}")
    fun removerAcademico(
        @PathVariable idTurma: Long,
        @PathVariable idAcademico: Long
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.removerAcademico(idTurma, idAcademico)
        return ResponseEntity.ok(turma)
    }
}
