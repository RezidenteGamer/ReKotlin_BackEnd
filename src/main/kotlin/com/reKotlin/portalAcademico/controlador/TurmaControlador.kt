package com.reKotlin.portalAcademico.controlador

import com.reKotlin.portalAcademico.dto.TurmaRequestDTO
import com.reKotlin.portalAcademico.dto.TurmaResponseDTO
import com.reKotlin.portalAcademico.servico.TurmaServico
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/turmas") // Prefixo da URL para todas as rotas deste controlador
//@CrossOrigin(origins = ["http://localhost:5173"]) // Permite que o React (porta 5173) acesse
class TurmaControlador(
    private val turmaServico: TurmaServico
) {

    // POST (Salvar)
    @PostMapping
    fun criar(@Valid @RequestBody request: TurmaRequestDTO): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.criarTurma(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(turma)
    }

    // GET (Listar tudo)
    @GetMapping
    fun listarTodas(): ResponseEntity<List<TurmaResponseDTO>> {
        return ResponseEntity.ok(turmaServico.listarTodas())
    }

    // GET (Consulta por nome)
    @GetMapping("/buscar")
    fun buscarPorNome(@RequestParam nome: String): ResponseEntity<List<TurmaResponseDTO>> {
        return ResponseEntity.ok(turmaServico.buscarPorNome(nome))
    }

    // PUT (Editar)
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: TurmaRequestDTO
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.atualizarTurma(id, request)
        return ResponseEntity.ok(turma)
    }

    // DELETE (Excluir)
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Unit> {
        turmaServico.excluirTurma(id)
        return ResponseEntity.noContent().build()
    }

    // --- Rotas das Funcionalidades Extras ---

    // POST (Acadêmico entra na turma)
    @PostMapping("/{idTurma}/matricular/{idAcademico}")
    fun matricularAcademico(
        @PathVariable idTurma: Long,
        @PathVariable idAcademico: Long
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.adicionarAcademico(idTurma, idAcademico)
        return ResponseEntity.ok(turma)
    }

    // DELETE (Professor remove acadêmico)
    @DeleteMapping("/{idTurma}/remover/{idAcademico}")
    fun removerAcademico(
        @PathVariable idTurma: Long,
        @PathVariable idAcademico: Long
    ): ResponseEntity<TurmaResponseDTO> {
        val turma = turmaServico.removerAcademico(idTurma, idAcademico)
        return ResponseEntity.ok(turma)
    }
}