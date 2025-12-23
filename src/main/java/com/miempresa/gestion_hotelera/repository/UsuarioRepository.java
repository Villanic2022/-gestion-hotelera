package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.cliente WHERE u.usuario = :usuario")
    Optional<Usuario> findByUsuarioWithCliente(@Param("usuario") String usuario);

    boolean existsByUsuario(String usuario);

    Optional<Usuario> findByEmail(String email);

    // Usuarios del mismo cliente
    List<Usuario> findByCliente_Id(Long clienteId);
}
