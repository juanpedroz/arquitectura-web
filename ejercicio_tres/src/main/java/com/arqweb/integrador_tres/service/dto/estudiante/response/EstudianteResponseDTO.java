package com.arqweb.integrador_tres.service.dto.estudiante.response;

import com.arqweb.integrador_tres.domain.Estudiante;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstudianteResponseDTO {

    private final Long id;
    private final String dni;
    private final String nombre;
    private final String apellido;
    private final int edad;
    private final String genero;
    private final String ciudad;
    private final String lu;

    public EstudianteResponseDTO(Estudiante estudiante) {
        this.id = estudiante.getId();
        this.dni = estudiante.getDni();
        this.nombre = estudiante.getNombre();
        this.apellido = estudiante.getApellido();
        this.edad = estudiante.getEdad();
        this.genero = estudiante.getGenero();
        this.ciudad = estudiante.getCiudad();
        this.lu = estudiante.getLu();
    }
}