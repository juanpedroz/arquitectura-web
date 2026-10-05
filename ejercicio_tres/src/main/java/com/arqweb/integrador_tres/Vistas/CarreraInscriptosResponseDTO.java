package com.arqweb.integrador_tres.Vistas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CarreraInscriptosResponseDTO {
    private String nombre;
    private Long duracion;
    private Long cantInscriptos;
}
