package Practica.DAOs;

import Practica.DTOs.Usuario;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UsuarioDAO {
    private final EntityManager em;

    public UsuarioDAO(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("EntityManager no puede ser nulo.");
        }
        this.em = em;
    }

    public void guardar(Usuario usuario) {
        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
    }

    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }
}

