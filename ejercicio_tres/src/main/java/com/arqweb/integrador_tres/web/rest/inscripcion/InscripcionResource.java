package com.arqweb.integrador_tres.web.rest.inscripcion;

import com.arqweb.integrador_tres.service.InscripcionService;
import com.arqweb.integrador_tres.service.dto.inscripcion.request.InscripcionRequestDTO;
import com.arqweb.integrador_tres.service.dto.inscripcion.response.InscripcionResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<InscripcionResponseDTO> getInscripcionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(this.inscripcionService.obtenerId(id));
    }

    //Obtener una inscripcion por estudianteId
    @GetMapping("/estudiante/{id}")
    public ResponseEntity<List<InscripcionResponseDTO>> getInscripcionByEstudianteId(
            @PathVariable Long id) {

        return ResponseEntity.ok(this.inscripcionService.obtenerXEstudianteId(id));
    }

    //Obtener una inscripcion por carreraId
    @GetMapping("/carrera/{id}")
    public ResponseEntity<List<InscripcionResponseDTO>> getInscripcionByCarreraId(
            @PathVariable Long id) {

        return ResponseEntity.ok(this.inscripcionService.obtenerXCarreraId(id));
    }

    @PostMapping("")
    public ResponseEntity<InscripcionResponseDTO> insert(
            @RequestBody InscripcionRequestDTO inscripcionRequestDTO) {

        return ResponseEntity.ok(this.inscripcionService.agregar(inscripcionRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid InscripcionRequestDTO request) {

        return ResponseEntity.ok(this.inscripcionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestBody @Valid InscripcionRequestDTO inscripcionRequestDTO) {

        inscripcionService.eliminar(inscripcionRequestDTO.getEstudianteId(), inscripcionRequestDTO.getCarreraId());
    }

}
