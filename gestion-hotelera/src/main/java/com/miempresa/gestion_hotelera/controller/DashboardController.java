package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.DashboardDiaResponse;
import com.miempresa.gestion_hotelera.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dia")
    public ResponseEntity<DashboardDiaResponse> resumenDia(
            @RequestParam Long hotelId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(dashboardService.resumenDia(hotelId, fecha));
    }
}
