package estudiantes.repository;

import estudiantes.modelo.Carrera;

import java.util.List;
import java.util.Optional;

public interface CarreraRepository {

    Carrera guardar(Carrera carrera);

    Carrera buscarPorId(int id);

    Optional<Carrera> buscarPorNombre(String nombre);

    List<Carrera> buscarTodos();
}