package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
// Certifique-se de que os imports para Turma e Usuario estão corretos
import com.reKotlin.portalAcademico.modelo.Turma
import com.reKotlin.portalAcademico.modelo.Usuario

/**
 * ============================================================================
 * ENTIDADE: Academico
 * ============================================================================
 *
 * Representa um estudante/aluno no sistema.
 *
 * FUNCIONALIDADES DE UM ACADÊMICO:
 * - Matricular-se em turmas
 * - Visualizar turmas disponíveis
 * - Ver turmas em que está matriculado
 * - Buscar turmas por nome
 *
 * HERANÇA:
 * - Herda: id, email, nome, senhaPlana (de Usuario)
 * - Adiciona: matricula, turmas
 *
 * @Entity - Marca como entidade JPA
 * @DiscriminatorValue("ACADEMICO") - Valor na coluna tipo_usuario
 *
 * @property matricula Número de matrícula do aluno (identificador acadêmico)
 * @property turmas Lista de turmas em que está matriculado
 */
@Entity
@DiscriminatorValue("ACADEMICO") // Valor que identifica esta classe
class Academico(
    // ========================================================================
    // Parâmetros herdados de Usuario
    // ========================================================================
    id: Long? = null,
    email: String,
    nome: String,
    senhaPlana: String,

    // ========================================================================
    // Campo específico de Academico
    // ========================================================================

    /**
     * Número de matrícula do acadêmico.
     *
     * Identificador único dentro da instituição.
     * Diferente do 'id' (que é PK do banco).
     *
     * Exemplos:
     * - "2024001"
     * - "20241234"
     * - "EST-2024-001"
     *
     * var = mutável (pode ser rematriculado com novo número)
     *
     * Usado para:
     * - Identificação oficial do aluno
     * - Documentos acadêmicos
     * - Busca alternativa (além de email)
     *
     * Possível melhoria futura:
     * @Column(unique = true) // Garantir matricula única
     */
    var matricula: String,

    // ========================================================================
    // Relacionamento: Academico pertence a muitas Turmas
    // ========================================================================

    /**
     * Lista de turmas em que este acadêmico está matriculado.
     *
     * Anotações:
     * @ManyToMany - Um acadêmico tem MUITAS turmas
     * Uma turma tem MUITOS acadêmicos
     * mappedBy = "academicosMatriculados" - Campo na classe Turma
     *
     * Por que ManyToMany?
     * - Acadêmico pode estar em várias turmas
     * - Turma pode ter vários acadêmicos
     * - Tabela intermediária: turma_academicos
     *
     * mappedBy:
     * - Turma é o "dono" da relação (@JoinTable está lá)
     * - Academico apenas visualiza as turmas relacionadas
     *
     * MutableList:
     * - Permite matricular/desmatricular
     * - Vazia por padrão (aluno novo não tem turmas)
     *
     * val turmas:
     * - val: a lista em si não muda
     * - MutableList: os itens podem mudar
     *
     * Uso:
     * academico.turmas.add(turma)  // Matricula em turma
     * academico.turmas.remove(turma)  // Desmatricula
     * academico.turmas.size  // Conta turmas matriculadas
     *
     * SQL equivalente:
     * SELECT t.* FROM turma t
     * INNER JOIN turma_academicos ta ON t.id = ta.turma_id
     * WHERE ta.academico_id = ?
     */
    @ManyToMany(mappedBy = "academicosMatriculados")
    val turmas: MutableList<Turma> = mutableListOf<Turma>() // Especificar o tipo <Turma> é uma boa prática

) : Usuario(id, email, nome, senhaPlana) // Herda de Usuario