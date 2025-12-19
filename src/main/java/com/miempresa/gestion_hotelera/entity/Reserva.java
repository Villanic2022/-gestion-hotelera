package com.miempresa.gestion_hotelera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.miempresa.gestion_hotelera.entity.EstadoPago;
import com.miempresa.gestion_hotelera.entity.EstadoReserva;

@Entity
@Table(name = "reserva")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadoPago;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion; // puede ser NULL

    private String canal;

    @Column(name = "id_externo")
    private String idExterno;

    @Column(name = "check_in")
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    private Integer adultos;
    private Integer ninos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoReserva estado;


    @Column(name = "precio_total")
    private BigDecimal precioTotal;

    private String moneda;

    @Column(name = "comentarios_cliente")
    private String comentariosCliente;

    @Column(name = "comentarios_internos")
    private String comentariosInternos;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_titular_id")
    private Huesped huespedTitular;

}
