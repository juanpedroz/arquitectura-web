package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

}
