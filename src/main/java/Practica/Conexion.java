package Practica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Conexion {
    private static EntityManagerFactory managerFactory;
    private static EntityManager em;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (managerFactory == null || !managerFactory.isOpen()) {
            managerFactory = Persistence.createEntityManagerFactory("biblioteca");
        }
        return managerFactory;
    }

    public static EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = getEntityManagerFactory().createEntityManager();
        }
        return em;
    }

    public static void cerrar() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (managerFactory != null && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}
