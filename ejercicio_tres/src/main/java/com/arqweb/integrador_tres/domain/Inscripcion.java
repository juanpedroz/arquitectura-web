package com.arqweb.integrador_tres.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn( name = "estudiante_id" )
    private Estudiante estudiante;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn( name = "carrera_id" )
    private Carrera carrera;

    private int inscripcion;
    private int graduacion;
    private int antiguedad;


}
