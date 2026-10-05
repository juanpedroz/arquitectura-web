package com.arqweb.integrador_tres.service.dto.estudiante.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // Spring lo necesita para convertir el JSON del POST en el DTO
public class EstudianteRequestDTO {

    private String dni;
    private String nombre;
    private String apellido;
    private int edad;
    private String genero;
    private String ciudad;
    private String lu;
}