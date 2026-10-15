package com.universidad.reservaslabs.controller;

import com.universidad.reservaslabs.model.Laboratorio;
import com.universidad.reservaslabs.repository.LaboratorioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    private final LaboratorioRepository repo; // catálogo simple, sin reglas de negocio propias

    public LaboratorioController(LaboratorioRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Laboratorio> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Laboratorio> obtener(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Laboratorio> crear(@RequestBody @Valid Laboratorio laboratorio) {
        return ResponseEntity.status(201).body(repo.save(laboratorio));
    }
}
