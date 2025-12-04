package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_habitacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    private String nombre;
    private String descripcion;
    private Integer capacidadBase;
    private Integer capacidadMax;
    private Boolean activo;
}

