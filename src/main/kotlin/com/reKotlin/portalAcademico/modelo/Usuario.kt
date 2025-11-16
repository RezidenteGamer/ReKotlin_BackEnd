/**
 * ============================================================================
 * ENTIDADE: Usuario (CLASSE BASE)
 * ============================================================================
 *
 * Classe abstrata que representa o conceito genérico de "usuário" no sistema.
 *
 * REQUISITO IMPLEMENTADO:
 * Usar elementos de HERANÇA
 * Usar elementos de POLIMORFISMO
 *
 * PADRÃO DE DESIGN: Herança de Tabela Única (Single Table Inheritance)
 *
 * Por que usar herança?
 * 1. REUTILIZAÇÃO DE CÓDIGO:
 *    - Campos comuns (id, email, nome, senha) definidos uma vez
 *    - Subclasses herdam automaticamente
 *
 * 2. POLIMORFISMO:
 *    - Variável tipo Usuario pode armazenar Professor ou Academico
 *    - Útil para consultas genéricas (ex: buscar por email)
 *
 * 3. ORGANIZAÇÃO:
 *    - Hierarquia clara: Usuario > Professor/Academico
 *    - Facilita entendimento do domínio
 *
 * ESTRATÉGIA JPA ESCOLHIDA: JOINED
 *
 * Como funciona:
 * - Tabela 'usuario': campos comuns (id, email, nome, senha)
 * - Tabela 'professor': campos específicos (departamento)
 * - Tabela 'academico': campos específicos (matricula)
 * - JOIN entre tabelas para buscar dados completos
 *
 * Estrutura no banco:
 *
 * usuario              professor           academico
 * id (PK)              id (PK, FK)         id (PK, FK)
 * tipo_usuario         departamento        matricula
 * email
 * nome
 * senha_plana
 *
 */

package com.reKotlin.portalAcademico.modelo

import jakarta.persistence.*

