/**
 * ============================================================================
 * ENTIDADE: Turma
 * ============================================================================
 *
 * Representa uma turma/disciplina no sistema acadêmico.
 * Entidade CENTRAL da aplicação - conecta professores e acadêmicos.
 *
 * REQUISITOS IMPLEMENTADOS:
 *Salvar entidade no banco
 * Relacionamentos entre entidades
 *FUNCIONALIDADES EXTRAS (matrícula, remoção de alunos)
 *
 * RELACIONAMENTOS:
 * - ManyToOne com Professor (muitas turmas → um professor)
 * - ManyToMany com Academico (muitas turmas ↔ muitos acadêmicos)
 *
 * Esta entidade demonstra:
 * - Relacionamentos unidirecionais e bidirecionais
 * - Lazy loading para performance
 * - Join tables para ManyToMany
 * - Mutabilidade controlada (val vs var)
 *
 */

package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.*

/**
 * ============================================================================
 * CLASSE: Turma
 * ============================================================================
 *
 * @Entity - Marca como entidade JPA (será tabela no banco)
 *
 * @property id Identificador único da turma
 * @property nome Nome/título da turma
 * @property descricao Descrição detalhada do conteúdo
 * @property professor Professor responsável pela turma
 * @property academicosMatriculados Lista de alunos matriculados
 */
@Entity
class Turma(
    // ========================================================================
    // CHAVE PRIMÁRIA
    // ========================================================================

    /**
     * ID único da turma.
     *
     * Anotações:
     * @Id - Chave primária
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * - IDENTITY: auto-increment do PostgreSQL (SERIAL)
     *
     * val (imutável):
     * - ID não muda após criação
     * - Referência permanente na tabela
     *
     * Long? (nullable):
     * - null antes de salvar (ainda não gerado)
     * - Long após save() (banco atribui)
     *
     * Exemplo de uso:
     * val turma = Turma(nome = "Programação Web", ...)
     * println(turma.id) // null
     *
     * turmaRepositorio.save(turma)
     * println(turma.id) // 1, 2, 3... (gerado pelo banco)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // ========================================================================
    // CAMPOS BÁSICOS
    // ========================================================================

    /**
     * Nome da turma.
     *
     * Anotações:
     * @Column(nullable = false) - NOT NULL no SQL (obrigatório)
     *
     * var (mutável):
     * - Professor pode editar o nome
     * - Permite correções/atualizações
     *
     * Validação no DTO:
     * @NotBlank(message = "Nome é obrigatório")
     *
     * Usado para:
     * - Exibição em cards e listas
     * - Busca por nome (findByNomeContainingIgnoreCase)
     * - Identificação da turma
     *
     * Exemplos:
     * - "Programação Web Avançada"
     * - "Estrutura de Dados"
     * - "Banco de Dados II"
     */
    @Column(nullable = false)
    var nome: String,

    /**
     * Descrição detalhada da turma.
     *
     * var (mutável):
     * - Professor pode atualizar conteúdo
     * - Pode ser editada ao longo do semestre
     *
     * NÃO tem nullable = false:
     * - Permite string vazia
     * - Campo opcional no formulário
     *
     * Usado para:
     * - Informações sobre o curso
     * - Objetivos de aprendizagem
     * - Pré-requisitos
     * - Metodologia
     *
     * Exemplo:
     * "Curso de desenvolvimento web full-stack usando React no front-end
     *  e Spring Boot no back-end. Aborda conceitos de APIs REST, JPA,
     *  autenticação e deploy."
     */
    var descricao: String,

    // ========================================================================
    // RELACIONAMENTO: ManyToOne (Turma → Professor)
    // ========================================================================

    /**
     * Professor responsável por esta turma.
     *
     * RELACIONAMENTO: Muitas turmas → Um professor
     *
     * Anotações:
     * @ManyToOne - Lado "muitos" da relação OneToMany
     * fetch = FetchType.LAZY - Carregamento sob demanda (performance)
     *
     * @JoinColumn(name = "professor_id")
     * - Define nome da coluna FK no banco
     * - turma.professor_id → professor.id
     *
     * var (mutável):
     * - CORREÇÃO IMPORTANTE!
     * - Permite trocar professor responsável
     * - Necessário para edição de turmas
     *
     * LAZY LOADING:
     * - Professor NÃO é carregado automaticamente
     * - Só busca do banco quando acessar turma.professor
     * - Melhora performance em listagens
     *
     * Como funciona:
     * ```kotlin
     * val turma = turmaRepositorio.findById(1)
     * // SELECT * FROM turma WHERE id = 1
     * // NÃO faz SELECT em professor ainda
     *
     * println(turma.professor.nome)
     * // AGORA faz: SELECT * FROM professor WHERE id = turma.professor_id
     * ```
     *
     * SQL gerado:
     * ALTER TABLE turma
     * ADD COLUMN professor_id BIGINT,
     * ADD FOREIGN KEY (professor_id) REFERENCES usuario(id);
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    var professor: Professor,

    // ========================================================================
    // RELACIONAMENTO: ManyToMany (Turma ↔ Academico)
    // ========================================================================

    /**
     * Lista de acadêmicos matriculados nesta turma.
     *
     * RELACIONAMENTO: Muitas turmas ↔ Muitos acadêmicos
     *
     * Anotações:
     * @ManyToMany - Relação muitos-para-muitos
     *
     * @JoinTable - Define tabela intermediária
     * - name: nome da tabela (turma_academicos)
     * - joinColumns: FK desta entidade (turma_id)
     * - inverseJoinColumns: FK da outra entidade (academico_id)
     *
     * TABELA INTERMEDIÁRIA GERADA:
     * CREATE TABLE turma_academicos (
     *   turma_id BIGINT NOT NULL,
     *   academico_id BIGINT NOT NULL,
     *   PRIMARY KEY (turma_id, academico_id),
     *   FOREIGN KEY (turma_id) REFERENCES turma(id),
     *   FOREIGN KEY (academico_id) REFERENCES usuario(id)
     * );
     *
     * val academicosMatriculados:
     * - val: a LISTA em si não muda (sempre a mesma instância)
     * - MutableList: os ITENS podem mudar (add/remove)
     *
     * mutableListOf():
     * - Inicializa vazia por padrão
     * - Turma nova não tem alunos
     *
     * FUNCIONALIDADE EXTRA #1: Matricular acadêmico
     * ```kotlin
     * turma.academicosMatriculados.add(academico)
     * turmaRepositorio.save(turma)
     * // INSERT INTO turma_academicos (turma_id, academico_id) VALUES (?, ?)
     * ```
     *
     * FUNCIONALIDADE EXTRA #2: Remover acadêmico
     * ```kotlin
     * turma.academicosMatriculados.remove(academico)
     * turmaRepositorio.save(turma)
     * // DELETE FROM turma_academicos WHERE turma_id = ? AND academico_id = ?
     * ```
     *
     * Operações úteis:
     * ```kotlin
     * turma.academicosMatriculados.size         // Conta alunos
     * turma.academicosMatriculados.contains(x)  // Verifica se está matriculado
     * turma.academicosMatriculados.clear()      // Remove todos (cuidado!)
     * ```
     */
    @ManyToMany
    @JoinTable(
        name = "turma_academicos",
        joinColumns = [JoinColumn(name = "turma_id")],
        inverseJoinColumns = [JoinColumn(name = "academico_id")]
    )
    val academicosMatriculados: MutableList<Academico> = mutableListOf()
)

