package com.arqweb.integrador_tres.service.dto.inscripcion.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InscripcionRequestDTO {
    
    private Long estudianteId;
    private Long carreraId;
    private int inscripcion;
    private int graduacion;
    private int antiguedad;

}
