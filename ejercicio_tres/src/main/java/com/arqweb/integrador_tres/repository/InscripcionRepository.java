package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Inscripcion;
import com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion,Long> {
    // ACA AGREGARÍAMOS LAS CONSULTAS CUANDO LAS TENGAMOS DEFINIDAS
    // EL JOIN FETCH TRAE TODOS LOS DATOS DE LA ENTIDAD INSCRIPCION Y DE LAS ENTIDADES RELACIONADAS
    // DEBO RETORNAR UN List<Inscripcion>
    //    @Query("SELECT i FROM Inscripcion i " +
    //            "JOIN FETCH i.estudiante e " +
    //            "JOIN FETCH i.carrera " +
    //            "WHERE e.id = :estudianteId")
    //Obtenertodas, obtenerId, actualizar, eliminar, agregar

    @Query ("SELECT new com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO(" +
            "i.id, i.estudiante.nombre, i.estudiante.apellido, i.estudiante.dni, i.carrera.nombre, i.inscripcion, i.graduacion, i.antiguedad)" +
            "FROM Inscripcion i " +
            "WHERE i.id = :id")
    InscripcionResponseDTO obtenerId(@Param("id") Long id);


    @Query("SELECT i " +
            "FROM Inscripcion i " +
            "JOIN FETCH i.estudiante " +
            "JOIN FETCH i.carrera " +
            "WHERE i.estudiante.id = :estudianteId AND i.carrera.id = :carreraId")
    Optional<Inscripcion> obtenerXEstudianteIdYCarreraId(
            @Param("estudianteId") Long estudianteId,
            @Param("carreraId") Long carreraId);

    @Query("SELECT new com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO(" +
            "i.id, i.estudiante.nombre, i.estudiante.apellido, i.estudiante.dni, i.carrera.nombre, i.inscripcion," +
            " i.graduacion, i.antiguedad ) " +
            "FROM Inscripcion i ")
    List<InscripcionResponseDTO> obtenerTodas();

    @Query("SELECT new com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO(" +
            "i.id, i.estudiante.nombre, i.estudiante.apellido, i.estudiante.dni, i.carrera.nombre, i.inscripcion," +
            " i.graduacion, i.antiguedad ) " +
            "FROM Inscripcion i " +
            "WHERE i.estudiante.id = :estudiante")
    List<InscripcionResponseDTO> obtenerXEstudianteId(
            @Param("estudiante") Long estudiante);

    @Query("SELECT new com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO(" +
            "i.id, i.estudiante.nombre, i.estudiante.apellido, i.estudiante.dni, i.carrera.nombre, i.inscripcion," +
            " i.graduacion, i.antiguedad ) " +
            "FROM Inscripcion i " +
            "WHERE i.carrera.id = :carrera")
    List<InscripcionResponseDTO> obtenerXCarreraId(
            @Param("carrera") Long carrera);
}
