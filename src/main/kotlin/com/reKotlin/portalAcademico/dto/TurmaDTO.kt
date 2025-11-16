/**
 * ============================================================================
 * DATA TRANSFER OBJECTS (DTOs) - TURMA
 * ============================================================================
 *
 * DTOs são objetos responsáveis por transferir dados entre camadas da aplicação.
 *
 * POR QUE USAR DTOs?
 *
 * 1. SEGURANÇA:
 *    - Evita expor entidades JPA diretamente ao cliente
 *    - Controla exatamente quais dados são enviados/recebidos
 *    - Previne ataques de mass assignment
 *
 * 2. DESACOPLAMENTO:
 *    - Mudanças no banco não afetam o contrato da API
 *    - Mudanças na API não afetam o modelo de dados
 *    - Camadas independentes (mais fácil manutenção)
 *
 * 3. VALIDAÇÃO:
 *    - Valida dados ANTES de chegar na entidade
 *    - Mensagens de erro personalizadas
 *    - Regras de negócio centralizadas
 *
 * 4. PERFORMANCE:
 *    - Envia apenas dados necessários (não lazy loading problems)
 *    - Controle fino sobre serialização JSON
 *
 * Este arquivo define dois DTOs:
 * - TurmaRequestDTO: Recebe dados do front-end (POST/PUT)
 * - TurmaResponseDTO: Envia dados ao front-end (GET)
 *
 */

package com.reKotlin.portalAcademico.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * ============================================================================
 * DTO DE ENTRADA: TurmaRequestDTO
 * ============================================================================
 *
 * Usado para RECEBER dados do front-end quando:
 * - Criando nova turma (POST /api/turmas)
 * - Atualizando turma existente (PUT /api/turmas/:id)
 *
 * REQUISITO IMPLEMENTADO:
 * Salvar/Editar entidade no banco
 *
 * Validações Bean Validation (JSR-303):
 * - Executam ANTES do método do controller
 * - Se falhar: retorna 400 Bad Request automaticamente
 * - Mensagens customizadas para o usuário
 *
 * data class do Kotlin:
 * - Gera automaticamente: equals(), hashCode(), toString(), copy()
 * - Sintaxe concisa e limpa
 * - Imutável por padrão (val)
 *
 * @property nome Nome da turma (obrigatório, não vazio)
 * @property descricao Descrição da turma (opcional, pode ser vazio)
 * @property professorId ID do professor responsável (obrigatório)
 *
 * @constructor Cria um TurmaRequestDTO com validações
 */
data class TurmaRequestDTO(
    /**
     * ------------------------------------------------------------------------
     * CAMPO: nome
     * ------------------------------------------------------------------------
     *
     * Nome da turma a ser criada/atualizada.
     *
     * Validações:
     * @NotBlank - Não pode ser null, vazio ("") ou apenas espaços ("   ")
     *
     * Diferenças de anotações:
     * - @NotNull: Aceita "", aceita "   "
     * - @NotEmpty: Aceita "   "
     * - @NotBlank: Não aceita nenhum dos casos (MELHOR)
     *
     * Mensagem de erro personalizada:
     * Se validação falhar, retorna: "Nome é obrigatório"
     *
     * field: indica que a anotação é para o campo (não para getter/setter)
     *
     * Exemplos válidos: "Programação Web", "Estrutura de Dados"
     * Exemplos inválidos: "", "   ", null
     */
    @field:NotBlank(message = "Nome é obrigatório")
    val nome: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: descricao
     * ------------------------------------------------------------------------
     *
     * Descrição detalhada da turma (conteúdo, objetivos, etc).
     *
     * Validação: NENHUMA (campo opcional)
     *
     * String com valor padrão = "" (string vazia)
     * Se o front-end não enviar este campo, será ""
     *
     * Por que permitir vazio?
     * - Nem toda turma precisa de descrição detalhada
     * - Professor pode adicionar depois
     * - Formulário mais simples para o usuário
     *
     * Exemplos válidos:
     * - "" (vazio)
     * - "Curso de desenvolvimento web full-stack"
     * - null será convertido para "" pelo padrão
     */
    val descricao: String = "",

    /**
     * ------------------------------------------------------------------------
     * CAMPO: professorId
     * ------------------------------------------------------------------------
     *
     * ID do professor que será responsável pela turma.
     *
     * Validações:
     * @NotNull - Não pode ser null
     *
     * Tipo Long (não Long?):
     * - Kotlin obriga a passar valor
     * - Impossível ser null em tempo de compilação
     * - @NotNull é redundante mas documentativo
     *
     * Este ID será usado para:
     * 1. Buscar o professor no banco de dados
     * 2. Validar se o professor existe
     * 3. Vincular a turma ao professor (FK)
     *
     * Validação extra no Serviço:
     * - Verifica se o ID corresponde a um professor real
     * - Verifica se é realmente do tipo PROFESSOR (não acadêmico)
     *
     * Exemplo: 1L (Long literal em Kotlin)
     */
    @field:NotNull(message = "Professor ID é obrigatório")
    val professorId: Long
)

