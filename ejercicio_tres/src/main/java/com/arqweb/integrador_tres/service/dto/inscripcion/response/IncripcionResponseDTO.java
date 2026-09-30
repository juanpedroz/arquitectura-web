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
    private final Long estudiante_id;
    private final Long carrera_id;
    private final Date inscripcion;
    private final Date graduacion;
    private final int antiguedad;

    public IncripcionResponseDTO(Inscripcion inscripcion) {
        this.id = inscripcion.getId();
        this.estudiante_id = inscripcion.getEstudiante().getId();
        this.carrera_id = inscripcion.getCarrera().getId();
        this.inscripcion = inscripcion.getInscripcion();
        this.graduacion = inscripcion.getGraduacion();
        this.antiguedad = inscripcion.getAntiguedad();
    }
}
