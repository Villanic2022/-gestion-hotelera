package com.miempresa.gestion_hotelera.repository;


import com.miempresa.gestion_hotelera.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReserva_Id(Long reservaId);
}