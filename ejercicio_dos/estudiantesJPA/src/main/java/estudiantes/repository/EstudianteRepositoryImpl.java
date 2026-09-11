package estudiantes.repository;

import estudiantes.dto.BusquedaEstudianteDTO;
import estudiantes.factory.JPAUtil;
import estudiantes.modelo.Estudiante;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class EstudianteRepositoryImpl implements EstudianteRepository {

    private static EstudianteRepositoryImpl instance = null;

    private EstudianteRepositoryImpl() {
    }

    public static EstudianteRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new EstudianteRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (existeDNI(estudiante.getDni())) {
                throw new IllegalArgumentException("Ya existe un estudiante con el DNI " + estudiante.getDni());
            }

            if (existeLU(estudiante.getLu())) {
                throw new IllegalArgumentException("Ya existe un estudiante con la libreta universitaria " + estudiante.getLu());
            }

            em.getTransaction().begin();
            em.persist(estudiante);
            em.getTransaction().commit();
            return estudiante;
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
    public Estudiante buscarPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Estudiante.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Estudiante> buscarPorDNI(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Estudiante e WHERE e.dni = :dni";

            List<Estudiante> resultado = em.createQuery(jpql, Estudiante.class)
                    .setParameter("dni", dni)
                    .getResultList();

            return resultado.stream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Estudiante> buscarPorLU(String lu) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Estudiante e WHERE e.lu = :lu";

            List<Estudiante> resultado = em.createQuery(jpql, Estudiante.class)
                    .setParameter("lu", lu)
                    .getResultList();

            return resultado.stream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Estudiante> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Estudiante e ORDER BY e.apellido ASC, e.nombre ASC";

            return em.createQuery(jpql, Estudiante.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Estudiante> buscarPorGenero(String genero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Estudiante e WHERE e.genero = :genero";

            return em.createQuery(jpql, Estudiante.class)
                    .setParameter("genero", genero)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BusquedaEstudianteDTO> buscarPorCarreraYCiudad(String carrera, String ciudad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT new estudiantes.dto.BusquedaEstudianteDTO(" +
                    "e.dni, e.nombre, e.apellido, e.edad, e.genero, e.ciudad, e.lu) " +
                    "FROM Estudiante e " +
                    "JOIN e.inscripciones i " +
                    "JOIN i.carrera c " +
                    "WHERE c.nombre = :carrera AND e.ciudad = :ciudad";

            return em.createQuery(jpql, BusquedaEstudianteDTO.class)
                    .setParameter("carrera", carrera)
                    .setParameter("ciudad", ciudad)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long contar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Estudiante e";

            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    private boolean existeDNI(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Estudiante e WHERE e.dni = :dni";

            Long cantidad = em.createQuery(jpql, Long.class)
                    .setParameter("dni", dni)
                    .getSingleResult();

            return cantidad > 0;
        } finally {
            em.close();
        }
    }

    private boolean existeLU(String lu) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM Estudiante e WHERE e.lu = :lu";

            Long cantidad = em.createQuery(jpql, Long.class)
                    .setParameter("lu", lu)
                    .getSingleResult();

            return cantidad > 0;
        } finally {
            em.close();
        }
    }
}