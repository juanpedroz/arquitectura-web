package com.arqweb.integrador_tres.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Carrera")
@AllArgsConstructor
@Getter
@Setter
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "duracion")
    private int duracion;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    protected Carrera(){}

    @Override
    public String toString(){
        return String.format(
                "%nCarrera : %s%n\tid       : %d%n\tduración : %d%n",
                nombre, id, duracion
        );
    }

}
