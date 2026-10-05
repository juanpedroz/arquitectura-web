package com.arqweb.integrador_tres.service;

import com.arqweb.integrador_tres.domain.Estudiante;
import com.arqweb.integrador_tres.repository.EstudianteRepository;
import com.arqweb.integrador_tres.service.dto.estudiante.request.EstudianteRequestDTO;
import com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    //agregar, obtenerTodosOrdenados, obtenerXLu, obtenerXGenero, obtenerXCarreraYCiudad

    // a)
    @Transactional
    public EstudianteResponseDTO agregar(EstudianteRequestDTO request) {

        //Verifico que no exista otro estudiante con la misma LU
        if (estudianteRepository.obtenerXLu(request.getLu()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con esa LU.");
        }

        //Creo la entidad con los datos del request
        Estudiante estudiante = new Estudiante();
        estudiante.setDni(request.getDni());
        estudiante.setNombre(request.getNombre());
        estudiante.setApellido(request.getApellido());
        estudiante.setEdad(request.getEdad());
        estudiante.setGenero(request.getGenero());
        estudiante.setCiudad(request.getCiudad());
        estudiante.setLu(request.getLu());

        //Guardo (Hibernate hace el INSERT en BD)
        Estudiante guardado = estudianteRepository.save(estudiante);

        //Mapeo a DTO
        return new EstudianteResponseDTO(guardado);
    }

    // c)
    @Transactional
    public List<EstudianteResponseDTO> obtenerTodosOrdenados() {
        return estudianteRepository.obtenerTodosOrdenadosXApellido();
    }

    // d)
    @Transactional
    public Optional<EstudianteResponseDTO> obtenerXLu(String lu) {
        return estudianteRepository.obtenerXLu(lu);
    }

    // e)
    @Transactional
    public List<EstudianteResponseDTO> obtenerXGenero(String genero) {
        return estudianteRepository.obtenerXGenero(genero);
    }

    // g)
    @Transactional
    public List<EstudianteResponseDTO> obtenerXCarreraYCiudad(Long carreraId, String ciudad) {
        return estudianteRepository.obtenerXCarreraYCiudad(carreraId, ciudad);
    }
}