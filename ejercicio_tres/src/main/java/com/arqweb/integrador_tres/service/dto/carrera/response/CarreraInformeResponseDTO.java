package com.arqweb.integrador_tres.service.dto.carrera.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarreraInformeResponseDTO {
    private String nombre;
    private long duracion;
    private int anio;
    private long inscriptos;
    private long graduados;
}
