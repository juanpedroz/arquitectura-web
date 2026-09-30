package com.arqweb.integrador_tres.service.dto.inscripcion.response;

import com.arqweb.integrador_tres.domain.Inscripcion;
import lombok.Data;

import java.util.Date;

@Data

public class InscripcionResponseDTO {

    private final Long id;
    private final String estudianteNombre;
    private final String estudianteApellido;
    private final String estudianteDni;
    private final String carreraNombre;
    private final Date inscripcion;
    private final Date graduacion;
    private final int antiguedad;

    public InscripcionResponseDTO(Inscripcion inscripcion) {
        this.id = inscripcion.getId();
        this.estudianteApellido = inscripcion.getEstudiante().getApellido();
        this.estudianteNombre = inscripcion.getEstudiante().getNombre();
        this.estudianteDni = inscripcion.getEstudiante().getDni();
        this.carreraNombre = inscripcion.getCarrera().getNombre();
        this.inscripcion = inscripcion.getInscripcion();
        this.graduacion = inscripcion.getGraduacion();
        this.antiguedad = inscripcion.getAntiguedad();
    }
}
