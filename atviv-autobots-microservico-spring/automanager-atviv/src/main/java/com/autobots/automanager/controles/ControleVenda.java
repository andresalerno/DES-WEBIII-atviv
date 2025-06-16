package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.ItemVenda;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Controle de Vendas", description = "Gerencia as vendas realizadas no sistema, permitindo o cadastro, consulta e filtragem de vendas por vendedor ou cliente.")
@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/vendas")
public class ControleVenda {

    @Autowired
    private RepositorioVenda repositorio;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @PostMapping(
    		  value = "/cadastrar-venda",
    		  consumes = MediaType.APPLICATION_JSON_VALUE,
    		  produces = MediaType.APPLICATION_JSON_VALUE
    		)
    		public ResponseEntity<?> cadastrarVenda(@RequestBody Venda venda) {
    		    // Garantir que os itens referenciem a venda
    		    for (ItemVenda item : venda.getItens()) {
    		        item.setVenda(venda);
    		    }

    		    repositorio.save(venda);
    		    return new ResponseEntity<>(HttpStatus.CREATED);
    		}




    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @GetMapping("/obter-vendas")
    public ResponseEntity<List<Venda>> obterVendas() {
        return new ResponseEntity<>(repositorio.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('VENDEDOR')")
    @GetMapping("/vendas-do-vendedor/{id}")
    public ResponseEntity<List<Venda>> vendasDoVendedor(@PathVariable Long id) {
        Optional<Usuario> vendedor = repositorioUsuario.findById(id);
        if (vendedor.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<Venda> vendas = repositorio.findByVendedor(vendedor.get());
        return new ResponseEntity<>(vendas, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/compras-do-cliente/{id}")
    public ResponseEntity<List<Venda>> comprasDoCliente(@PathVariable Long id) {
        Optional<Usuario> cliente = repositorioUsuario.findById(id);
        if (cliente.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<Venda> compras = repositorio.findByCliente(cliente.get());
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }
}
