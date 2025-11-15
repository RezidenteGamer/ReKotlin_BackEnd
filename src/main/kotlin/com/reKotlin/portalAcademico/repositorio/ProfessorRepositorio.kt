    package com.reKotlin.portalAcademico.repositorio

    import com.reKotlin.portalAcademico.modelo.Professor
    import org.springframework.data.jpa.repository.JpaRepository
    import org.springframework.stereotype.Repository

    @Repository
    interface ProfessorRepositorio : JpaRepository<Professor, Long> {

        // Por enquanto, não precisamos de métodos customizados aqui,
        // mas se quiséssemos buscar um professor pelo departamento,
        // poderíamos adicionar:
        // fun findByDepartamento(departamento: String): List<Professor>
    }