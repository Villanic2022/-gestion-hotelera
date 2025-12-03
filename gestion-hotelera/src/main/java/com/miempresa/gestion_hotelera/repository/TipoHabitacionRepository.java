package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {

    List<TipoHabitacion> findByHotel_Cliente_Id(Long clienteId);

    // un tipo específico, asegurando que sea del cliente
    Optional<TipoHabitacion> findByIdAndHotel_Cliente_Id(Long id, Long clienteId);
}
