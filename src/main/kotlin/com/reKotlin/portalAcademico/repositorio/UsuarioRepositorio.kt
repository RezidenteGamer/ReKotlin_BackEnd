package com.reKotlin.portalAcademico.repositorio

import com.reKotlin.portalAcademico.modelo.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepositorio : JpaRepository<Usuario, Long> {

    // Este método é um ótimo exemplo de polimorfismo:
    // Ele busca na tabela 'usuario' pelo email.
    // O objeto retornado será do tipo 'Usuario', mas o Spring
    // saberá, graças ao @DiscriminatorColumn, se deve instanciar
    // um objeto 'Professor' ou 'Academico'.
    fun findByEmail(email: String): Usuario?
}