/**
 * ============================================================================
 * DIFERENÇA ENTRE val E var NOS RELACIONAMENTOS
 * ============================================================================
 *
 * CORRETO: var professor
 * ```kotlin
 * var professor: Professor
 *
 * // Permite trocar o professor (necessário para PUT)
 * turma.professor = outroProfessor  // ✅ Funciona
 * ```
 *
 * INCORRETO: val professor
 * ```kotlin
 * val professor: Professor
 *
 * // NÃO permite trocar o professor
 * turma.professor = outroProfessor  // ❌ ERRO: val cannot be reassigned
 * ```
 *
 * RELACIONAMENTO ManyToMany:
 * ```kotlin
 * val academicosMatriculados: MutableList<Academico>
 *
 * // val: não pode trocar a LISTA
 * turma.academicosMatriculados = outraLista  // ❌ ERRO
 *
 * // MÁS pode modificar os ITENS da lista
 * turma.academicosMatriculados.add(academico)     // ✅ OK
 * turma.academicosMatriculados.remove(academico)  // ✅ OK
 * ```
 *
 * REGRA GERAL:
 * - val + MutableList: para coleções (@ManyToMany, @OneToMany)
 * - var: para referências únicas (@ManyToOne, @OneToOne)
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * LAZY LOADING vs EAGER LOADING
 * ============================================================================
 *
 * LAZY (usado em professor):
 * ```kotlin
 * @ManyToOne(fetch = FetchType.LAZY)
 * var professor: Professor
 *
 * val turma = turmaRepositorio.findById(1)
 * // SELECT * FROM turma WHERE id = 1
 *
 * println(turma.nome)  // ✅ Dado já está em memória
 *
 * println(turma.professor.nome)  // Agora busca professor
 * // SELECT * FROM usuario u JOIN professor p WHERE u.id = turma.professor_id
 * ```
 *
 * Vantagens LAZY:
 * ✅ Performance melhor em listagens
 * ✅ Não carrega dados desnecessários
 * ✅ Menos queries ao banco
 *
 * Desvantagens LAZY:
 * ❌ LazyInitializationException se sessão fechar
 * ❌ N+1 queries problem (cuidado!)
 *
 * EAGER:
 * ```kotlin
 * @ManyToOne(fetch = FetchType.EAGER)
 * var professor: Professor
 *
 * val turma = turmaRepositorio.findById(1)
 * // SELECT * FROM turma t
 * // LEFT JOIN usuario u ON t.professor_id = u.id
 * // LEFT JOIN professor p ON u.id = p.id
 * // WHERE t.id = 1
 *
 * println(turma.professor.nome)  // ✅ Já está em memória (sem nova query)
 * ```
 *
 * Vantagens EAGER:
 * ✅ Sem LazyInitializationException
 * ✅ Dados sempre disponíveis
 *
 * Desvantagens EAGER:
 * ❌ Performance pior (sempre busca tudo)
 * ❌ Pode carregar dados desnecessários
 *
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * N+1 QUERIES PROBLEM
 * ============================================================================
 *
 * PROBLEMA:
 * ```kotlin
 * val turmas = turmaRepositorio.findAll()  // 1 query
 *
 * turmas.forEach { turma ->
 *     println(turma.professor.nome)  // N queries (uma por turma!)
 * }
 *
 * // Total: 1 + N queries
 * // Se tem 100 turmas: 101 queries! ❌
 * ```
 *
 * SOLUÇÃO: Join Fetch
 * ```kotlin
 * @Query("SELECT t FROM Turma t JOIN FETCH t.professor")
 * fun findAllWithProfessor(): List<Turma>
 *
 * val turmas = turmaRepositorio.findAllWithProfessor()  // 1 query apenas!
 *
 * turmas.forEach { turma ->
 *     println(turma.professor.nome)  // Sem query adicional ✅
 * }
 * ```
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * ESTRUTURA NO BANCO DE DADOS
 * ============================================================================
 *
 * Tabela: turma
 * ┌────┬──────────────────────┬─────────────┬──────────────┐
 * │ id │ nome                 │ descricao   │ professor_id │
 * ├────┼──────────────────────┼─────────────┼──────────────┤
 * │ 1  │ Programação Web      │ Curso...    │ 1            │
 * │ 2  │ Banco de Dados       │ SQL...      │ 1            │
 * │ 3  │ Estrutura de Dados   │ Algoritmos..│ 3            │
 * └────┴──────────────────────┴─────────────┴──────────────┘
 *
 * Tabela intermediária: turma_academicos
 * ┌──────────┬──────────────┐
 * │ turma_id │ academico_id │
 * ├──────────┼──────────────┤
 * │ 1        │ 2            │ ← Acadêmico 2 matriculado em Turma 1
 * │ 1        │ 4            │ ← Acadêmico 4 matriculado em Turma 1
 * │ 2        │ 2            │ ← Acadêmico 2 matriculado em Turma 2
 * │ 2        │ 4            │ ← Acadêmico 4 matriculado em Turma 2
 * │ 3        │ 4            │ ← Acadêmico 4 matriculado em Turma 3
 * └──────────┴──────────────┘
 *
 * Constraints:
 * - PRIMARY KEY (turma_id, academico_id) → Previne duplicatas
 * - FOREIGN KEY turma_id → turma.id
 * - FOREIGN KEY academico_id → usuario.id
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * OPERAÇÕES CRUD - EXEMPLOS
 * ============================================================================
 *
 * CREATE:
 * ```kotlin
 * val professor = professorRepositorio.findById(1L).get()
 * val turma = Turma(
 *     nome = "Programação Web",
 *     descricao = "Curso full-stack",
 *     professor = professor
 * )
 * turmaRepositorio.save(turma)
 * // INSERT INTO turma (nome, descricao, professor_id) VALUES (?, ?, ?)
 * ```
 *
 * READ:
 * ```kotlin
 * val turma = turmaRepositorio.findById(1L).get()
 * // SELECT * FROM turma WHERE id = 1
 *
 * println(turma.nome)  // Acesso direto
 * println(turma.professor.nome)  // Lazy load (nova query)
 * ```
 *
 * UPDATE:
 * ```kotlin
 * val turma = turmaRepositorio.findById(1L).get()
 * turma.nome = "Programação Web Avançada"
 * turma.descricao = "Nova descrição"
 * turmaRepositorio.save(turma)
 * // UPDATE turma SET nome = ?, descricao = ? WHERE id = ?
 * ```
 *
 * DELETE:
 * ```kotlin
 * turmaRepositorio.deleteById(1L)
 * // DELETE FROM turma_academicos WHERE turma_id = 1 (primeiro!)
 * // DELETE FROM turma WHERE id = 1
 * ```
 *
 * MATRICULAR ACADÊMICO:
 * ```kotlin
 * val turma = turmaRepositorio.findById(1L).get()
 * val academico = academicoRepositorio.findById(2L).get()
 *
 * turma.academicosMatriculados.add(academico)
 * turmaRepositorio.save(turma)
 * // INSERT INTO turma_academicos (turma_id, academico_id) VALUES (1, 2)
 * ```
 *
 * REMOVER ACADÊMICO:
 * ```kotlin
 * val turma = turmaRepositorio.findById(1L).get()
 * val academico = academicoRepositorio.findById(2L).get()
 *
 * turma.academicosMatriculados.remove(academico)
 * turmaRepositorio.save(turma)
 * // DELETE FROM turma_academicos WHERE turma_id = 1 AND academico_id = 2
 * ```
 *
 * ============================================================================
 */