package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cliente")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;   // Nombre del cliente / cuenta (ej: "Cabañas Los Pinos")
    private String email;
    private Boolean activo;

    @OneToMany(mappedBy = "cliente")
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "cliente")
    private List<Hotel> hoteles;
}
