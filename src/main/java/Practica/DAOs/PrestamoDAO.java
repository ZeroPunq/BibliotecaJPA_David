package Practica.DAOs;

import Practica.DTOs.Prestamo;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PrestamoDAO {
    private final EntityManager em;

    public PrestamoDAO(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager no puede ser nulo.");
        }
        this.em = em;
    }

    public void guardar(Prestamo prestamo) {
        em.getTransaction().begin();
        em.persist(prestamo);
        em.getTransaction().commit();
    }

    public List<Prestamo> listarTodos() {
        return em.createQuery("SELECT p FROM Prestamo p", Prestamo.class).getResultList();
    }
}

