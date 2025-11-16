/**
 * ============================================================================
 * REPOSITÓRIOS - CAMADA DE ACESSO AOS DADOS
 * ============================================================================
 *
 * Interfaces que estendem JpaRepository para acesso ao banco de dados.
 *
 * PADRÃO: Repository Pattern
 * - Abstrai lógica de acesso ao banco
 * - Spring Data JPA gera implementação automaticamente
 * - Métodos CRUD prontos sem código
 *
 * REQUISITOS IMPLEMENTADOS:
 * Buscar lista de entidades
 * Fazer consulta (por nome, email, etc)
 * Salvar/Editar/Excluir entidades
 *
 * MAGIA DO SPRING DATA JPA:
 * - Você define apenas a interface
 * - Spring cria a implementação em runtime
 * - Query methods por convenção de nomes
 * - Sem SQL manual necessário
 *
 */

package com.reKotlin.portalAcademico.repositorio

import com.reKotlin.portalAcademico.modelo.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * ============================================================================
 * REPOSITÓRIO: TurmaRepositorio
 * ============================================================================
 *
 * Repositório principal da aplicação - gerencia operações com turmas.
 *
 * REQUISITO IMPLEMENTADO:
 * Fazer consulta por nome
 *
 * @Repository - Marca como componente Spring (opcional com JpaRepository)
 *
 * JpaRepository<Turma, Long>:
 * - Turma: tipo da entidade gerenciada
 * - Long: tipo da chave primária (id)
 *
 * MÉTODOS HERDADOS AUTOMATICAMENTE:
 * - save(turma): Salva ou atualiza (INSERT ou UPDATE)
 * - findById(id): Busca por ID (SELECT WHERE id = ?)
 * - findAll(): Busca todas (SELECT * FROM turma)
 * - deleteById(id): Exclui por ID (DELETE WHERE id = ?)
 * - count(): Conta registros (SELECT COUNT(*))
 * - existsById(id): Verifica se existe (SELECT 1 WHERE id = ?)
 * - E muitos outros...
 *
 * TOTAL: 18+ métodos prontos para usar!
 */
@Repository
interface TurmaRepositorio : JpaRepository<Turma, Long> {

    /**
     * ------------------------------------------------------------------------
     * QUERY METHOD: Buscar turmas por nome (parcial, case-insensitive)
     * ------------------------------------------------------------------------
     *
     * REQUISITO IMPLEMENTADO:
     * Fazer uma consulta (por nome)
     *
     * CONVENÇÃO DE NOMES (Query Method):
     * - findBy: inicia query de busca
     * - Nome: campo da entidade (turma.nome)
     * - Containing: operador LIKE %valor%
     * - IgnoreCase: case-insensitive (UPPER/LOWER)
     *
     * SQL GERADO AUTOMATICAMENTE:
     * SELECT * FROM turma
     * WHERE UPPER(nome) LIKE UPPER(CONCAT('%', ?1, '%'))
     *
     * Funcionamento:
     * ```kotlin
     * // Buscar turmas com "web" no nome
     * val turmas = turmaRepositorio.findByNomeContainingIgnoreCase("web")
     *
     * // Retorna:
     * // - "Programação Web"
     * // - "Desenvolvimento Web Avançado"
     * // - "Web Design"
     * ```
     *
     * Outros exemplos de Query Methods:
     * - findByNome(nome: String): WHERE nome = ?
     * - findByNomeStartingWith(prefix: String): WHERE nome LIKE 'prefix%'
     * - findByNomeEndingWith(suffix: String): WHERE nome LIKE '%suffix'
     * - findByDescricaoIsNotNull(): WHERE descricao IS NOT NULL
     * - findByProfessor(professor: Professor): WHERE professor_id = ?
     *
     * @param nome Termo de busca (parcial)
     * @return Lista de turmas que contém o termo no nome
     */
    fun findByNomeContainingIgnoreCase(nome: String): List<Turma>
}