/**
 * ============================================================================
 * DTO DE SAÍDA: TurmaResponseDTO
 * ============================================================================
 *
 * Usado para ENVIAR dados ao front-end quando:
 * - Listando turmas (GET /api/turmas)
 * - Buscando por nome (GET /api/turmas/buscar?nome=X)
 * - Após criar turma (POST retorna dados criados)
 * - Após atualizar turma (PUT retorna dados atualizados)
 *
 * REQUISITO IMPLEMENTADO:
 * Buscar lista de entidades
 * Fazer consulta (por nome)
 *
 * POR QUE SEPARAR REQUEST E RESPONSE?
 *
 * 1. Dados diferentes:
 *    - Request: precisa de professorId para criar
 *    - Response: mostra nomeProfessor (mais amigável)
 *
 * 2. Segurança:
 *    - Response nunca expõe campos sensíveis (senhas, etc)
 *    - Controle fino sobre o que sai da API
 *
 * 3. Flexibilidade:
 *    - Response pode ter campos calculados (quantidadeAlunos)
 *    - Response pode agregar dados de múltiplas entidades
 *
 * @property id ID único da turma (gerado pelo banco)
 * @property nome Nome da turma
 * @property descricao Descrição da turma
 * @property professorId ID do professor (para edição no front-end)
 * @property nomeProfessor Nome do professor (para exibição)
 * @property quantidadeAlunos Número de alunos matriculados (calculado)
 *
 * @constructor Cria um TurmaResponseDTO com todos os dados
 */
data class TurmaResponseDTO(
    /**
     * ------------------------------------------------------------------------
     * CAMPO: id
     * ------------------------------------------------------------------------
     *
     * Identificador único da turma no banco de dados.
     *
     * Tipo: Long
     * Gerado por: GenerationType.IDENTITY (auto-increment do PostgreSQL)
     *
     * Usado para:
     * - Chave primária na URL (GET /api/turmas/5)
     * - Referências entre entidades
     * - Edição (PUT /api/turmas/5)
     * - Exclusão (DELETE /api/turmas/5)
     *
     * IMPORTANTE: Sempre presente em Response, nunca em Request
     * (Request não tem ID pois ainda não foi criado)
     *
     * Exemplo: 5L
     */
    val id: Long,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: nome
     * ------------------------------------------------------------------------
     *
     * Nome da turma.
     *
     * Mesmo campo presente no Request, mas já validado e salvo.
     *
     * Exemplo: "Programação Web Avançada"
     */
    val nome: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: descricao
     * ------------------------------------------------------------------------
     *
     * Descrição detalhada da turma.
     *
     * Pode ser string vazia se não foi preenchida.
     *
     * Front-end deve tratar:
     * if (descricao.isEmpty()) exibir "Sem descrição"
     *
     * Exemplo: "Curso de desenvolvimento full-stack com React e Spring Boot"
     */
    val descricao: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: professorId
     * ------------------------------------------------------------------------
     *
     * ID do professor responsável pela turma.
     *
     * ADICIONADO em atualização posterior!
     * Essencial para modo de edição no front-end.
     *
     * Por que incluir?
     * - Front-end precisa saber qual professor ao editar
     * - Permite validação (só professor responsável pode editar)
     * - Facilita formulário de edição (pré-selecionar professor)
     *
     * Exemplo: 1L
     */
    val professorId: Long,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: nomeProfessor
     * ------------------------------------------------------------------------
     *
     * Nome completo do professor responsável pela turma.
     *
     * MELHOR QUE professorId para EXIBIÇÃO:
     * - Usuário vê "Prof. João Silva" ao invés de "ID: 1"
     * - Mais amigável e legível
     * - Evita requisição extra para buscar nome do professor
     *
     * Este campo é CALCULADO (não existe na tabela turma):
     * - Vem da relação turma.professor.nome
     * - Montado no método turmaParaResponseDTO() do Serviço
     *
     * Front-end exibe diretamente no card:
     * <p>Professor: {turma.nomeProfessor}</p>
     *
     * Exemplo: "Prof. João Silva"
     */
    val nomeProfessor: String,

    /**
     * ------------------------------------------------------------------------
     * CAMPO: quantidadeAlunos
     * ------------------------------------------------------------------------
     *
     * Número total de acadêmicos matriculados nesta turma.
     *
     * CAMPO CALCULADO (não existe no banco):
     * - Calculado a partir de: turma.academicosMatriculados.size
     * - Representa COUNT(*) da tabela turma_academicos
     *
     * Por que incluir?
     * - Informação útil para exibição (cards mostram "5 alunos")
     * - Evita que front-end precise calcular
     * - Evita requisição extra para contar alunos
     *
     * Usado no front-end:
     * <span>{turma.quantidadeAlunos} alunos</span>
     *
     * Tipo: Int (não Long)
     * - Quantidade de alunos raramente ultrapassa Int.MAX_VALUE
     * - Mais semântico (count é inteiro)
     *
     * Exemplo: 15 (15 alunos matriculados)
     */
    val quantidadeAlunos: Int
)

