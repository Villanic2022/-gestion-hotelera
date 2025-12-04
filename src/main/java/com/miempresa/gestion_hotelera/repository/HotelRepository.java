package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCliente_Id(Long clienteId);

    Optional<Hotel> findByIdAndCliente_Id(Long id, Long clienteId);

    boolean existsByIdAndCliente_Id(Long id, Long clienteId);
}
