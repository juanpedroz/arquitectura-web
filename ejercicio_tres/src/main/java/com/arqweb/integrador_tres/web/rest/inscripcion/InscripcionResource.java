package com.arqweb.integrador_tres.web.rest.inscripcion;

import com.arqweb.integrador_tres.service.InscripcionService;
import com.arqweb.integrador_tres.service.dto.inscripcion.request.InscripcionRequestDTO;
import com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inscripcion")
public class InscripcionResource {
    private final InscripcionService inscripcionService;

    @Autowired
    public InscripcionResource(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    //Todas las inscripciones
    @GetMapping("")
    public List<InscripcionResponseDTO> getAllInscripciones() {
        return inscripcionService.obtenerTodas();
    }

    //Obtener una inscripcion por id
    //El optinal prevee que la inscripcion no exista
    @GetMapping("/{id}")
    public Optional<InscripcionResponseDTO> getInscripcionById(@PathVariable Long id) {
        return inscripcionService.obtenerId(id);
    }

    //Obtener una inscripcion por estudianteId
    @GetMapping("/estudiante/{id}")
    public List<InscripcionResponseDTO> getInscripcionByEstudianteId(@PathVariable Long id) {
        return inscripcionService.obtenerXEstudianteId(id);
    }

    //Obtener una inscripcion por carreraId
    @GetMapping("/carrera/{id}")
    public List<InscripcionResponseDTO> getInscripcionByCarreraId(@PathVariable Long id) {
        return inscripcionService.obtenerXCarreraId(id);
    }

    @PostMapping("")
    public InscripcionResponseDTO insert(@RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        return inscripcionService.agregar(inscripcionRequestDTO);
    }

    @PutMapping("")
    public InscripcionResponseDTO actualizar(@RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        return inscripcionService.actualizar(inscripcionRequestDTO);
    }

    @DeleteMapping("")
    public void delete(@RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        inscripcionService.eliminar(inscripcionRequestDTO.getEstudianteId(), inscripcionRequestDTO.getCarreraId());
    }

}
