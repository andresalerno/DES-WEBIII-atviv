package com.autobots.automanager.modelos;

public class LoginDTO {
    private String nomeUsuario;
    private String senha;

    public LoginDTO() {}

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
