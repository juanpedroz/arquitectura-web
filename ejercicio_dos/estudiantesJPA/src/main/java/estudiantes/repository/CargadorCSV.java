package estudiantes.repository;

import estudiantes.modelo.Carrera;
import estudiantes.modelo.Estudiante;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CargadorCSV {

    private static final String RUTA_CARRERAS = "/datos/carreras.csv";
    private static final String RUTA_ESTUDIANTES = "/datos/estudiantes.csv";
    private static final String RUTA_INSCRIPCIONES = "/datos/estudianteCarrera.csv";

    private final CarreraRepository carreraRepository = CarreraRepositoryImpl.getInstance();
    private final EstudianteRepository estudianteRepository = EstudianteRepositoryImpl.getInstance();
    private final InscripcionRepository inscripcionRepository = InscripcionRepositoryImpl.getInstance();

    public CargadorCSVResultado cargar() {
        Map<Integer, Carrera> carrerasPorIdCsv = new HashMap<>();
        Map<String, Estudiante> estudiantesPorDni = new HashMap<>();

        int[] carreras = cargarCarreras(carrerasPorIdCsv);
        int[] estudiantes = cargarEstudiantes(estudiantesPorDni);
        int[] inscripciones = cargarInscripciones(carrerasPorIdCsv, estudiantesPorDni);

        return new CargadorCSVResultado(
                carreras[0], carreras[1],
                estudiantes[0], estudiantes[1],
                inscripciones[0], inscripciones[1]);
    }

    // ====== CARGA DE CARRERAS ======

    private int[] cargarCarreras(Map<Integer, Carrera> carrerasPorIdCsv) {
        int insertadas = 0;
        int rechazadas = 0;

        try (Reader reader = abrirRecurso(RUTA_CARRERAS); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                try {
                    int idCsv = Integer.parseInt(registro.get("id_carrera").trim());
                    String nombre = registro.get("carrera").trim();
                    int duracion = Integer.parseInt(registro.get("duracion").trim());

                    Carrera guardada = carreraRepository.guardar(new Carrera(nombre, duracion));
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
        return new int[]{insertadas, rechazadas};
    }

    // ====== CARGA DE ESTUDIANTES ======

    private int[] cargarEstudiantes(Map<String, Estudiante> estudiantesPorDni) {
        int insertados = 0;
        int rechazados = 0;

        try (Reader reader = abrirRecurso(RUTA_ESTUDIANTES); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                try {
                    String dni = registro.get("DNI").trim();
                    String nombre = registro.get("nombre").trim();
                    String apellido = registro.get("apellido").trim();
                    int edad = Integer.parseInt(registro.get("edad").trim());
                    String genero = normalizarGenero(registro.get("genero").trim());
                    String ciudad = registro.get("ciudad").trim();
                    String lu = registro.get("LU").trim();

                    if (dni.isEmpty() || lu.isEmpty()) {
                        throw new IllegalArgumentException("DNI o LU vacíos en la fila " + registro.getRecordNumber());
                    }

                    Estudiante guardado = estudianteRepository.guardar(new Estudiante(dni, nombre, apellido, edad, genero, ciudad, lu));
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
        return new int[]{insertados, rechazados};
    }

    // ====== CARGA DE INSCRIPCIONES ======

    private int[] cargarInscripciones(Map<Integer, Carrera> carrerasPorIdCsv, Map<String, Estudiante> estudiantesPorDni) {
        int insertadas = 0;
        int rechazadas = 0;

        try (Reader reader = abrirRecurso(RUTA_INSCRIPCIONES); CSVParser parser = crearParser(reader)) {
            for (CSVRecord registro : parser) {
                String idCsv = registro.get("id").trim();
                try {
                    String dniEstudiante = registro.get("id_estudiante").trim();
                    int idCarreraCsv = Integer.parseInt(registro.get("id_carrera").trim());
                    int anioInscripcion = Integer.parseInt(registro.get("inscripcion").trim());
                    int anioGraduacion = Integer.parseInt(registro.get("graduacion").trim());
                    int antiguedad = Integer.parseInt(registro.get("antiguedad").trim());

                    if (anioInscripcion < 1000 || anioInscripcion > 9999) {
                        throw new IllegalArgumentException("año de inscripción mal formado: " + registro.get("inscripcion"));
                    }

                    Estudiante estudiante = estudiantesPorDni.get(dniEstudiante);

                    if (estudiante == null) {
                        throw new IllegalArgumentException("DNI " + dniEstudiante + " inexistente en el padrón de estudiantes");
                    }

                    Carrera carrera = carrerasPorIdCsv.get(idCarreraCsv);

                    if (carrera == null) {
                        throw new IllegalArgumentException("carrera " + idCarreraCsv + " inexistente");
                    }

                    if (anioGraduacion != 0 && anioGraduacion < anioInscripcion) {
                        throw new IllegalArgumentException("año de graduación " + anioGraduacion
                                + " anterior al de inscripción " + anioInscripcion);
                    }

                    if (inscripcionRepository.existeInscripcion(estudiante.getId(), carrera.getId())) {
                        throw new IllegalArgumentException("par estudiante-carrera ya cargado: "
                                + "DNI " + dniEstudiante + " - carrera " + carrera.getNombre());
                    }

                    inscripcionRepository.guardar(estudiante.getId(), carrera.getId(), anioInscripcion, anioGraduacion, antiguedad);
                    insertadas++;
                } catch (Exception e) {
                    rechazadas++;
                    System.err.println("  [rechazo] estudianteCarrera.csv - inscripción " + idCsv + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + RUTA_INSCRIPCIONES, e);
        }

        System.out.println("  estudianteCarrera.csv: " + insertadas + " insertadas, " + rechazadas + " rechazadas");
        return new int[]{insertadas, rechazadas};
    }

    // ====== UTILIDADES ======

    private String normalizarGenero(String genero) {
        if (genero.equalsIgnoreCase("Masculino")) {
            return "Male";
        }
        if (genero.equalsIgnoreCase("Femenino")) {
            return "Female";
        }
        return genero;
    }

    private Reader abrirRecurso(String ruta) {
        InputStream stream = getClass().getResourceAsStream(ruta);

        if (stream == null) {
            throw new IllegalArgumentException("No se encontró el recurso " + ruta + " en el classpath");
        }

        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private CSVParser crearParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .get()
                .parse(reader);
    }
}