/**
 * ============================================================================
 * CLASSE: Usuario
 * ============================================================================
 *
 * Anotações JPA explicadas:
 *
 * @Entity
 * Marca como entidade JPA (será mapeada para tabela)
 *
 * @Inheritance(strategy = InheritanceType.JOINED)
 * Define estratégia de herança no banco de dados:
 * - JOINED: Tabela separada para cada classe (melhor normalização)
 * - SINGLE_TABLE: Tudo em uma tabela (mais simples, menos normalizado)
 * - TABLE_PER_CLASS: Tabela completa para cada classe (redundante)
 *
 * @DiscriminatorColumn(name = "tipo_usuario")
 * Coluna que identifica qual subclasse representa cada linha
 * Valores: "PROFESSOR" ou "ACADEMICO"
 *
 * open class
 * Kotlin: classes são final por padrão
 * 'open' permite que seja herdada (necessário para Professor/Academico)
 *
 * @param id Identificador único do usuário
 * @param email Email de login (único no sistema)
 * @param nome Nome completo do usuário
 * @param senhaPlana Senha em texto plano (⚠️ APENAS PARA PROJETO ACADÊMICO!)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
open class Usuario(
    /**
     * ------------------------------------------------------------------------
     * CAMPO: id (Chave Primária)
     * ------------------------------------------------------------------------
     *
     * Identificador único de cada usuário no banco de dados.
     *
     * Anotações:
     * @Id - Marca como chave primária
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * - IDENTITY: Usa auto-increment do banco (PostgreSQL: SERIAL)
     * - Banco gera o ID automaticamente ao inserir
     *
     * Tipo: Long? (nullable)
     * - null antes de salvar (ainda não tem ID)
     * - Long após salvar (banco atribui ID)
     *
     * open val
     * - open: permite override em subclasses (se necessário)
     * - val: imutável (ID não muda após criação)
     *
     * Exemplo de uso:
     * val usuario = Usuario(...)
     * println(usuario.id) // null
     *
     * usuarioRepositorio.save(usuario)
     * println(usuario.id) // 1, 2, 3... (gerado pelo banco)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: email (Identificador de Login)
     * ------------------------------------------------------------------------
     *
     * Email do usuário, usado para login no sistema.
     *
     * Anotações:
     * @Column(nullable = false, unique = true)
     * - nullable = false: NOT NULL no SQL (obrigatório)
     * - unique = true: UNIQUE constraint (não pode repetir)
     *
     * Por que unique?
     * - Cada usuário tem email exclusivo
     * - Previne duplicatas
     * - Permite buscar por email (usuarioRepositorio.findByEmail)
     *
     * open var
     * - open: permite override se necessário
     * - var: mutável (usuário pode mudar email)
     *
     * Validação no DTO (TurmaRequestDTO não tem, mas LoginDTO teria):
     * @Email(message = "Email inválido")
     *
     * Exemplos válidos:
     * - "professor@email.com"
     * - "aluno@universidade.br"
     *
     * Exemplos inválidos:
     * - "" (nullable = false impede)
     * - Email duplicado (unique = true impede)
     */
    @Column(nullable = false, unique = true)
    open var email: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: nome (Nome Completo)
     * ------------------------------------------------------------------------
     *
     * Nome completo do usuário (exibido na interface).
     *
     * Anotações:
     * @Column(nullable = false)
     * - Obrigatório no banco
     *
     * open var
     * - Mutável: usuário pode atualizar nome
     *
     * Usado para:
     * - Exibição na navbar: "Bem-vindo, João Silva"
     * - Cards de turmas: "Professor: Maria Santos"
     * - Listas de alunos matriculados
     *
     * Exemplos:
     * - "Prof. João Silva"
     * - "Maria Santos"
     * - "Pedro Oliveira"
     */
    @Column(nullable = false)
    open var nome: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: senhaPlana (Senha em Texto Plano)
     * ------------------------------------------------------------------------
     * Anotações:
     * @Column(nullable = false)
     * - Senha é obrigatória
     *
     * open var
     * - Mutável: permite troca de senha
     *
     * Validação no DTO:
     * @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
     */
    @Column(nullable = false)
    open var senhaPlana: String
)

