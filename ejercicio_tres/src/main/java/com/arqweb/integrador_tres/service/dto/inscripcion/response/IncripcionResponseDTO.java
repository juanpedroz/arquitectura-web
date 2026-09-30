package com.arqweb.integrador_tres.service.dto.inscripcion.response;

import com.arqweb.integrador_tres.domain.Carrera;
import com.arqweb.integrador_tres.domain.Estudiante;
import com.arqweb.integrador_tres.domain.Inscripcion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data

public class IncripcionResponseDTO {

    private final Long id;
    private final String estudianteNombre;
    private final String estudianteApellido;
    private final String estudianteDni;
    private final String carreraNombre;
    private final Date inscripcion;
    private final Date graduacion;
    private final int antiguedad;

    public IncripcionResponseDTO(Inscripcion inscripcion) {
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
