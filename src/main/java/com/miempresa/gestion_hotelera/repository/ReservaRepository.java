package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
           SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
           FROM Reserva r
           WHERE r.habitacion.id = :habitacionId
             AND r.estado IN :estados
             AND r.checkOut > :checkIn
             AND r.checkIn < :checkOut
           """)
    boolean existeSuperposicionReserva(@Param("habitacionId") Long habitacionId,
                                       @Param("estados") List<String> estados,
                                       @Param("checkIn") LocalDate checkIn,
                                       @Param("checkOut") LocalDate checkOut);

    List<Reserva> findByHotel_Id(Long hotelId);
    List<Reserva> findByHotel_Cliente_Id(Long clienteId);

}
