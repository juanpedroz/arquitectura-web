package estudiantes.repository;

public record CargadorCSVResultado(
        int carrerasInsertadas,
        int carrerasRechazadas,
        int estudiantesInsertados,
        int estudiantesRechazados,
        int inscripcionesInsertadas,
        int inscripcionesRechazadas) {

    @Override
    public String toString() {
        return "CargadorCSVResultado{" +
                "carrerasInsertadas=" + carrerasInsertadas +
                ", carrerasRechazadas=" + carrerasRechazadas +
                ", estudiantesInsertados=" + estudiantesInsertados +
                ", estudiantesRechazados=" + estudiantesRechazados +
                ", inscripcionesInsertadas=" + inscripcionesInsertadas +
                ", inscripcionesRechazadas=" + inscripcionesRechazadas +
                '}';
    }
}