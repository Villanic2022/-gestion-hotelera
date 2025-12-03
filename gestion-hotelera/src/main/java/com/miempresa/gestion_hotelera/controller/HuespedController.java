package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.HuespedRequest;
import com.miempresa.gestion_hotelera.dto.HuespedResponse;
import com.miempresa.gestion_hotelera.service.HuespedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
public class HuespedController {

    private final HuespedService service;

    @PostMapping
    public ResponseEntity<HuespedResponse> crear(@RequestBody HuespedRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<HuespedResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HuespedResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HuespedResponse> actualizar(@PathVariable Long id,
                                                      @RequestBody HuespedRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
