package com.universidad.reservaslabs.controller;

import com.universidad.reservaslabs.model.Reserva;
import com.universidad.reservaslabs.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) { this.service = service; }

    @GetMapping
    public List<Reserva> listar() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtener(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/laboratorio/{laboratorioId}")
    public List<Reserva> porLaboratorio(@PathVariable Long laboratorioId) {
        return service.findByLaboratorio(laboratorioId);
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody @Valid Reserva reserva) {
        return ResponseEntity.status(201).body(service.crear(reserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
