package com.arqweb.integrador_tres.service.dto.estudiante;

import com.arqweb.integrador_tres.domain.Estudiante;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDTO {

    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private int edad;
    private String genero;
    private String ciudad;
    private String lu;

    public EstudianteDTO(Estudiante e) {
        this.id = e.getId();
        this.dni = e.getDni();
        this.nombre = e.getNombre();
        this.apellido = e.getApellido();
        this.edad = e.getEdad();
        this.genero = e.getGenero();
        this.ciudad = e.getCiudad();
        this.lu = e.getLu();
    }
}
