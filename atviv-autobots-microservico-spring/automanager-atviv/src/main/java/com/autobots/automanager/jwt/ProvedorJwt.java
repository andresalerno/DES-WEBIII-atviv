package com.autobots.automanager.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
@Component
public class ProvedorJwt {
	@Value("${jwt.secret}")
	private String assinatura;
	
	@Value("${jwt.expiration}")
	private Long duracao;

	private GeradorJwt gerador;
	private AnalisadorJwt analisador;
	private ValidadorJwt validador;

	public String proverJwt(String nomeUsuario) {
		gerador = new GeradorJwt(assinatura, duracao);
		return gerador.gerarJwt(nomeUsuario);
	}
	
	// ✅ Adicionado para consistência com uso por Authentication
		public String proverJwt(Authentication autenticado) {
			gerador = new GeradorJwt(assinatura, duracao);
			return gerador.gerarToken(autenticado);
		}

	public boolean validarJwt(String jwt) {
		analisador = new AnalisadorJwt(assinatura, jwt);
		validador = new ValidadorJwt();
		return validador.validar(analisador.obterReivindicacoes());
	}

	public String obterNomeUsuario(String jwt) {
		analisador = new AnalisadorJwt(assinatura, jwt);
		Claims reivindicacoes = analisador.obterReivindicacoes();
		return analisador.obterNomeUsuairo(reivindicacoes);
	}
}