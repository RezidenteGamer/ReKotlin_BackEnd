package com.reKotlin.portalAcademico

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PortalAcademicoApplication

fun main(args: Array<String>) {
	runApplication<PortalAcademicoApplication>(*args)
}

/*
* PROFESSORES:
- Email: joao.silva@professor.com | Senha: 123456
- Email: maria.santos@professor.com | Senha: 123456

ACADÊMICOS:
- Email: pedro.oliveira@aluno.com | Senha: 123456
- Email: ana.costa@aluno.com | Senha: 123456
- Email: lucas.mendes@aluno.com | Senha: 123456
*
* */