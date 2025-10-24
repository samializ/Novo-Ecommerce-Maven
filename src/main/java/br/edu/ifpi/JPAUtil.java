package br.edu.ifpi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("EcommercePU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory para evitar vazamento de memória
     * Deve ser chamado no shutdown da aplicação
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
