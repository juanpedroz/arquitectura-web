package com.arqweb.integrador_tres.utils;

import com.arqweb.integrador_tres.domain.Carrera;
import com.arqweb.integrador_tres.domain.Estudiante;
import com.arqweb.integrador_tres.domain.Inscripcion;
import com.arqweb.integrador_tres.repository.CarreraRepository;
import com.arqweb.integrador_tres.repository.EstudianteRepository;
import com.arqweb.integrador_tres.repository.InscripcionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class CSVReader implements CommandLineRunner {

    private static final String RUTA_CARRERAS = "src/main/resources/csv/carreras.csv";
    private static final String RUTA_ESTUDIANTES ="src/main/resources/estudiantes.csv";
    private static final String RUTA_INSCRIPCIONES = "src/main/resources/datos/estudianteCarrera.csv";

    private final CarreraRepository carreraRepository;
    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;

    @Autowired
    public CSVReader(CarreraRepository carreraRepository,
                       EstudianteRepository estudianteRepository,
                       InscripcionRepository inscripcionRepository) {
        this.carreraRepository = carreraRepository;
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    // Se ejecuta automáticamente al arrancar la aplicación
    @Override
    public void run(String... args) {
        // Con ddl-auto=update la base no se borra al reiniciar: evito cargar datos duplicados
        if (carreraRepository.count() > 0) {
            System.out.println("[CSV] La base ya tiene datos, no se vuelve a cargar.");
            return;
        }

        System.out.println("[CSV] Iniciando carga de datos...");

        // Guardan las entidades ya persistidas, para asociarlas después en las inscripciones
        Map<Integer, Carrera> carrerasPorIdCsv = new HashMap<>();
        Map<String, Estudiante> estudiantesPorDni = new HashMap<>();

        cargarCarreras(carrerasPorIdCsv);
        cargarEstudiantes(estudiantesPorDni);
        cargarInscripciones(carrerasPorIdCsv, estudiantesPorDni);

        System.out.println("[CSV] Carga finalizada.");
    }

    // ====== CARRERAS ======

    private void cargarCarreras(Map<Integer, Carrera> carrerasPorIdCsv) {
        int insertadas = 0;
        int rechazadas = 0;

        try (Reader reader = abrirRecurso(RUTA_CARRERAS); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                try {
                    int idCsv = Integer.parseInt(registro.get("id_carrera").trim());
                    String nombre = registro.get("carrera").trim();
                    long duracion = Long.parseLong(registro.get("duracion").trim());

                    if (nombre.isEmpty()) {
                        throw new IllegalArgumentException("nombre vacío");
                    }

                    Carrera carrera = new Carrera();
                    carrera.setNombre(nombre);
                    carrera.setDuracion(duracion);

                    Carrera guardada = carreraRepository.save(carrera);
                    carrerasPorIdCsv.put(idCsv, guardada);
                    insertadas++;
                } catch (Exception e) {
                    rechazadas++;
                    System.err.println("  [rechazo] carreras.csv - fila " + registro.getRecordNumber() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + RUTA_CARRERAS, e);
        }

        System.out.println("  carreras.csv: " + insertadas + " insertadas, " + rechazadas + " rechazadas");
    }

    // ====== ESTUDIANTES ======

    private void cargarEstudiantes(Map<String, Estudiante> estudiantesPorDni) {
        int insertados = 0;
        int rechazados = 0;

        try (Reader reader = abrirRecurso(RUTA_ESTUDIANTES); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                try {
                    String dni = registro.get("DNI").trim();
                    String nombre = registro.get("nombre").trim();
                    String apellido = registro.get("apellido").trim();
                    int edad = Integer.parseInt(registro.get("edad").trim());
                    String genero = registro.get("genero").trim();
                    String ciudad = registro.get("ciudad").trim();
                    String lu = registro.get("LU").trim();

                    if (dni.isEmpty() || lu.isEmpty()) {
                        throw new IllegalArgumentException("DNI o LU vacíos");
                    }

                    Estudiante estudiante = new Estudiante();
                    estudiante.setDni(dni);
                    estudiante.setNombre(nombre);
                    estudiante.setApellido(apellido);
                    estudiante.setEdad(edad);
                    estudiante.setGenero(genero);
                    estudiante.setCiudad(ciudad);
                    estudiante.setLu(lu);

                    Estudiante guardado = estudianteRepository.save(estudiante);
                    estudiantesPorDni.put(dni, guardado);
                    insertados++;
                } catch (Exception e) {
                    rechazados++;
                    System.err.println("  [rechazo] estudiantes.csv - fila " + registro.getRecordNumber() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + RUTA_ESTUDIANTES, e);
        }

        System.out.println("  estudiantes.csv: " + insertados + " insertados, " + rechazados + " rechazados");
    }

    // ====== INSCRIPCIONES (siempre al final: dependen de carreras y estudiantes) ======

    private void cargarInscripciones(Map<Integer, Carrera> carrerasPorIdCsv,
                                     Map<String, Estudiante> estudiantesPorDni) {
        int insertadas = 0;
        int rechazadas = 0;

        try (Reader reader = abrirRecurso(RUTA_INSCRIPCIONES); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                try {
                    // En el CSV, "id_estudiante" contiene el DNI
                    String dni = registro.get("id_estudiante").trim();
                    int idCarreraCsv = Integer.parseInt(registro.get("id_carrera").trim());
                    int anioInscripcion = Integer.parseInt(registro.get("inscripcion").trim());
                    int anioGraduacion = Integer.parseInt(registro.get("graduacion").trim());
                    int antiguedad = Integer.parseInt(registro.get("antiguedad").trim());

                    Estudiante estudiante = estudiantesPorDni.get(dni);
                    if (estudiante == null) {
                        throw new IllegalArgumentException("DNI " + dni + " inexistente en estudiantes.csv");
                    }

                    Carrera carrera = carrerasPorIdCsv.get(idCarreraCsv);
                    if (carrera == null) {
                        throw new IllegalArgumentException("carrera " + idCarreraCsv + " inexistente en carreras.csv");
                    }

                    if (anioInscripcion < 1000 || anioInscripcion > 9999) {
                        throw new IllegalArgumentException("año de inscripción mal formado: " + anioInscripcion);
                    }

                    // graduacion = 0 significa que todavía no se graduó
                    if (anioGraduacion != 0 && anioGraduacion < anioInscripcion) {
                        throw new IllegalArgumentException("graduación (" + anioGraduacion
                                + ") anterior a la inscripción (" + anioInscripcion + ")");
                    }

                    if (inscripcionRepository
                            .obtenerXEstudianteIdYCarreraId(estudiante.getId(), carrera.getId())
                            .isPresent()) {
                        throw new IllegalArgumentException("inscripción ya cargada (DNI " + dni
                                + ", carrera " + carrera.getNombre() + ")");
                    }

                    Inscripcion inscripcion = new Inscripcion();
                    inscripcion.setEstudiante(estudiante);
                    inscripcion.setCarrera(carrera);
                    inscripcion.setInscripcion(anioInscripcion);
                    inscripcion.setGraduacion(anioGraduacion);
                    inscripcion.setAntiguedad(antiguedad);

                    inscripcionRepository.save(inscripcion);
                    insertadas++;
                } catch (Exception e) {
                    rechazadas++;
                    System.err.println("  [rechazo] estudianteCarrera.csv - fila " + registro.getRecordNumber() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + RUTA_INSCRIPCIONES, e);
        }

        System.out.println("  estudianteCarrera.csv: " + insertadas + " insertadas, " + rechazadas + " rechazadas");
    }

    // ====== UTILIDADES ======

    private Reader abrirRecurso(String ruta) {
        InputStream stream = getClass().getResourceAsStream(ruta);
        if (stream == null) {
            throw new IllegalArgumentException("No se encontró " + ruta + " en src/main/resources");
        }
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private CSVParser crearParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .get()
                .parse(reader);
    }
}