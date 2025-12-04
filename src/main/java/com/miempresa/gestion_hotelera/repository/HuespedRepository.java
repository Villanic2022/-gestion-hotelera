package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Long> {
    Optional<Huesped> findByEmail(String email);

    Optional<Huesped> findByNumeroDocumento(String numeroDocumento);

    List<Huesped> findByCliente_Id(Long clienteId);

    Optional<Huesped> findByIdAndCliente_Id(Long id, Long clienteId);

    boolean existsByIdAndCliente_Id(Long id, Long clienteId);
}