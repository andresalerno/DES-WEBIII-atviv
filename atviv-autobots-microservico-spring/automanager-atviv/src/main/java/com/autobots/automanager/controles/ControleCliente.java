package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.RepositorioCliente;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Controle Cliente", description = "Gerencia os clientes do sistema")
@RestController
//@RequestMapping("/clientes")
@SecurityRequirement(name = "BearerAuth")
public class ControleCliente {

    @Autowired
    private RepositorioCliente repositorio;

    // ✅ CREATE - Apenas ADMIN pode cadastrar clientes diretamente
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        repositorio.save(cliente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // ✅ READ ALL - ADMIN, GERENTE e VENDEDOR podem ver todos os clientes
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/obter-todos")
    public ResponseEntity<List<Cliente>> obterClientes() {
        List<Cliente> clientes = repositorio.findAll();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    // ✅ READ ONE - ADMIN, GERENTE e VENDEDOR podem consultar cliente por ID
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/obter/{id}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable Long id) {
        Optional<Cliente> cliente = repositorio.findById(id);
        return cliente.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ UPDATE - ADMIN e GERENTE podem atualizar qualquer cliente
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente novosDados) {
        Optional<Cliente> existente = repositorio.findById(id);
        if (existente.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Cliente cliente = existente.get();
        cliente.setNome(novosDados.getNome());
        cliente.setCpf(novosDados.getCpf());
        cliente.setDataNascimento(novosDados.getDataNascimento());
 //       cliente.setEndereco(novosDados.getEndereco());
 //       cliente.setTelefones(novosDados.getTelefones());
        cliente.setEmail(novosDados.getEmail());

        repositorio.save(cliente);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ✅ DELETE - Apenas ADMIN pode deletar cliente
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> excluirCliente(@PathVariable Long id) {
        Optional<Cliente> cliente = repositorio.findById(id);
        if (cliente.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        repositorio.delete(cliente.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
