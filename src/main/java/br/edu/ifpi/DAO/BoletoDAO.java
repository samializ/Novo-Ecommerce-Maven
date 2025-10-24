package br.edu.ifpi.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import br.edu.ifpi.Model.Boleto;
import br.edu.ifpi.JPAUtil;
import java.util.List;

public class BoletoDAO {

    public void salvar(Boleto boleto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(boleto);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Boleto buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Boleto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Boleto> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM Boleto", Boleto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Boleto boleto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(boleto);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void remover(Boleto boleto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(boleto) ? boleto : em.merge(boleto));
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
