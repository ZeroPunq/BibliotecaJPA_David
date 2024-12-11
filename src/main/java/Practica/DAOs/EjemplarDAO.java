package Practica.DAOs;

import Practica.DTOs.Ejemplar;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EjemplarDAO {
    private final EntityManager em;

    public EjemplarDAO(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager no puede ser nulo.");
        }
        this.em = em;
    }

    public void guardar(Ejemplar ejemplar) {
        em.getTransaction().begin();
        em.persist(ejemplar);
        em.getTransaction().commit();
    }

    public List<Ejemplar> listarTodos() {
        return em.createQuery("SELECT e FROM Ejemplar e", Ejemplar.class).getResultList();
    }
}
