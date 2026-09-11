package estudiantes.dto;

public record ReporteCarreraDTO(
        String carrera,
        int anio,
        long inscriptos,
        long egresados) {
}