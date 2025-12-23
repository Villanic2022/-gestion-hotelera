package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.UsuarioCreateRequest;
import com.miempresa.gestion_hotelera.dto.UsuarioResponse;
import com.miempresa.gestion_hotelera.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * GET /api/usuarios - Listar usuarios del cliente (SOLO ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.listarUsuariosDelCliente();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * POST /api/usuarios/crear-recepcion - Crear usuario RECEPCIÓN (SOLO ADMIN)
     */
    @PostMapping("/crear-recepcion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> crearUsuarioRecepcion(
            @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse usuario = usuarioService.crearUsuarioRecepcion(request);
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/{id}/promover-admin - Promocionar a ADMIN (SOLO ADMIN)
     */
    @PutMapping("/{id}/promover-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> promoverAAdmin(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.promoverAAdmin(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/{id}/activar - Activar usuario (SOLO ADMIN)
     */
    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> activarUsuario(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.cambiarEstado(id, true);
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/{id}/desactivar - Desactivar usuario (SOLO ADMIN)
     */
    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> desactivarUsuario(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.cambiarEstado(id, false);
        return ResponseEntity.ok(usuario);
    }
}