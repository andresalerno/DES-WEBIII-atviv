package com.autobots.automanager.jwt;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class GeradorJwt {
	private String assinatura;
	private long duracao;

	// ✅ Construtor necessário para uso no ProvedorJwt
	public GeradorJwt(String assinatura, long duracao) {
		this.assinatura = assinatura;
		this.duracao = duracao;
	}

	// ✅ Geração de token a partir do nome do usuário
	public String gerarJwt(String nomeUsuario) {
		Date expiracao = new Date(System.currentTimeMillis() + duracao);
		return Jwts.builder()
				.setSubject(nomeUsuario)
				.setExpiration(expiracao)
				.signWith(SignatureAlgorithm.HS512, assinatura.getBytes())
				.compact();
	}

	// ✅ Geração de token a partir do Authentication
	public String gerarToken(Authentication autenticado) {
		UserDetails usuario = (UserDetails) autenticado.getPrincipal();
		return gerarJwt(usuario.getUsername());
	}
}
