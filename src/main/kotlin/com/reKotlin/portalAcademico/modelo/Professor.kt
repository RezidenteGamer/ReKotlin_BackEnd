package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.DiscriminatorValue

@Entity
@DiscriminatorValue("PROFESSOR") // Valor que identifica esta classe na coluna 'tipo_usuario'
class Professor(
    id: Long? = null,
    email: String,
    nome: String,
    senhaPlana: String,

    var departamento: String,

    @OneToMany(mappedBy = "professor") // Um professor pode ter muitas turmas
    val turmasCriadas: MutableList<Turma> = mutableListOf()

) : Usuario(id, email, nome, senhaPlana) // Herda de Usuario