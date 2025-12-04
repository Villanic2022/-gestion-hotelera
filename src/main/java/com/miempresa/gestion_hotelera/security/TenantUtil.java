package com.miempresa.gestion_hotelera.security;

import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Usuario;
import com.miempresa.gestion_hotelera.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantUtil {

    private final UsuarioRepository usuarioRepository;

    public Cliente getClienteActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return usuario.getCliente();
    }
}
