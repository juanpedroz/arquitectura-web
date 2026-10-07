package com.arqweb.integrador_tres.web.rest.carrera;

import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInformeResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInscriptosResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.request.CarreraRequestDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraResponseDTO;
import com.arqweb.integrador_tres.service.CarreraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrera")
public class CarreraResource {
    private final CarreraService service;

    @Autowired
    public CarreraResource(CarreraService service){
        this.service = service;
    }

    // GET: todas las carreras
    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> get() {
        return ResponseEntity.ok(this.service.obtenerTodas());
    }

    // GET: carrera específica
    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> get(@PathVariable long id) {
        return ResponseEntity.ok(this.service.obtenerId(id));
    }

    // POST: crear carrera
    @PostMapping
    public ResponseEntity<CarreraResponseDTO> insert(
            @RequestBody @Valid CarreraRequestDTO request) {

        CarreraResponseDTO nueva = this.service.agregar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nueva);
    }

    // PUT: actualizar carrera
    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> update(
            @PathVariable long id,
            @RequestBody @Valid CarreraRequestDTO request) {

        return ResponseEntity.ok(
                this.service.actualizar(id, request)
        );
    }

    // DELETE: eliminar carrera
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {

        this.service.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // F: carreras con cantidad de inscriptos
    @GetMapping("/inscriptos")
    public ResponseEntity<List<CarreraInscriptosResponseDTO>> getCantInscriptos() {
        return ResponseEntity.ok(
                this.service.getCarrerasInscriptos()
        );
    }

    // H: informe de inscriptos y graduados por carrera
    @GetMapping("/informe")
    public ResponseEntity<List<CarreraInformeResponseDTO>> getInforme() {
        return ResponseEntity.ok(
                this.service.getInformeCarreras()
        );
    }


}
