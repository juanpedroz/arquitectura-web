package com.arqweb.integrador_tres.web.rest.carrera;

import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInformeResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInscriptosResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.request.CarreraRequestDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraResponseDTO;
import com.arqweb.integrador_tres.service.CarreraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    //GET : todos los elementos de carrera
    @GetMapping("")
    public List<CarreraResponseDTO> get(){
        return this.service.getAll();
    }

    //GET : elemento especifico
    @GetMapping("/{id}")
    public CarreraResponseDTO get(@PathVariable long id){
        return this.service.getById(id);
    }

    //INSERT
    @PostMapping("")
    public CarreraResponseDTO insert(@RequestBody @Valid CarreraRequestDTO request){
        return this.service.insert(request);
    }

    //UPDATE
    @PutMapping("/{id}")
    public CarreraResponseDTO update(@PathVariable long id, @RequestBody @Valid CarreraRequestDTO request){
        return this.service.update(id, request);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        this.service.delete(id);
    }

    //Punto F) : Listar las carreras con su cantidad de inscriptos
    @GetMapping("/inscriptos")
    public List<CarreraInscriptosResponseDTO> getCatnInscriptos(){
        return this.service.getCarrerasInscriptos();
    }

    //Punto H) : Obtener informe con detalle de inscriptos y graduados por carrera
    @GetMapping("/informe")
    public List<CarreraInformeResponseDTO> getInforme(){
        return this.service.getInformeCarreras();
    }


}
