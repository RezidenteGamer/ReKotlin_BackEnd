package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.*

/**
 * ============================================================================
 * ENTIDADE: Professor
 * ============================================================================
 *
 * Representa um professor/docente no sistema.
 *
 * FUNCIONALIDADES DE UM PROFESSOR:
 * - Criar turmas
 * - Editar turmas que criou
 * - Excluir turmas que criou
 * - Remover acadêmicos de suas turmas
 * - Visualizar todas as turmas
 *
 * HERANÇA:
 * - Herda: id, email, nome, senhaPlana (de Usuario)
 * - Adiciona: departamento, turmasCriadas
 *
 * @Entity - Marca como entidade JPA
 * @DiscriminatorValue("PROFESSOR") - Valor na coluna tipo_usuario
 *
 * @property departamento Departamento/Faculdade do professor
 * @property turmasCriadas Lista de turmas criadas por este professor
 */
@Entity
@DiscriminatorValue("PROFESSOR")
class Professor(
    // ========================================================================
    // Parâmetros herdados de Usuario (devem ser repassados)
    // ========================================================================
    id: Long? = null,
    email: String,
    nome: String,
    senhaPlana: String,

    // ========================================================================
    // Campo específico de Professor
    // ========================================================================

    /**
     * Departamento/Faculdade a que o professor pertence.
     *
     * Exemplos:
     * - "Ciência da Computação"
     * - "Engenharia de Software"
     * - "Matemática"
     *
     * var = mutável (professor pode trocar de departamento)
     *
     * Usado para:
     * - Organização institucional
     * - Filtros de busca (futuro)
     * - Estatísticas por departamento
     */
    var departamento: String,

    // ========================================================================
    // Relacionamento: Professor possui muitas Turmas
    // ========================================================================

    /**
     * Lista de turmas criadas/gerenciadas por este professor.
     *
     * Anotações:
     * @OneToMany - Um professor tem MUITAS turmas
     * mappedBy = "professor" - Campo na classe Turma que mapeia este relacionamento
     *
     * Por que mappedBy?
     * - Indica que Turma é o "dono" da relação (tem FK)
     * - Professor apenas visualiza turmas relacionadas
     * - Evita tabela intermediária desnecessária
     *
     * MutableList:
     * - Permite adicionar/remover turmas
     * - Inicializada vazia por padrão
     *
     * val turmasCriadas:
     * - val (imutável): a LISTA em si não muda
     * - MutableList: os ITENS da lista podem mudar
     *
     * Uso:
     * professor.turmasCriadas.add(novaTurma)  // Adiciona turma
     * professor.turmasCriadas.size  // Conta turmas
     *
     * SQL equivalente:
     * SELECT * FROM turma WHERE professor_id = ?
     */
    @OneToMany(mappedBy = "professor")
    val turmasCriadas: MutableList<Turma> = mutableListOf()

) : Usuario(id, email, nome, senhaPlana) // Chama construtor da superclasse