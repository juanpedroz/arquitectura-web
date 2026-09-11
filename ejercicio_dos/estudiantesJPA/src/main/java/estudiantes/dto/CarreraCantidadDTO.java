package estudiantes.dto;

public record CarreraCantidadDTO(String nombre, long cantidad) {

    @Override
    public String toString() {
        return nombre + " - inscriptos: " + cantidad;
    }
}