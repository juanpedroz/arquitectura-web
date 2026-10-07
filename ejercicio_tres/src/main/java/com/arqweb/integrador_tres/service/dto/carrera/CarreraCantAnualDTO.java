package com.arqweb.integrador_tres.service.dto.carrera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarreraCantAnualDTO {
    private String nombre;
    private long duracion;
    private int anio;
    private long cant;
}
