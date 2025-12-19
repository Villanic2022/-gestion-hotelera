package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Facturas de un cliente en un rango de fechas con JOIN FETCH
    @Query("SELECT f FROM Factura f " +
           "LEFT JOIN FETCH f.cliente c " +
           "LEFT JOIN FETCH f.reserva r " +
           "LEFT JOIN FETCH r.huespedTitular h " +
           "LEFT JOIN FETCH r.hotel hotel " +
           "WHERE f.cliente.id = :clienteId " +
           "AND f.fechaEmision BETWEEN :desde AND :hasta")
    List<Factura> findByClienteIdAndFechaEmisionBetweenWithFetch(
            @Param("clienteId") Long clienteId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    // Saber si ya existe factura para una reserva (lo usás en emitirFacturaDesdeReserva)
    boolean existsByReservaId(Long reservaId);

    // Obtener la primera factura asociada a una reserva (para GET /facturas/reserva/{id})
    Optional<Factura> findFirstByReservaId(Long reservaId);

    // Obtener factura por reserva con JOIN FETCH
    @Query("SELECT f FROM Factura f " +
           "LEFT JOIN FETCH f.cliente c " +
           "LEFT JOIN FETCH f.reserva r " +
           "LEFT JOIN FETCH r.huespedTitular h " +
           "LEFT JOIN FETCH r.hotel hotel " +
           "WHERE f.reserva.id = :reservaId")
    Optional<Factura> findFirstByReservaIdWithFetch(@Param("reservaId") Long reservaId);

    // Obtener factura por ID con JOIN FETCH
    @Query("SELECT f FROM Factura f " +
           "LEFT JOIN FETCH f.cliente c " +
           "LEFT JOIN FETCH f.reserva r " +
           "LEFT JOIN FETCH r.huespedTitular h " +
           "LEFT JOIN FETCH r.hotel hotel " +
           "WHERE f.id = :id")
    Optional<Factura> findByIdWithFetch(@Param("id") Long id);
}
