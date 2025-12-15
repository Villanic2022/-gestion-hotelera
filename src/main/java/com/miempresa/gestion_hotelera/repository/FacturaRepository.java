package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Todas las facturas de un cliente
    List<Factura> findByClienteId(Long clienteId);

    // Facturas de un cliente en un rango de fechas
    List<Factura> findByClienteIdAndFechaEmisionBetween(
            Long clienteId,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    // Saber si ya existe factura para una reserva (lo usás en emitirFacturaDesdeReserva)
    boolean existsByReservaId(Long reservaId);

    // Obtener la primera factura asociada a una reserva (para GET /facturas/reserva/{id})
    Optional<Factura> findFirstByReservaId(Long reservaId);
}
