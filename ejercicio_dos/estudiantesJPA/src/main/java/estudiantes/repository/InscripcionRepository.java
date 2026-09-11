package estudiantes.repository;

import estudiantes.dto.CarreraCantidadDTO;
import estudiantes.dto.ReporteCarreraDTO;
import estudiantes.modelo.Carrera;
import estudiantes.modelo.Estudiante;
import estudiantes.modelo.Inscripcion;

import java.util.List;

public interface InscripcionRepository {

    Inscripcion guardar(int idEstudiante, int idCarrera, int anioInscripcion, int anioGraduacion, int antiguedad);

    Inscripcion matricular(Estudiante estudiante, Carrera carrera);

    Inscripcion buscarPorId(int id);

    List<Inscripcion> buscarTodos();

    boolean existeInscripcion(int idEstudiante, int idCarrera);

    List<CarreraCantidadDTO> carrerasConInscriptos();

    List<ReporteCarreraDTO> generarReporte();
}