/**
 * ============================================================================
 * EXEMPLO DE USO - FLUXO COMPLETO
 * ============================================================================
 *
 * CENÁRIO: Professor cria uma nova turma
 *
 * 1. FRONT-END envia JSON:
 * POST /api/turmas
 * {
 *   "nome": "Programação Web",
 *   "descricao": "Curso full-stack",
 *   "professorId": 1
 * }
 *
 * 2. SPRING BOOT deserializa para TurmaRequestDTO:
 * TurmaRequestDTO(
 *   nome = "Programação Web",
 *   descricao = "Curso full-stack",
 *   professorId = 1L
 * )
 *
 * 3. VALIDAÇÃO automática:
 * - @NotBlank em nome: OK
 * - @NotNull em professorId: OK
 *
 * 4. CONTROLLER recebe DTO validado:
 * fun criar(@Valid @RequestBody request: TurmaRequestDTO)
 *
 * 5. SERVIÇO converte DTO para Entidade:
 * val turma = Turma(
 *   nome = request.nome,
 *   descricao = request.descricao,
 *   professor = professorRepositorio.findById(request.professorId)
 * )
 *
 * 6. SALVA no banco:
 * val turmaSalva = turmaRepositorio.save(turma)
 * // Retorna: Turma(id=5, nome="Programação Web", ...)
 *
 * 7. SERVIÇO converte Entidade para DTO de resposta:
 * val response = TurmaResponseDTO(
 *   id = turmaSalva.id!!,
 *   nome = turmaSalva.nome,
 *   descricao = turmaSalva.descricao,
 *   professorId = turmaSalva.professor.id!!,
 *   nomeProfessor = turmaSalva.professor.nome,
 *   quantidadeAlunos = turmaSalva.academicosMatriculados.size
 * )
 *
 * 8. SPRING BOOT serializa para JSON:
 * {
 *   "id": 5,
 *   "nome": "Programação Web",
 *   "descricao": "Curso full-stack",
 *   "professorId": 1,
 *   "nomeProfessor": "Prof. João Silva",
 *   "quantidadeAlunos": 0
 * }
 *
 * 9. FRONT-END recebe e exibe:
 * const turma = response.data;
 * console.log(turma.nomeProfessor); // "Prof. João Silva"
 *
 * ============================================================================
 */

/**
 * ============================================================================
 * VALIDAÇÕES BEAN VALIDATION (JSR-303)
 * ============================================================================
 *
 * Anotações disponíveis:
 *
 * STRINGS:
 * - @NotNull: não pode ser null
 * - @NotEmpty: não pode ser null ou vazio ("")
 * - @NotBlank: não pode ser null, vazio ou apenas espaços
 * - @Size(min, max): limita tamanho
 * - @Pattern(regex): valida formato
 * - @Email: valida formato de email
 *
 * NÚMEROS:
 * - @NotNull: não pode ser null
 * - @Min(valor): valor mínimo
 * - @Max(valor): valor máximo
 * - @Positive: deve ser positivo (> 0)
 * - @PositiveOrZero: deve ser >= 0
 *
 * DATAS:
 * - @Future: deve ser no futuro
 * - @Past: deve ser no passado
 * - @FutureOrPresent: futuro ou agora
 * - @PastOrPresent: passado ou agora
 *
 * COLEÇÕES:
 * - @NotEmpty: não pode ser vazia
 * - @Size(min, max): limita tamanho
 *
 * CUSTOM:
 * - Criar sua própria anotação
 * - Implementar ConstraintValidator
 *
 * ============================================================================
 */