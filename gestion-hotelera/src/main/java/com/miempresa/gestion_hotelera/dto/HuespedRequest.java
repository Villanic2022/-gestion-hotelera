package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class HuespedRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String pais;
    private String tipoDocumento;
    private String numeroDocumento;
    private String notas;
}
