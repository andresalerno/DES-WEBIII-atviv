package com.autobots.automanager.entidades;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.List;

@Data
@Entity
public class Mercadoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private Double preco;
    private int quantidadeEstoque;

    @OneToMany(mappedBy = "mercadoria", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ItemVenda> itensDeVenda;
}
