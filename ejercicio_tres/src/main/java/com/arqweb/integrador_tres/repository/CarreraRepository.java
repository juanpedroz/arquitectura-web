package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Carrera;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Carrera (nombre, duracion) VALUES (:nombre, :duracion)", nativeQuery = true)
    public int agregar(@Param("nombre") String nombre, @Param("duracion") Long duracion);

    @Modifying
    @Transactional
    @Query("DELETE FROM Carrera c WHERE c.id = :id")
    public int eliminar(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Carrera c SET c.nombre = :nombre, c.duracion = :duracion WHERE c.id = :id")
    public int actualizar(@Param("id") Long id, @Param("nombre") String nombre, @Param("duracion") Long duracion);

    @Query("SELECT new com.arqweb.integrador_tres.dto.CarreraDTO(c.id, c.nombre, c.duracion) FROM Carrera c")
    public List<CarreraDTO> obtenerTodas();

    @Query("SELECT new com.arqweb.integrador_tres.dto.CarreraDTO(c.id, c.nombre, c.duracion) FROM Carrera c WHERE c.id = :id")
    public CarreraDTO obtenerId(@Param("id") Long id);
}
