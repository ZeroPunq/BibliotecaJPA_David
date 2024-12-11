package Practica.DAOs;

import Practica.DTOs.Libro;
import jakarta.persistence.EntityManager;

import java.util.List;

public class LibroDAO {
    private final EntityManager em;

    public LibroDAO(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager no puede ser nulo.");
        }
        this.em = em;
    }

    public void guardar(Libro libro) {
        em.getTransaction().begin();
        em.persist(libro);
        em.getTransaction().commit();
    }

    public List<Libro> listarTodos() {
        return em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
    }
}
