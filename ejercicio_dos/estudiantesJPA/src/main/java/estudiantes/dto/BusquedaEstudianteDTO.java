package estudiantes.dto;

public record BusquedaEstudianteDTO(
        String dni,
        String nombre,
        String apellido,
        int edad,
        String genero,
        String ciudad,
        String lu) {
}