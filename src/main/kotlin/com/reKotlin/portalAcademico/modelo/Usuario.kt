package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia de Herança
@DiscriminatorColumn(name = "tipo_usuario") // Coluna que identifica o tipo
open class Usuario( // 'open' permite que a classe seja herdada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    @Column(nullable = false, unique = true)
    open var email: String,

    @Column(nullable = false)
    open var nome: String,

    @Column(nullable = false)
    open var senhaPlana: String // Simplificado. Em um projeto real, usaríamos hash (Spring Security)
)