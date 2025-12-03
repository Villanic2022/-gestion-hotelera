package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    Optional<Usuario> findByEmail(String email);
}
