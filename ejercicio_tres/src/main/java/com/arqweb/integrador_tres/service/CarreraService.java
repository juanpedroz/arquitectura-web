package com.arqweb.integrador_tres.service;

import com.arqweb.integrador_tres.domain.Carrera;
import com.arqweb.integrador_tres.repository.CarreraRepository;
import com.arqweb.integrador_tres.service.dto.carrera.CarreraCantAnualDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInformeResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.request.CarreraRequestDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraInscriptosResponseDTO;
import com.arqweb.integrador_tres.service.dto.carrera.response.CarreraResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CarreraService {
    private final CarreraRepository carreraRepository;

    @Autowired
    public CarreraService(CarreraRepository repo){
        this.carreraRepository = repo;
    }

    @Transactional
    public CarreraResponseDTO agregar(CarreraRequestDTO carrera){
        Carrera nueva = new Carrera();
        nueva.setNombre(carrera.getNombre());
        nueva.setDuracion(carrera.getDuracion());

        Carrera response = this.carreraRepository.save(nueva);

        return new CarreraResponseDTO(response.getNombre(), response.getDuracion());
    }

    @Transactional
    public CarreraResponseDTO actualizar(Long id, CarreraRequestDTO carrera){
        Carrera actualizar = this.carreraRepository.findById(id).orElseThrow(
                                    () -> new EntityNotFoundException("Carrera no encontrada"));

        actualizar.setNombre(carrera.getNombre());
        actualizar.setDuracion(carrera.getDuracion());

        Carrera response = this.carreraRepository.save(actualizar);

        return new CarreraResponseDTO(response.getNombre(), response.getDuracion());

    }

    @Transactional
    public void eliminar(Long id) {
        //Verifico que exista la carrera antes de intentar eliminar
        if (!this.carreraRepository.existsById(id)) {
            throw new EntityNotFoundException("Carrera no encontrada");
        }
        this.carreraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> obtenerTodas(){
        return this.carreraRepository.findAll().stream()
                .map(c -> new CarreraResponseDTO(c.getNombre(), c.getDuracion()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CarreraResponseDTO obtenerId(Long id){
        Carrera response = this.carreraRepository.findById(id).orElseThrow(
                              () -> new EntityNotFoundException("Carrera no encontrada"));

        return new CarreraResponseDTO(response.getNombre(), response.getDuracion());
    }

    @Transactional
    public List<CarreraInscriptosResponseDTO> getCarrerasInscriptos(){
        return this.carreraRepository.obtenerInscriptos();
    }

    @Transactional
    public List<CarreraInformeResponseDTO> getInformeCarreras() {
        List<CarreraCantAnualDTO> inscripciones = this.carreraRepository.obtenerInscripcionesPorAño();
        List<CarreraCantAnualDTO> graduaciones = this.carreraRepository.obtenerGraduadosPorAño();

        Map<String, CarreraInformeResponseDTO> filas = new HashMap<>();

        // 1) Cargo los inscriptos de cada carrera y año
        for (CarreraCantAnualDTO i : inscripciones) {
            String clave = i.getNombre() + "-" + i.getAnio();
            filas.put(clave, new CarreraInformeResponseDTO(
                    i.getNombre(), i.getDuracion(), i.getAnio(), i.getCant(), 0));
        }

        // 2) Cargo los egresados; si esa carrera y año no existía, creo la fila con 0 inscriptos
        for (CarreraCantAnualDTO g : graduaciones) {
            String clave = g.getNombre() + "-" + g.getAnio();
            filas.computeIfAbsent(clave, k -> new CarreraInformeResponseDTO(
                            g.getNombre(), g.getDuracion(), g.getAnio(), 0, 0))
                    .setGraduados(g.getCant());
        }

        // 3) Armo la lista final: carreras alfabéticas y años cronológicos
        List<CarreraInformeResponseDTO> response = new ArrayList<>(filas.values());
        response.sort(Comparator.comparing(CarreraInformeResponseDTO::getNombre)
                .thenComparing(CarreraInformeResponseDTO::getAnio));

        return response;
    }
}
