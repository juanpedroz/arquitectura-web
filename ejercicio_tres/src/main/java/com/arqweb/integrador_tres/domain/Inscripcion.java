package com.arqweb.integrador_tres.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Entity
@Data
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

    private Date inscripcion;
    private Date graduacion;
    private int antiguedad;

    public Inscripcion() {
    }

    public String toString() {
        return "Inscripcion{" +
                "id=" + id +
                ", estudiante=" + estudiante +
                ", carrera=" + carrera +
                ", inscripcion=" + inscripcion +
                ", graduacion=" + graduacion +
                ", antiguedad=" + antiguedad +
                '}';
    }

}
