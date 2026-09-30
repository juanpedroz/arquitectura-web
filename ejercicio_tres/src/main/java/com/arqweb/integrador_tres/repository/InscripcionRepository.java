package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    // ACA AGREGARÍAMOS LAS CONSULTAS CUANDO LAS TENGAMOS DEFINIDAS
}
