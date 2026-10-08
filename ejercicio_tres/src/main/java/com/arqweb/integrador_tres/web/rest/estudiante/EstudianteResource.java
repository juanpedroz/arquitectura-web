package com.arqweb.integrador_tres.web.rest.estudiante;

import com.arqweb.integrador_tres.service.EstudianteService;
import com.arqweb.integrador_tres.service.dto.estudiante.request.EstudianteRequestDTO;
import com.arqweb.integrador_tres.service.dto.estudiante.response.EstudianteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteResource {

    private final EstudianteService estudianteService;

    // a) POST /estudiante
    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> agregar(@RequestBody EstudianteRequestDTO request) {
        EstudianteResponseDTO nuevo = estudianteService.agregar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // c) GET /estudiante
    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerTodosOrdenados() {
        return ResponseEntity.ok(estudianteService.obtenerTodosOrdenados());
    }

    // d) GET /estudiante/lu/{lu}
    @GetMapping("/lu/{lu}")
    public ResponseEntity<EstudianteResponseDTO> obtenerXLu(@PathVariable String lu) {
        return estudianteService.obtenerXLu(lu)
                .map(ResponseEntity::ok)                          // si existe -> 200
                .orElse(ResponseEntity.notFound().build());       // si no -> 404
    }

    // e) GET /estudiante/genero/{genero}
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerXGenero(@PathVariable String genero) {
        return ResponseEntity.ok(estudianteService.obtenerXGenero(genero));
    }

    // g) GET /estudiante/carrera/{carreraId}?ciudad=Tandil
    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerXCarreraYCiudad(
            @PathVariable Long carreraId,
            @RequestParam String ciudad) {
        return ResponseEntity.ok(estudianteService.obtenerXCarreraYCiudad(carreraId, ciudad));
    }


    // PUT /estudiante/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody EstudianteRequestDTO request) {
        return estudianteService.actualizar(id, request)
                .map(ResponseEntity::ok)                          // si existe -> 200
                .orElse(ResponseEntity.notFound().build());       // si no -> 404
    }

    // DELETE /estudiante/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (estudianteService.eliminar(id)) {
            return ResponseEntity.noContent().build();            // 204
        }
        return ResponseEntity.notFound().build();                 // 404
    }

}