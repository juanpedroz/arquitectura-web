package estudiantes.repository;

import estudiantes.dto.CarreraCantidadDTO;
import estudiantes.dto.ReporteCarreraDTO;
import estudiantes.factory.JPAUtil;
import estudiantes.modelo.Carrera;
import estudiantes.modelo.Estudiante;
import estudiantes.modelo.Inscripcion;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class InscripcionRepositoryImpl implements InscripcionRepository {

    private static InscripcionRepositoryImpl instance = null;

    private InscripcionRepositoryImpl() {
    }

    public static InscripcionRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new InscripcionRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Inscripcion guardar(int idEstudiante, int idCarrera, int anioInscripcion, int anioGraduacion, int antiguedad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Estudiante estudiante = em.find(Estudiante.class, idEstudiante);

            if (estudiante == null) {
                throw new IllegalArgumentException("No existe el estudiante con id " + idEstudiante);
            }

            Carrera carrera = em.find(Carrera.class, idCarrera);

            if (carrera == null) {
                throw new IllegalArgumentException("No existe la carrera con id " + idCarrera);
            }

            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setEstudiante(estudiante);
            inscripcion.setCarrera(carrera);
            inscripcion.setInscripcion(anioInscripcion);
            inscripcion.setGraduacion(anioGraduacion);
            inscripcion.setAntiguedad(antiguedad);

            em.getTransaction().begin();
            em.persist(inscripcion);
            em.getTransaction().commit();
            return inscripcion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Inscripcion matricular(Estudiante estudiante, Carrera carrera) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Estudiante estGestionado = em.find(Estudiante.class, estudiante.getId());

            if (estGestionado == null) {
                throw new IllegalArgumentException("El estudiante con DNI " + estudiante.getDni() + " no existe");
            }

            Carrera carGestionada = em.find(Carrera.class, carrera.getId());

            if (carGestionada == null) {
                throw new IllegalArgumentException("La carrera " + carrera.getNombre() + " no existe");
            }

            if (existeInscripcion(estGestionado.getId(), carGestionada.getId())) {
                throw new IllegalArgumentException("El estudiante con DNI " + estGestionado.getDni()
                        + " ya está inscripto en la carrera " + carGestionada.getNombre());
            }

            Inscripcion nueva = new Inscripcion();
            nueva.setEstudiante(estGestionado);
            nueva.setCarrera(carGestionada);
            nueva.setInscripcion(LocalDate.now().getYear());
            nueva.setGraduacion(0);
            nueva.setAntiguedad(0);

            em.getTransaction().begin();
            em.persist(nueva);
            em.getTransaction().commit();
            return nueva;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Inscripcion buscarPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Inscripcion.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Inscripcion> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT i FROM Inscripcion i";

            return em.createQuery(jpql, Inscripcion.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existeInscripcion(int idEstudiante, int idCarrera) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(i) FROM Inscripcion i " +
                    "WHERE i.estudiante.id = :idEstudiante AND i.carrera.id = :idCarrera";

            Long cantidad = em.createQuery(jpql, Long.class)
                    .setParameter("idEstudiante", idEstudiante)
                    .setParameter("idCarrera", idCarrera)
                    .getSingleResult();

            return cantidad > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<CarreraCantidadDTO> carrerasConInscriptos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT new estudiantes.dto.CarreraCantidadDTO(c.nombre, COUNT(i)) " +
                    "FROM Carrera c " +
                    "JOIN c.inscripciones i " +
                    "GROUP BY c.nombre " +
                    "ORDER BY COUNT(i) DESC, c.nombre ASC";

            return em.createQuery(jpql, CarreraCantidadDTO.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReporteCarreraDTO> generarReporte() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpqlInscriptos = "SELECT new estudiantes.dto.ReporteCarreraDTO(c.nombre, i.inscripcion, COUNT(i), 0L) " +
                    "FROM Carrera c " +
                    "JOIN c.inscripciones i " +
                    "GROUP BY c.nombre, i.inscripcion";

            List<ReporteCarreraDTO> inscriptos = em.createQuery(jpqlInscriptos, ReporteCarreraDTO.class).getResultList();

            String jpqlEgresados = "SELECT new estudiantes.dto.ReporteCarreraDTO(c.nombre, i.graduacion, 0L, COUNT(i)) " +
                    "FROM Carrera c " +
                    "JOIN c.inscripciones i " +
                    "WHERE i.graduacion <> 0 " +
                    "GROUP BY c.nombre, i.graduacion";

            List<ReporteCarreraDTO> egresados = em.createQuery(jpqlEgresados, ReporteCarreraDTO.class).getResultList();

            return combinarReporte(inscriptos, egresados);
        } finally {
            em.close();
        }
    }

    // ====== ARMADO DEL REPORTE ======

    private List<ReporteCarreraDTO> combinarReporte(List<ReporteCarreraDTO> inscriptos, List<ReporteCarreraDTO> egresados) {
        TreeMap<String, TreeMap<Integer, ReporteCarreraDTO>> tabla = new TreeMap<>();

        for (ReporteCarreraDTO fila : inscriptos) {
            tabla.computeIfAbsent(fila.carrera(), clave -> new TreeMap<>()).put(fila.anio(), fila);
        }

        for (ReporteCarreraDTO fila : egresados) {
            TreeMap<Integer, ReporteCarreraDTO> filas = tabla.computeIfAbsent(fila.carrera(), clave -> new TreeMap<>());
            ReporteCarreraDTO filaExistente = filas.get(fila.anio());

            if (filaExistente == null) {
                filas.put(fila.anio(), fila);
            } else {
                filas.put(fila.anio(), new ReporteCarreraDTO(
                        filaExistente.carrera(),
                        filaExistente.anio(),
                        filaExistente.inscriptos(),
                        fila.egresados()));
            }
        }

        List<ReporteCarreraDTO> resultado = new ArrayList<>();
        tabla.forEach((carrera, filas) -> filas.forEach((anio, fila) -> resultado.add(fila)));
        return resultado;
    }
}