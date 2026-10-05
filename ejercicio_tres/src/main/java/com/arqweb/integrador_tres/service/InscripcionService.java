package com.arqweb.integrador_tres.service;

import com.arqweb.integrador_tres.domain.Carrera;
import com.arqweb.integrador_tres.domain.Estudiante;
import com.arqweb.integrador_tres.domain.Inscripcion;
import com.arqweb.integrador_tres.repository.CarreraRepository;
import com.arqweb.integrador_tres.repository.EstudianteRepository;
import com.arqweb.integrador_tres.repository.InscripcionRepository;
import com.arqweb.integrador_tres.service.dto.inscripcion.request.InscripcionRequestDTO;
import com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;

    //Obtenertodas, obtenerId, obtenerXCarreraId, obtenerXEstudianteId, actualizar, eliminar, agregar

    @Transactional
    public List<InscripcionResponseDTO> obtenerTodas() {
        return inscripcionRepository.obtenerTodas();
    }

    @Transactional
    public Optional<InscripcionResponseDTO> obtenerId(Long id) { //El optinal ayuda a manejar el caso en que no se encuentre la inscripcion
        return inscripcionRepository.obtenerId(id);
    }

    @Transactional
    public List<InscripcionResponseDTO> obtenerXCarreraId(Long carreraId) {
        return inscripcionRepository.obtenerXCarreraId(carreraId);
    }

    @Transactional
    public List<InscripcionResponseDTO> obtenerXEstudianteId(Long estudianteId) {
        return inscripcionRepository.obtenerXEstudianteId(estudianteId);
    }

    @Transactional
    public InscripcionResponseDTO actualizar(InscripcionRequestDTO request) {

        //Busca la entidad con sus relaciones cargadas
        Inscripcion inscripcion = inscripcionRepository
                .obtenerXEstudianteIdYCarreraId(request.getEstudianteId(), request.getCarreraId())
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada"));

        //Modifica los datos
        inscripcion.setInscripcion(request.getInscripcion());
        inscripcion.setGraduacion(request.getGraduacion());
        inscripcion.setAntiguedad(request.getAntiguedad());

        //Guarda las modificaciones(Hibernate hace el UPDATE en BD)
        Inscripcion guardada = inscripcionRepository.save(inscripcion);

        //Mapea a DTO
        return new InscripcionResponseDTO(guardada);
    }

    @Transactional
    public void eliminar(Long estudianteId, Long carreraId) {

        //Encuentra la inscripción
        Inscripcion inscripcion = inscripcionRepository
                .obtenerXEstudianteIdYCarreraId(estudianteId, carreraId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la inscripción para eliminar"));

        //La elimina de la base de datos
        inscripcionRepository.delete(inscripcion);
    }

    @Transactional
    public InscripcionResponseDTO agregar(InscripcionRequestDTO request) {

        //Busco la inscripción
        Optional<Inscripcion> inscripcionOpt = inscripcionRepository.obtenerXEstudianteIdYCarreraId(
                request.getEstudianteId(),
                request.getCarreraId()
        );

        //Verifico si ya existe la inscripción
        if (inscripcionOpt.isPresent()) { // o bien: if (!inscripcionOpt.isEmpty())
            throw new IllegalArgumentException("El estudiante ya está inscripto.");
        }

        //Obtengo las entidades Estudiante y Carrera con verificación de existencia
        Estudiante estudiante = estudianteRepository.obtenerId(request.getEstudianteId())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));

        Carrera carrera = carreraRepository.obtenerId(request.getCarreraId())
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada"));

        //Creo y guardo la nueva inscripción
        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setEstudiante(estudiante);
        nuevaInscripcion.setCarrera(carrera);
        nuevaInscripcion.setInscripcion(request.getInscripcion());
        nuevaInscripcion.setGraduacion(request.getGraduacion());
        nuevaInscripcion.setAntiguedad(request.getAntiguedad());

        Inscripcion guardada = inscripcionRepository.save(nuevaInscripcion);

        return new InscripcionResponseDTO(guardada);
    }
}
