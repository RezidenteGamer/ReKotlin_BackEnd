package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.*

@Entity
class Turma(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // ID deve ser 'val' (imutável)

    @Column(nullable = false)
    var nome: String, // 'var' para permitir edição

    var descricao: String, // 'var' para permitir edição

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    var professor: Professor, // <--- CORREÇÃO AQUI: MUDAR DE 'val' PARA 'var'

    @ManyToMany
    @JoinTable(
        name = "turma_academicos",
        joinColumns = [JoinColumn(name = "turma_id")],
        inverseJoinColumns = [JoinColumn(name = "academico_id")]
    )
    // A lista pode ser 'val' pois não vamos trocar a lista,
    // apenas adicionar/remover itens dela (a lista em si é mutável)
    val academicosMatriculados: MutableList<Academico> = mutableListOf()
)