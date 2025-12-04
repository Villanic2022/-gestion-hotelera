package com.miempresa.gestion_hotelera.entity;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReservaHuespedId implements Serializable {
    private Long reserva;
    private Long huesped;
}

