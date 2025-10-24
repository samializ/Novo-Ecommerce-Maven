package br.edu.ifpi.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import br.edu.ifpi.Model.Historico;
import br.edu.ifpi.JPAUtil;
import java.util.List;

public class HistoricoDAO {

    public void salvar(Historico historico) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(historico);
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

    public Historico buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Historico.class, id);
        } finally {
            em.close();
        }
    }

    public List<Historico> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM Historico", Historico.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Historico historico) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(historico);
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

    public void remover(Historico historico) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(historico) ? historico : em.merge(historico));
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
