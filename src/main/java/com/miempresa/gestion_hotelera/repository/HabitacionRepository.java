package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByHotel_Id(Long hotelId);

    // NUEVO: todas las habitaciones de todos los hoteles de un cliente
    List<Habitacion> findByHotel_Cliente_Id(Long clienteId);
}