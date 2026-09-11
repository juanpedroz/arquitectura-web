package estudiantes.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inscripcion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @Column(name = "inscripcion")
    private int inscripcion;

    @Column(name = "graduacion")
    private int graduacion;

    @Column(name = "antiguedad")
    private int antiguedad;

    public boolean esGraduado() {
        return graduacion != 0;
    }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "id=" + id +
                ", dni=" + (estudiante != null ? estudiante.getDni() : null) +
                ", carrera=" + (carrera != null ? carrera.getNombre() : null) +
                ", inscripcion=" + inscripcion +
                ", graduacion=" + graduacion +
                ", antiguedad=" + antiguedad +
                ", esGraduado=" + esGraduado() +
                '}';
    }
}