package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.HabitacionRequest;
import com.miempresa.gestion_hotelera.dto.HabitacionResponse;
import com.miempresa.gestion_hotelera.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService service;

    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@RequestBody HabitacionRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponse> actualizar(
            @PathVariable Long id,
            @RequestBody HabitacionRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
