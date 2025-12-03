package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canal_tipo_habitacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CanalTipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canal_id", nullable = false)
    private Canal canal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @Column(name = "codigo_externo")
    private String codigoExterno;

    @Column(name = "rateplan_externo")
    private String rateplanExterno;

    private Boolean activo;
}
