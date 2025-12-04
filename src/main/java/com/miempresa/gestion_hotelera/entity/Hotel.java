package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String nombre;
    private String direccion;
    private String ciudad;
    private String pais;
    private String telefono;
    private String email;
    private Boolean activo;

}