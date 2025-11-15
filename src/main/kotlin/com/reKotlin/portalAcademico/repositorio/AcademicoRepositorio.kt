package com.reKotlin.portalAcademico.repositorio

import com.reKotlin.portalAcademico.modelo.Academico
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AcademicoRepositorio : JpaRepository<Academico, Long> {

    // Também não precisamos de métodos customizados obrigatórios por agora,
    // mas um exemplo útil seria:
    // fun findByMatricula(matricula: String): Academico?
}