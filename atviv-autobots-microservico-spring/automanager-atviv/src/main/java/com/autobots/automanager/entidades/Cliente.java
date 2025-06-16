package com.autobots.automanager.entidades;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(unique = true)
	private String cpf;

	@Column
	private LocalDate dataNascimento;

//	@OneToOne(cascade = CascadeType.ALL)
//	private Endereco endereco;

//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Telefone> telefones;

	@Column
	private String email;
}
