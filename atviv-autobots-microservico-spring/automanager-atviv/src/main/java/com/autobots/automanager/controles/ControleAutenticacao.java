package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.jwt.GeradorJwt;
import com.autobots.automanager.jwt.ProvedorJwt;
import com.autobots.automanager.modelos.LoginDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Controle de Autenticação", description = "Gerencia a autenticação de usuários na API")
@RestController
@RequestMapping("/auth")
public class ControleAutenticacao {

    @Autowired
    private AuthenticationManager autenticador;

    @Autowired
    private ProvedorJwt provedorJwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        UsernamePasswordAuthenticationToken creds =
                new UsernamePasswordAuthenticationToken(login.getNomeUsuario(), login.getSenha());

        Authentication autenticado = autenticador.authenticate(creds);
        String token = provedorJwt.proverJwt(autenticado.getName());


        return ResponseEntity.ok(token);
    }
}
