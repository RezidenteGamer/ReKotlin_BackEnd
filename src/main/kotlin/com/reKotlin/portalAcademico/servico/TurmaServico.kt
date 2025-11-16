/**
 * ============================================================================
 * SERVIÇO: TurmaServico
 * ============================================================================
 *
 * Camada de LÓGICA DE NEGÓCIO da aplicação.
 * Intermediário entre Controller e Repository.
 *
 * REQUISITOS IMPLEMENTADOS:
 * Salvar entidade
 * Buscar lista
 * Consulta por nome
 * Editar entidade
 * Excluir entidade
 * FUNCIONALIDADES EXTRAS (matricular, remover)
 *
 * RESPONSABILIDADES:
 * - Validações de negócio
 * - Conversão DTO ↔ Entidade
 * - Orquestração de repositórios
 * - Tratamento de exceções
 * - Transações
 *
 *
 */

package com.reKotlin.portalAcademico.servico

import com.reKotlin.portalAcademico.dto.TurmaRequestDTO
import com.reKotlin.portalAcademico.dto.TurmaResponseDTO
import com.reKotlin.portalAcademico.modelo.Academico
import com.reKotlin.portalAcademico.modelo.Professor
import com.reKotlin.portalAcademico.modelo.Turma
import com.reKotlin.portalAcademico.repositorio.AcademicoRepositorio
import com.reKotlin.portalAcademico.repositorio.ProfessorRepositorio
import com.reKotlin.portalAcademico.repositorio.TurmaRepositorio
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @Service - Marca como componente Spring (camada de serviço)
 * @Transactional - Todas operações são transacionais (rollback automático em erro)
 */
@Service
@Transactional
class TurmaServico(
    // Injeção de dependências via construtor (melhor prática)
    private val turmaRepositorio: TurmaRepositorio,
    private val professorRepositorio: ProfessorRepositorio,
    private val academicoRepositorio: AcademicoRepositorio
) {

    /**
     * ========================================================================
     * MÉTODO: criarTurma
     * ========================================================================
     *
     * Cria uma nova turma no banco de dados.
     *
     * REQUISITO: Salvar entidade
     *
     * Fluxo:
     * 1. Valida se professor existe
     * 2. Valida se é realmente professor (não acadêmico)
     * 3. Cria entidade Turma
     * 4. Salva no banco
     * 5. Converte para DTO de resposta
     *
     * @param request Dados da turma (TurmaRequestDTO)
     * @return Turma criada (TurmaResponseDTO)
     * @throws EntityNotFoundException se professor não existe
     * @throws IllegalArgumentException se usuário não é professor
     */
    fun criarTurma(request: TurmaRequestDTO): TurmaResponseDTO {
        // Busca professor no banco
        val professor = professorRepositorio.findById(request.professorId)
            .orElseThrow { EntityNotFoundException("Professor não encontrado") } as? Professor
            ?: throw IllegalArgumentException("Usuário não é um professor")

        // Cria entidade Turma
        val turma = Turma(
            nome = request.nome,
            descricao = request.descricao,
            professor = professor
        )

        // Salva no banco (INSERT)
        val turmaSalva = turmaRepositorio.save(turma)

        // Converte para DTO e retorna
        return turmaParaResponseDTO(turmaSalva)
    }

    /**
     * ========================================================================
     * MÉTODO: listarTodas
     * ========================================================================
     *
     * Busca todas as turmas do banco.
     *
     * REQUISITO: Buscar lista de entidades
     */
    fun listarTodas(): List<TurmaResponseDTO> {
        return turmaRepositorio.findAll().map(::turmaParaResponseDTO)
    }

    /**
     * ========================================================================
     * MÉTODO: buscarPorNome
     * ========================================================================
     *
     * Busca turmas que contém o nome informado (case-insensitive).
     *
     * REQUISITO: Fazer consulta por nome
     */
    fun buscarPorNome(nome: String): List<TurmaResponseDTO> {
        return turmaRepositorio.findByNomeContainingIgnoreCase(nome).map(::turmaParaResponseDTO)
    }

    /**
     * ========================================================================
     * MÉTODO: atualizarTurma
     * ========================================================================
     *
     * Atualiza uma turma existente.
     *
     * REQUISITO: Editar entidade
     */
    fun atualizarTurma(id: Long, request: TurmaRequestDTO): TurmaResponseDTO {
        val turmaExistente = turmaRepositorio.findById(id)
            .orElseThrow { EntityNotFoundException("Turma não encontrada") }

        val professor = professorRepositorio.findById(request.professorId)
            .orElseThrow { EntityNotFoundException("Professor não encontrado") } as? Professor
            ?: throw IllegalArgumentException("Usuário não é um professor")

        // Atualiza campos
        turmaExistente.nome = request.nome
        turmaExistente.descricao = request.descricao
        turmaExistente.professor = professor

        val turmaAtualizada = turmaRepositorio.save(turmaExistente)
        return turmaParaResponseDTO(turmaAtualizada)
    }

    /**
     * ========================================================================
     * MÉTODO: excluirTurma
     * ========================================================================
     *
     * Exclui uma turma do banco.
     *
     * REQUISITO: Excluir entidade
     */
    fun excluirTurma(id: Long) {
        if (!turmaRepositorio.existsById(id)) {
            throw EntityNotFoundException("Turma não encontrada")
        }
        turmaRepositorio.deleteById(id)
    }

    /**
     * ========================================================================
     * FUNCIONALIDADE EXTRA #1: Matricular acadêmico
     * ========================================================================
     */
    fun adicionarAcademico(idTurma: Long, idAcademico: Long): TurmaResponseDTO {
        val turma = turmaRepositorio.findById(idTurma)
            .orElseThrow { EntityNotFoundException("Turma não encontrada") }

        val academico = academicoRepositorio.findById(idAcademico)
            .orElseThrow { EntityNotFoundException("Acadêmico não encontrado") } as? Academico
            ?: throw IllegalArgumentException("Usuário não é um acadêmico")

        if (!turma.academicosMatriculados.contains(academico)) {
            turma.academicosMatriculados.add(academico)
            turmaRepositorio.save(turma)
        }

        return turmaParaResponseDTO(turma)
    }

    /**
     * ========================================================================
     * FUNCIONALIDADE EXTRA #2: Remover acadêmico
     * ========================================================================
     */
    fun removerAcademico(idTurma: Long, idAcademico: Long): TurmaResponseDTO {
        val turma = turmaRepositorio.findById(idTurma)
            .orElseThrow { EntityNotFoundException("Turma não encontrada") }

        val academico = academicoRepositorio.findById(idAcademico)
            .orElseThrow { EntityNotFoundException("Acadêmico não encontrado") } as? Academico
            ?: throw IllegalArgumentException("Usuário não é um acadêmico")

        turma.academicosMatriculados.remove(academico)
        turmaRepositorio.save(turma)

        return turmaParaResponseDTO(turma)
    }

    /**
     * ========================================================================
     * MÉTODO AUXILIAR: Converter Entidade → DTO
     * ========================================================================
     */
    private fun turmaParaResponseDTO(turma: Turma): TurmaResponseDTO {
        return TurmaResponseDTO(
            id = turma.id!!,
            nome = turma.nome,
            descricao = turma.descricao,
            professorId = turma.professor.id!!,
            nomeProfessor = turma.professor.nome,
            quantidadeAlunos = turma.academicosMatriculados.size
        )
    }
}