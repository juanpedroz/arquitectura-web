package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.Vistas.CarreraCantAnualDTO;
import com.arqweb.integrador_tres.Vistas.CarreraInscriptosResponseDTO;
import com.arqweb.integrador_tres.domain.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    //recuperar las carreras con estudiantes inscriptos, y ordenar por cantidad de inscriptos.
    @Query("SELECT new com.arqweb.integrador_tres.Vistas.CarreraInscriptosResponseDTO(" +
            "c.nombre, c.duracion, COUNT(i)) " +
            "FROM Carrera c " +
            "JOIN c.inscripciones i " +
            "GROUP BY c.id, c.nombre, c.duracion " +
            "ORDER BY COUNT(i) DESC")
    List<CarreraInscriptosResponseDTO> obtenerInscriptos();

    @Query("SELECT new com.arqweb.integrador_tres.Vistas.CarreraCantAnualDTO(" +
            "c.nombre, c.duracion, i.inscripcion, COUNT(i)) " +
            "FROM Carrera c " +
            "JOIN c.inscripciones i " +
            "GROUP BY c.id, c.nombre, i.inscripcion " +
            "ORDER BY c.nombre, i.inscripcion")
    List<CarreraCantAnualDTO> obtenerInscripcionesPorAño();

    @Query("SELECT new com.arqweb.integrador_tres.Vistas.CarreraCantAnualDTO(" +
            "c.nombre, c.duracion, i.graduacion, COUNT(i)) " +
            "FROM Carrera c " +
            "JOIN c.inscripciones i " +
            "WHERE i.graduacion IS NOT NULL " +
            "GROUP BY c.id, c.nombre, c.duracion, i.graduacion " +
            "ORDER BY c.nombre, i.graduacion")
    List<CarreraCantAnualDTO> obtenerGraduadosPorAño();


}