/**
 * ============================================================================
 * POLIMORFISMO EM AÇÃO
 * ============================================================================
 *
 * Exemplo de consulta polimórfica:
 *
 * ```kotlin
 * // UsuarioRepositorio.kt
 * fun findByEmail(email: String): Usuario?
 *
 * // Uso:
 * val usuario: Usuario? = usuarioRepositorio.findByEmail("joao@email.com")
 *
 * // 'usuario' é do tipo Usuario, mas pode ser Professor ou Academico!
 * when (usuario) {
 *     is Professor -> println("É professor do depto: ${usuario.departamento}")
 *     is Academico -> println("É acadêmico com matrícula: ${usuario.matricula}")
 *     null -> println("Usuário não encontrado")
 * }
 * ```
 *
 * O JPA sabe qual classe instanciar baseado em 'tipo_usuario':
 * - Se tipo_usuario = "PROFESSOR" → instancia Professor
 * - Se tipo_usuario = "ACADEMICO" → instancia Academico
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * ESTRATÉGIAS DE HERANÇA JPA (Comparação)
 * ============================================================================
 *
 * 1. JOINED (escolhida aqui)
 * Prós:
 * - Normalização perfeita (sem redundância)
 * - Campos específicos em tabelas separadas
 * - Fácil adicionar novos tipos
 * Contras:
 * - Requer JOIN (pode ser mais lento)
 * - Estrutura mais complexa
 *
 * 2. SINGLE_TABLE
 * Prós:
 * - Performance (sem JOIN)
 * - Simples de entender
 * Contras:
 * - Todas subclasses na mesma tabela
 * - Colunas nullable (departamento null para acadêmico)
 * - Pode ficar com MUITAS colunas
 *
 * 3. TABLE_PER_CLASS
 * Prós:
 * - Cada classe é independente
 * Contras:
 * - Duplicação de colunas comuns
 * - Consultas polimórficas complexas (UNION)
 * - Geralmente evitada
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * EXEMPLO DE SQL GERADO (JOINED)
 * ============================================================================
 *
 * CREATE TABLE usuario (
 *   id BIGSERIAL PRIMARY KEY,
 *   tipo_usuario VARCHAR(31) NOT NULL,  -- PROFESSOR ou ACADEMICO
 *   email VARCHAR(255) NOT NULL UNIQUE,
 *   nome VARCHAR(255) NOT NULL,
 *   senha_plana VARCHAR(255) NOT NULL
 * );
 *
 * CREATE TABLE professor (
 *   id BIGINT PRIMARY KEY,
 *   departamento VARCHAR(255) NOT NULL,
 *   FOREIGN KEY (id) REFERENCES usuario(id)
 * );
 *
 * CREATE TABLE academico (
 *   id BIGINT PRIMARY KEY,
 *   matricula VARCHAR(255) NOT NULL,
 *   FOREIGN KEY (id) REFERENCES usuario(id)
 * );
 *
 * -- Buscar professor completo:
 * SELECT u.*, p.departamento
 * FROM usuario u
 * INNER JOIN professor p ON u.id = p.id
 * WHERE u.id = 1;
 *
 * -- Buscar acadêmico completo:
 * SELECT u.*, a.matricula
 * FROM usuario u
 * INNER JOIN academico a ON u.id = a.id
 * WHERE u.id = 2;
 *
 * -- Buscar qualquer usuário por email (polimorfismo):
 * SELECT u.*
 * FROM usuario u
 * WHERE u.email = 'joao@email.com';
 * -- JPA faz JOIN automaticamente se necessário
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * CICLO DE VIDA DA ENTIDADE
 * ============================================================================
 *
 * Estados de uma entidade JPA:
 *
 * 1. TRANSIENT (Novo)
 *    val usuario = Usuario(...)
 *    - Objeto existe apenas na memória
 *    - JPA não sabe da existência
 *    - id = null
 *
 * 2. MANAGED (Gerenciado)
 *    usuarioRepositorio.save(usuario)
 *    - JPA está rastreando mudanças
 *    - Qualquer alteração será sincronizada com banco
 *    - id != null
 *
 * 3. DETACHED (Desanexado)
 *    entityManager.detach(usuario)
 *    - Objeto existe mas JPA não rastreia
 *    - Mudanças não são sincronizadas
 *
 * 4. REMOVED (Removido)
 *    usuarioRepositorio.delete(usuario)
 *    - Marcado para exclusão
 *    - Será deletado do banco no flush
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * BOAS PRÁTICAS DE SEGURANÇA (Para Produção)
 * ============================================================================
 *
 * 1. SENHAS:
 *    ❌ var senhaPlana: String
 *    ✅ var senhaHash: String (BCrypt, Argon2)
 *
 * 2. VALIDAÇÃO DE EMAIL:
 *    ✅ @Email annotation
 *    ✅ Verificação de domínio
 *    ✅ Confirmação por email
 *
 * 3. AUDITORIA:
 *    ✅ val dataCriacao: LocalDateTime
 *    ✅ val dataAtualizacao: LocalDateTime
 *    ✅ val criadoPor: String
 *
 * 4. SOFT DELETE:
 *    ✅ var ativo: Boolean = true
 *    - Não deleta fisicamente
 *    - Apenas marca como inativo
 *
 * 5. ROLES/PERMISSÕES:
 *    ✅ @ManyToMany var roles: List<Role>
 *    - Controle fino de permissões
 *    - Spring Security integration
 *
 * 6. TOKEN DE SESSÃO:
 *    ✅ var refreshToken: String?
 *    ✅ var tokenExpiration: LocalDateTime?
 *
 * ============================================================================
 */