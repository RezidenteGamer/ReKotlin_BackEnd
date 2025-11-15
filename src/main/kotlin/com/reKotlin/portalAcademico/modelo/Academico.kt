package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany

@Entity
@DiscriminatorValue("ACADEMICO") // Valor que identifica esta classe
class Academico(
    id: Long? = null,
    email: String,
    nome: String,
    senhaPlana: String,

    var matricula: String,

    @ManyToMany(mappedBy = "academicosMatriculados") // Um acadêmico pode estar em muitas turmas
    val turmas: MutableList<Turma> = mutableListOf()

) : Usuario(id, email, nome, senhaPlana) // Herda de Usuario