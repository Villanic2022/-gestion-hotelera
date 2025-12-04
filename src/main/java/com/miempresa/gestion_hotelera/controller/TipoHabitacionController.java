package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.TipoHabitacionRequest;
import com.miempresa.gestion_hotelera.dto.TipoHabitacionResponse;
import com.miempresa.gestion_hotelera.service.TipoHabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-habitacion")
@RequiredArgsConstructor
public class TipoHabitacionController {

    private final TipoHabitacionService service;

    @PostMapping
    public ResponseEntity<TipoHabitacionResponse> crear(@RequestBody TipoHabitacionRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<TipoHabitacionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacionResponse> actualizar(@PathVariable Long id,
                                                             @RequestBody TipoHabitacionRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}