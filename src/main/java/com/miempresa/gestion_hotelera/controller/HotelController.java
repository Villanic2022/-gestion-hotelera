package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.HotelRequest;
import com.miempresa.gestion_hotelera.dto.HotelResponse;
import com.miempresa.gestion_hotelera.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoteles")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelResponse> crear(@RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> listar() {
        return ResponseEntity.ok(hotelService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> actualizar(
            @PathVariable Long id,
            @RequestBody HotelRequest request
    ) {
        return ResponseEntity.ok(hotelService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        hotelService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}