package estudiantes;

import estudiantes.dto.BusquedaEstudianteDTO;
import estudiantes.dto.CarreraCantidadDTO;
import estudiantes.dto.ReporteCarreraDTO;
import estudiantes.factory.JPAUtil;
import estudiantes.modelo.Carrera;
import estudiantes.modelo.Estudiante;
import estudiantes.modelo.Inscripcion;
import estudiantes.repository.CargadorCSV;
import estudiantes.repository.CargadorCSVResultado;
import estudiantes.repository.CarreraRepository;
import estudiantes.repository.CarreraRepositoryImpl;
import estudiantes.repository.EstudianteRepository;
import estudiantes.repository.EstudianteRepositoryImpl;
import estudiantes.repository.InscripcionRepository;
import estudiantes.repository.InscripcionRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class Main {

    private static final EstudianteRepository estudianteRepository = EstudianteRepositoryImpl.getInstance();
    private static final CarreraRepository carreraRepository = CarreraRepositoryImpl.getInstance();
    private static final InscripcionRepository inscripcionRepository = InscripcionRepositoryImpl.getInstance();

    static void main() {
        banner();

        cargarDatosIniciales();
        puntoAAltaEstudiante();
        puntoBMatriculacion();
        puntoCTodosLosEstudiantes();
        puntoDporLibreta();
        puntoEporGenero();
        puntoFcarrerasConInscriptos();
        puntoGestudiantesPorCarreraYCiudad();
        reporteCarreras();

        JPAUtil.cerrar();
    }

    // ====== ENCABEZADO ======

    private static void banner() {
        System.out.println("==============================================================");
        System.out.println("  INTEGRADOR 2 - REGISTRO DE ESTUDIANTES (JPA/Hibernate)");
        System.out.println("==============================================================");
    }

    // ====== CARGA DE DATOS INICIALES ======

    private static void cargarDatosIniciales() {
        System.out.println();
        System.out.println("===== CARGA DE DATOS INICIALES =====");

        if (estudianteRepository.contar() > 0) {
            System.out.println("  La base ya tiene datos; no se recargan los CSV.");
            return;
        }

        System.out.println("  Leyendo archivos CSV desde el classpath...");
        CargadorCSV cargador = new CargadorCSV();
        CargadorCSVResultado resultado = cargador.cargar();

        System.out.println();
        System.out.println("  Resumen de la carga:");
        System.out.println("    Carreras:      " + resultado.carrerasInsertadas() + " insertadas, "
                + resultado.carrerasRechazadas() + " rechazadas");
        System.out.println("    Estudiantes:   " + resultado.estudiantesInsertados() + " insertados, "
                + resultado.estudiantesRechazados() + " rechazados");
        System.out.println("    Inscripciones: " + resultado.inscripcionesInsertadas() + " insertadas, "
                + resultado.inscripcionesRechazadas() + " rechazadas");
    }

    // ====== PUNTO a) DAR DE ALTA UN ESTUDIANTE ======

    private static void puntoAAltaEstudiante() {
        System.out.println();
        System.out.println("===== PUNTO a) DAR DE ALTA UN ESTUDIANTE =====");

        Estudiante nueva = new Estudiante("45987654", "Laura", "Gomez", 21, "Female", "Tandil", "909909");
        System.out.println("  Se da de alta: " + nueva.getNombre() + " " + nueva.getApellido()
                + " (DNI " + nueva.getDni() + ", LU " + nueva.getLu() + ")");

        try {
            estudianteRepository.guardar(nueva);
            System.out.println("  OK: estudiante persistido con id " + nueva.getId());
        } catch (RuntimeException e) {
            System.out.println("  No se pudo dar de alta: " + e.getMessage());
        }

        intentarAltaDuplicada("45987654", "888888", "mismo DNI");
        intentarAltaDuplicada("40555555", "909909", "misma libreta universitaria");
    }

    private static void intentarAltaDuplicada(String dni, String lu, String motivo) {
        Estudiante intento = new Estudiante(dni, "Otro", "Estudiante", 30, "Male", "Ayacucho", lu);

        try {
            estudianteRepository.guardar(intento);
            System.out.println("  ERROR: no debería haberse permitido el alta con " + motivo);
        } catch (RuntimeException e) {
            System.out.println("  OK (caso esperado - " + motivo + "): " + e.getMessage());
        }
    }

    // ====== PUNTO b) MATRICULAR UN ESTUDIANTE EN UNA CARRERA ======

    private static void puntoBMatriculacion() {
        System.out.println();
        System.out.println("===== PUNTO b) MATRICULAR UN ESTUDIANTE EN UNA CARRERA =====");

        Optional<Estudiante> opEstudiante = estudianteRepository.buscarPorDNI("45987654");
        Optional<Carrera> opCarrera = carreraRepository.buscarPorNombre("TUDAI");

        if (opEstudiante.isEmpty() || opCarrera.isEmpty()) {
            System.out.println("  No se encontró la estudiante o la carrera para la demo de matriculación.");
            return;
        }

        Estudiante estudiante = opEstudiante.get();
        Carrera carrera = opCarrera.get();

        try {
            Inscripcion inscripcion = inscripcionRepository.matricular(estudiante, carrera);
            System.out.println("  OK: " + inscripcion);
        } catch (RuntimeException e) {
            System.out.println("  No se pudo matricular: " + e.getMessage());
        }

        try {
            inscripcionRepository.matricular(estudiante, carrera);
            System.out.println("  ERROR: no debería haberse duplicado la inscripción en la misma carrera.");
        } catch (RuntimeException e) {
            System.out.println("  OK (caso esperado - ya inscripto): " + e.getMessage());
        }

        Optional<Carrera> opSegundaCarrera = carreraRepository.buscarPorNombre("Ciencias Economicas");

        if (opSegundaCarrera.isPresent()) {
            try {
                Inscripcion inscripcion = inscripcionRepository.matricular(estudiante, opSegundaCarrera.get());
                System.out.println("  OK (segunda carrera para el mismo estudiante): " + inscripcion);
            } catch (RuntimeException e) {
                System.out.println("  No se pudo matricular: " + e.getMessage());
            }
        }
    }

    // ====== PUNTO c) TODOS LOS ESTUDIANTES ORDENADOS ======

    private static void puntoCTodosLosEstudiantes() {
        System.out.println();
        System.out.println("===== PUNTO c) TODOS LOS ESTUDIANTES (ORDENADOS POR APELLIDO, NOMBRE) =====");

        List<Estudiante> estudiantes = estudianteRepository.buscarTodos();

        System.out.println("  Cantidad: " + estudiantes.size());
        System.out.println("  " + encabezadoEstudiante());

        for (Estudiante estudiante : estudiantes) {
            System.out.println("  " + filaEstudiante(estudiante));
        }
    }

    // ====== PUNTO d) ESTUDIANTE POR LIBRETA UNIVERSITARIA ======

    private static void puntoDporLibreta() {
        System.out.println();
        System.out.println("===== PUNTO d) ESTUDIANTE POR LIBRETA UNIVERSITARIA =====");

        buscarMostrarPorLU("61607");
        buscarMostrarPorLU("999999");
    }

    private static void buscarMostrarPorLU(String lu) {
        Optional<Estudiante> encontrado = estudianteRepository.buscarPorLU(lu);

        if (encontrado.isPresent()) {
            Estudiante estudiante = encontrado.get();
            System.out.println("  LU " + lu + " -> " + estudiante.getApellido() + ", " + estudiante.getNombre()
                    + " (DNI " + estudiante.getDni() + ", " + estudiante.getCiudad() + ")");
        } else {
            System.out.println("  LU " + lu + " -> no hay resultados");
        }
    }

    // ====== PUNTO e) ESTUDIANTES POR GÉNERO ======

    private static void puntoEporGenero() {
        System.out.println();
        System.out.println("===== PUNTO e) ESTUDIANTES POR GÉNERO (PARÁMETRO) =====");

        String[] generos = {"Female", "Male", "Non-binary"};

        for (String genero : generos) {
            mostrarPorGenero(genero);
        }
    }

    private static void mostrarPorGenero(String genero) {
        List<Estudiante> lista = estudianteRepository.buscarPorGenero(genero);

        System.out.println();
        System.out.println("  Género: " + genero + " (" + lista.size() + " estudiantes)");

        for (Estudiante estudiante : lista) {
            System.out.println("    " + estudiante.getNombre() + " " + estudiante.getApellido()
                    + " - DNI " + estudiante.getDni() + " - LU " + estudiante.getLu());
        }
    }

    // ====== PUNTO f) CARRERAS CON INSCRIPTOS ORDENADAS POR CANTIDAD ======

    private static void puntoFcarrerasConInscriptos() {
        System.out.println();
        System.out.println("===== PUNTO f) CARRERAS CON ESTUDIANTES INSCRIPTOS (POR CANTIDAD) =====");

        List<CarreraCantidadDTO> carreras = inscripcionRepository.carrerasConInscriptos();

        System.out.println("  " + String.format("%-30s | %s", "Carrera", "Inscriptos"));

        for (CarreraCantidadDTO dto : carreras) {
            System.out.println("  " + String.format("%-30s | %d", dto.nombre(), dto.cantidad()));
        }
    }

    // ====== PUNTO g) ESTUDIANTES DE UNA CARRERA FILTRADOS POR CIUDAD ======

    private static void puntoGestudiantesPorCarreraYCiudad() {
        System.out.println();
        System.out.println("===== PUNTO g) ESTUDIANTES DE UNA CARRERA FILTRADOS POR CIUDAD =====");

        List<BusquedaEstudianteDTO> lista = estudianteRepository.buscarPorCarreraYCiudad("TUDAI", "Rauch");

        System.out.println("  Carrera: TUDAI | Ciudad: Rauch | " + lista.size() + " resultado(s)");

        for (BusquedaEstudianteDTO dto : lista) {
            System.out.println("  " + dto);
        }
    }

    // ====== REPORTE DE CARRERAS ======

    private static void reporteCarreras() {
        System.out.println();
        System.out.println("===== REPORTE: INSCRIPTOS Y EGRESADOS POR CARRERA Y AÑO =====");

        List<ReporteCarreraDTO> reporte = inscripcionRepository.generarReporte();

        System.out.println("  " + String.format("%-26s | %-6s | %-10s | %s",
                "Carrera", "Año", "Inscriptos", "Egresados"));

        for (ReporteCarreraDTO fila : reporte) {
            System.out.println("  " + String.format("%-26s | %-6d | %-10d | %d",
                    fila.carrera(), fila.anio(), fila.inscriptos(), fila.egresados()));
        }
    }

    // ====== FORMATEO DE TABLAS ======

    private static String encabezadoEstudiante() {
        return String.format("%-9s | %-20s | %-22s | %-4s | %-14s | %-24s | %-7s",
                "DNI", "Nombre", "Apellido", "Edad", "Género", "Ciudad", "LU");
    }

    private static String filaEstudiante(Estudiante estudiante) {
        return String.format("%-9s | %-20s | %-22s | %-4d | %-14s | %-24s | %-7s",
                estudiante.getDni(),
                estudiante.getNombre(),
                estudiante.getApellido(),
                estudiante.getEdad(),
                estudiante.getGenero(),
                estudiante.getCiudad(),
                estudiante.getLu());
    }
}