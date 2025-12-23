package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.UsuarioCreateRequest;
import com.miempresa.gestion_hotelera.dto.UsuarioResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Rol;
import com.miempresa.gestion_hotelera.entity.Usuario;
import com.miempresa.gestion_hotelera.repository.RolRepository;
import com.miempresa.gestion_hotelera.repository.UsuarioRepository;
import com.miempresa.gestion_hotelera.security.TenantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantUtil tenantUtil;

    /**
     * ADMIN puede promocionar usuarios de su mismo cliente a ADMIN
     */
    @Transactional
    public UsuarioResponse promoverAAdmin(Long usuarioId) {
        Cliente clienteActual = tenantUtil.getClienteActual();
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Verificar que pertenezca al mismo cliente
        if (!usuario.getCliente().getId().equals(clienteActual.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No puede modificar usuarios de otro cliente");
        }

        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

        // Agregar rol ADMIN (manteniendo otros roles)
        usuario.getRoles().add(rolAdmin);
        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    /**
     * ADMIN puede crear usuarios RECEPCION en su mismo cliente
     */
    @Transactional
    public UsuarioResponse crearUsuarioRecepcion(UsuarioCreateRequest request) {
        Cliente clienteActual = tenantUtil.getClienteActual();

        if (usuarioRepository.existsByUsuario(request.getUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }

        Rol rolRecepcion = rolRepository.findByNombre("RECEPCION")
                .orElseThrow(() -> new RuntimeException("Rol RECEPCION no encontrado"));

        Usuario usuario = Usuario.builder()
                .usuario(request.getUsuario())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .activo(true)
                .cliente(clienteActual) // Mismo cliente que el ADMIN
                .roles(Set.of(rolRecepcion))
                .build();

        usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    /**
     * Listar usuarios del mismo cliente
     */
    public List<UsuarioResponse> listarUsuariosDelCliente() {
        Cliente clienteActual = tenantUtil.getClienteActual();
        
        return usuarioRepository.findByCliente_Id(clienteActual.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Activar/Desactivar usuario del mismo cliente
     */
    @Transactional
    public UsuarioResponse cambiarEstado(Long usuarioId, boolean activo) {
        Cliente clienteActual = tenantUtil.getClienteActual();
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!usuario.getCliente().getId().equals(clienteActual.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No puede modificar usuarios de otro cliente");
        }

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .usuario(usuario.getUsuario())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .roles(usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet()))
                .build();
    }
}