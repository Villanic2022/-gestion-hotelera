package com.miempresa.gestion_hotelera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(length = 20)
    private String cuit;

    @Column(length = 20)
    private String condicionIva; // RI, MONOTRIBUTO, EXENTO, CONSUMIDOR_FINAL, etc.

    @Column(length = 255)
    private String domicilioFiscal;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Hotel> hoteles;
}
