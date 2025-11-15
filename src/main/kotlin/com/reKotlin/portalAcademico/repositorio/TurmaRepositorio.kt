package com.reKotlin.portalAcademico.repositorio

import com.reKotlin.portalAcademico.modelo.Turma
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TurmaRepositorio : JpaRepository<Turma, Long> {

    // Requisito: "Fazer uma consulta (por nome, valor, etc)"
    fun findByNomeContainingIgnoreCase(nome: String): List<Turma>
}