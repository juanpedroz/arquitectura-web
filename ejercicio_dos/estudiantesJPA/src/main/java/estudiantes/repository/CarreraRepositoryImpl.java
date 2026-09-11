package estudiantes.repository;

import estudiantes.factory.JPAUtil;
import estudiantes.modelo.Carrera;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CarreraRepositoryImpl implements CarreraRepository {

    private static CarreraRepositoryImpl instance = null;

    private CarreraRepositoryImpl() {
    }

    public static CarreraRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new CarreraRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Carrera guardar(Carrera carrera) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(carrera);
            em.getTransaction().commit();
            return carrera;
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
    public Carrera buscarPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Carrera.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Carrera> buscarPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Carrera c WHERE c.nombre = :nombre";

            List<Carrera> resultado = em.createQuery(jpql, Carrera.class)
                    .setParameter("nombre", nombre)
                    .getResultList();

            return resultado.stream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Carrera> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Carrera c ORDER BY c.nombre ASC";

            return em.createQuery(jpql, Carrera.class).getResultList();
        } finally {
            em.close();
        }
    }
}