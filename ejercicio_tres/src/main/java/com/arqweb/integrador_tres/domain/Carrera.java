package com.arqweb.integrador_tres.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Carrera")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "duracion")
    private long duracion;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

}
