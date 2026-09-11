package estudiantes.repository;

import estudiantes.dto.BusquedaEstudianteDTO;
import estudiantes.modelo.Estudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository {

    Estudiante guardar(Estudiante estudiante);

    Estudiante buscarPorId(int id);

    Optional<Estudiante> buscarPorDNI(String dni);

    Optional<Estudiante> buscarPorLU(String lu);

    List<Estudiante> buscarTodos();

    List<Estudiante> buscarPorGenero(String genero);

    List<BusquedaEstudianteDTO> buscarPorCarreraYCiudad(String carrera, String ciudad);

    long contar();
}