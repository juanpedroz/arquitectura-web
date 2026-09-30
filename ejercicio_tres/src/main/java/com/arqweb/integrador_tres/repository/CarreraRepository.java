package com.arqweb.integrador_tres.repository;

import com.arqweb.integrador_tres.domain.Carrera;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CarreraRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(Carrera carrera){
        em.persist(carrera);
    }

    @Transactional
    public void delete(Long id) {
        Carrera c = em.find(Carrera.class, id);
        if (c != null) {
            em.remove(c); //Tira error si la carrera tiene registros asociados en inscripcion
        }
    }

    @Transactional
    public void update(Carrera carrera){
        String jpql = "UPDATE Carrera c SET c.nombre = :nombre, c.duracion = :duracion WHERE c.id = :id";
        em.createQuery(jpql)
                .setParameter("nombre", carrera.getNombre())
                .setParameter("duracion", carrera.getDuracion())
                .setParameter("id", carrera.getId())
                .executeUpdate();
        em.clear();
    }

    public List<Carrera> getAll(){
        String jpql = "SELECT c FROM Carrera c";
        return em.createQuery(jpql, Carrera.class)
                .getResultList();//puede estar vacia
    }

    public Carrera getById(Long id){
        String jpql = "SELECT c FROM Carrera c WHERE c.id = :id";

        try {
           return em.createQuery(jpql, Carrera.class)
                    .setParameter("id", id)
                    .getSingleResult();

        }catch (NoResultException e){
            return null;
        }

    }
}
