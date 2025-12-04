package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.ReservaHuesped;
import com.miempresa.gestion_hotelera.entity.ReservaHuespedId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaHuespedRepository extends JpaRepository<ReservaHuesped, ReservaHuespedId> {

    // Para obtener todos los huéspedes (titular + acompañantes) de una reserva
    List<ReservaHuesped> findByReserva_Id(Long reservaId);
}
