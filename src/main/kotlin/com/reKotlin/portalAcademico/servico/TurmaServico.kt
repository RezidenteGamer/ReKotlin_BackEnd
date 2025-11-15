package com.reKotlin.portalAcademico.servico

import com.reKotlin.portalAcademico.dto.TurmaRequestDTO
import com.reKotlin.portalAcademico.dto.TurmaResponseDTO
import com.reKotlin.portalAcademico.modelo.Academico
import com.reKotlin.portalAcademico.modelo.Professor
import com.reKotlin.portalAcademico.modelo.Turma
import com.reKotlin.portalAcademico.repositorio.AcademicoRepositorio // Crie este repositório
import com.reKotlin.portalAcademico.repositorio.ProfessorRepositorio // Crie este repositório
import com.reKotlin.portalAcademico.repositorio.TurmaRepositorio
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional // Garante que as operações com banco de dados sejam atômicas
class TurmaServico(
    private val turmaRepositorio: TurmaRepositorio,
    // Injete os outros repositórios (crie-os como fez com o TurmaRepositorio)
    private val professorRepositorio: ProfessorRepositorio,
    private val academicoRepositorio: AcademicoRepositorio
) {

    // Requisito: "Salvar ao menos uma entidade no banco" (usado pelo Professor)
    fun criarTurma(request: TurmaRequestDTO): TurmaResponseDTO {
        val professor = professorRepositorio.findById(request.professorId)
            .orElseThrow { EntityNotFoundException("Professor não encontrado") } as? Professor
            ?: throw IllegalArgumentException("Usuário não é um professor")

        val turma = Turma(
            nome = request.nome,
            descricao = request.descricao,
            professor = professor
        )
        val turmaSalva = turmaRepositorio.save(turma)
        return turmaParaResponseDTO(turmaSalva)
    }

    // Requisito: "Buscar por uma lista de uma entidade no banco"
    fun listarTodas(): List<TurmaResponseDTO> {
        return turmaRepositorio.findAll().map(::turmaParaResponseDTO)
    }

    // Requisito: "Fazer uma consulta (por nome...)"
    fun buscarPorNome(nome: String): List<TurmaResponseDTO> {
        return turmaRepositorio.findByNomeContainingIgnoreCase(nome).map(::turmaParaResponseDTO)
    }

    // Requisito: "Editar ao menos uma entidade no banco"
    fun atualizarTurma(id: Long, request: TurmaRequestDTO): TurmaResponseDTO {
        val turmaExistente = turmaRepositorio.findById(id)
            .orElseThrow { EntityNotFoundException("Turma não encontrada") }

        val professor = professorRepositorio.findById(request.professorId)
            .orElseThrow { EntityNotFoundException("Professor não encontrado") } as? Professor
            ?: throw IllegalArgumentException("Usuário não é um professor")

        // Atualiza os dados
        turmaExistente.nome = request.nome
        turmaExistente.descricao = request.descricao
        turmaExistente.professor = professor

        val turmaAtualizada = turmaRepositorio.save(turmaExistente)
        return turmaParaResponseDTO(turmaAtualizada)
    }

    // Requisito: "Excluir ao menos uma entidade no banco"
    fun excluirTurma(id: Long) {
        if (!turmaRepositorio.existsById(id)) {
            throw EntityNotFoundException("Turma não encontrada")
        }
        turmaRepositorio.deleteById(id)
    }

    // --- Funcionalidades Extras ---

    // Funcionalidade Extra 1: "Acadêmicos podem entrar em turmas"
    fun adicionarAcademico(idTurma: Long, idAcademico: Long): TurmaResponseDTO {
        val turma = turmaRepositorio.findById(idTurma).orElseThrow { EntityNotFoundException("Turma não encontrada") }
        val academico = academicoRepositorio.findById(idAcademico).orElseThrow { EntityNotFoundException("Acadêmico não encontrado") } as? Academico
            ?: throw IllegalArgumentException("Usuário não é um acadêmico")

        if (!turma.academicosMatriculados.contains(academico)) {
            turma.academicosMatriculados.add(academico)
            turmaRepositorio.save(turma)
        }
        return turmaParaResponseDTO(turma)
    }

    // Funcionalidade Extra 2: "Professores podem... remover acadêmicos"
    fun removerAcademico(idTurma: Long, idAcademico: Long): TurmaResponseDTO {
        val turma = turmaRepositorio.findById(idTurma).orElseThrow { EntityNotFoundException("Turma não encontrada") }
        val academico = academicoRepositorio.findById(idAcademico).orElseThrow { EntityNotFoundException("Acadêmico não encontrado") } as? Academico
            ?: throw IllegalArgumentException("Usuário não é um acadêmico")

        turma.academicosMatriculados.remove(academico)
        turmaRepositorio.save(turma)
        return turmaParaResponseDTO(turma)
    }

    // Método auxiliar para converter Entidade em DTO
    private fun turmaParaResponseDTO(turma: Turma): TurmaResponseDTO {
        return TurmaResponseDTO(
            id = turma.id!!,
            nome = turma.nome,
            descricao = turma.descricao,
            professorId = turma.professor.id!!, // ADICIONE ISTO!
            nomeProfessor = turma.professor.nome,
            quantidadeAlunos = turma.academicosMatriculados.size
        )
    }
}