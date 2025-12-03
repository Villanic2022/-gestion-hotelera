package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habitacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    private String codigo;
    private String piso;
    private String estado;
    private Boolean activo;
}
