package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.repositorios.RepositorioMercadoria;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Mercadorias", description = "Gerenciamento de mercadorias")
@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/mercadorias")
public class ControleMercadoria {

    @Autowired
    private RepositorioMercadoria repositorio;

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @PostMapping("/cadastrar-mercadoria")
    public ResponseEntity<?> cadastrarMercadoria(@RequestBody Mercadoria mercadoria) {
        repositorio.save(mercadoria);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/obter-mercadorias")
    public ResponseEntity<List<Mercadoria>> obterMercadorias() {
        return new ResponseEntity<>(repositorio.findAll(), HttpStatus.OK);
    }

    // ➕ Obter uma mercadoria específica
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Mercadoria> obterMercadoriaPorId(@PathVariable Long id) {
        Optional<Mercadoria> mercadoria = repositorio.findById(id);
        if (mercadoria.isPresent()) {
            return new ResponseEntity<>(mercadoria.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ❌ Deletar mercadoria
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarMercadoria(@PathVariable Long id) {
        Optional<Mercadoria> mercadoria = repositorio.findById(id);
        if (mercadoria.isPresent()) {
            repositorio.delete(mercadoria.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarMercadoria(@PathVariable Long id, @RequestBody Mercadoria novosDados) {
        Optional<Mercadoria> existente = repositorio.findById(id);
        if (existente.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Mercadoria mercadoria = existente.get();
        mercadoria.setNome(novosDados.getNome());
        mercadoria.setDescricao(novosDados.getDescricao());
        mercadoria.setPreco(novosDados.getPreco());
        mercadoria.setQuantidadeEstoque(novosDados.getQuantidadeEstoque());
        // ⚠️ Evite setar itensDeVenda diretamente se não estiver usando DTOs

        repositorio.save(mercadoria);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
