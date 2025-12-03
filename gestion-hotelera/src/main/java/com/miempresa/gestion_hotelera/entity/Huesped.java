package com.miempresa.gestion_hotelera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "huesped")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String pais;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    private String notas;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
}