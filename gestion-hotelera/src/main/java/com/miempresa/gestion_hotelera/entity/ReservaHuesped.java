package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reserva_huesped")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(ReservaHuespedId.class)
public class ReservaHuesped {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_id")
    private Huesped huesped;

    @Column(name = "es_titular")
    private Boolean esTitular;
}