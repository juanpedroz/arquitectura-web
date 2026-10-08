package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Estudiante;
import com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Lo usa InscripcionService (matricular)
    @Query("SELECT e FROM Estudiante e WHERE e.id = :id")
    Optional<Estudiante> obtenerId(@Param("id") Long id);

    // c) Todos los estudiantes ordenados por apellido y nombre
    @Query("SELECT new com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO(" +
            "e.id, e.dni, e.nombre, e.apellido, e.edad, e.genero, e.ciudad, e.lu) " +
            "FROM Estudiante e " +
            "ORDER BY e.apellido ASC, e.nombre ASC")
    List<EstudianteResponseDTO> obtenerTodosOrdenadosXApellido();

    // d) Estudiante por libreta universitaria
    @Query("SELECT new com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO(" +
            "e.id, e.dni, e.nombre, e.apellido, e.edad, e.genero, e.ciudad, e.lu) " +
            "FROM Estudiante e " +
            "WHERE e.lu = :lu")
    Optional<EstudianteResponseDTO> obtenerXLu(@Param("lu") String lu);

    // e) Estudiantes por género
    @Query("SELECT new com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO(" +
            "e.id, e.dni, e.nombre, e.apellido, e.edad, e.genero, e.ciudad, e.lu) " +
            "FROM Estudiante e " +
            "WHERE e.genero = :genero")
    List<EstudianteResponseDTO> obtenerXGenero(@Param("genero") String genero);


    // g)
    // Estudiantes de una carrera filtrados por ciudad (pasa por Inscripcion)
    @Query("SELECT new com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO(" +
            "e.id, e.dni, e.nombre, e.apellido, e.edad, e.genero, e.ciudad, e.lu) " +
            "FROM Inscripcion i " +
            "JOIN i.estudiante e " +
            "WHERE i.carrera.id = :carreraId AND e.ciudad = :ciudad")
    List<EstudianteResponseDTO> obtenerXCarreraYCiudad(
            @Param("carreraId") Long carreraId,
            @Param("ciudad") String ciudad);

    // Para eliminar: chequeo si el estudiante tiene inscripciones
    @Query("SELECT COUNT(i) > 0 FROM Inscripcion i WHERE i.estudiante.id = :id")
    boolean tieneInscripciones(@Param("id") Long id);
}