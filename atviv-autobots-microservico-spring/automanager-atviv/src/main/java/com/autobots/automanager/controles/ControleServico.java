package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.repositorios.RepositorioServico;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Serviços", description = "Gerenciamento de serviços oferecidos pela empresa")
@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/servicos")
public class ControleServico {

    @Autowired
    private RepositorioServico repositorio;

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @PostMapping("/cadastrar-servico")
    public ResponseEntity<?> cadastrarServico(@RequestBody Servico servico) {
        repositorio.save(servico);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/obter-servicos")
    public ResponseEntity<List<Servico>> obterServicos() {
        return new ResponseEntity<>(repositorio.findAll(), HttpStatus.OK);
    }

    // ➕ Obter um serviço específico
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Servico> obterServicoPorId(@PathVariable Long id) {
        Optional<Servico> servico = repositorio.findById(id);
        if (servico.isPresent()) {
            return new ResponseEntity<>(servico.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ❌ Deletar serviço
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarServico(@PathVariable Long id) {
        Optional<Servico> servico = repositorio.findById(id);
        if (servico.isPresent()) {
            repositorio.delete(servico.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
