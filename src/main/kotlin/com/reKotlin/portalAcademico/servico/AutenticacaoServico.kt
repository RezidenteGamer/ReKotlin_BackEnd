package com.reKotlin.portalAcademico.servico

import com.reKotlin.portalAcademico.dto.LoginRequestDTO
import com.reKotlin.portalAcademico.dto.LoginResponseDTO
import com.reKotlin.portalAcademico.modelo.Academico
import com.reKotlin.portalAcademico.modelo.Professor
import com.reKotlin.portalAcademico.repositorio.UsuarioRepositorio
import org.springframework.stereotype.Service

/**
 * Serviço responsável pela autenticação de usuários.
 * Implementa um sistema de login simplificado (fake) para fins acadêmicos.
 *
 * IMPORTANTE: Em produção, seria necessário usar hash de senhas (BCrypt)
 * e tokens JWT para segurança adequada.
 */
@Service
class AutenticacaoServico(
    private val usuarioRepositorio: UsuarioRepositorio
) {

    /**
     * Realiza o login do usuário verificando email, senha e tipo.
     *
     * @param request Dados de login (email, senha, tipoUsuario)
     * @return LoginResponseDTO com dados do usuário autenticado
     * @throws IllegalArgumentException se credenciais inválidas
     */
    fun fazerLogin(request: LoginRequestDTO): LoginResponseDTO {
        // Busca o usuário pelo email
        val usuario = usuarioRepositorio.findByEmail(request.email)
            ?: throw IllegalArgumentException("Email ou senha incorretos")

        // Verifica a senha (em produção, usar BCrypt.matches())
        if (usuario.senhaPlana != request.senha) {
            throw IllegalArgumentException("Email ou senha incorretos")
        }

        // Verifica o tipo de usuário e faz o cast polimórfico
        return when (request.tipoUsuario) {
            "PROFESSOR" -> {
                val professor = usuario as? Professor
                    ?: throw IllegalArgumentException("Usuário não é um professor")

                LoginResponseDTO(
                    id = professor.id!!,
                    email = professor.email,
                    nome = professor.nome,
                    tipoUsuario = "PROFESSOR",
                    departamento = professor.departamento
                )
            }

            "ACADEMICO" -> {
                val academico = usuario as? Academico
                    ?: throw IllegalArgumentException("Usuário não é um acadêmico")

                LoginResponseDTO(
                    id = academico.id!!,
                    email = academico.email,
                    nome = academico.nome,
                    tipoUsuario = "ACADEMICO",
                    matricula = academico.matricula
                )
            }

            else -> throw IllegalArgumentException("Tipo de usuário inválido")
        }
    }
}