package br.edu.ifpi.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import br.edu.ifpi.Model.ProdutoFisico;
import br.edu.ifpi.JPAUtil;
import java.util.List;

public class ProdutoFisicoDAO {

    public void salvar(ProdutoFisico produto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(produto);
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

    public ProdutoFisico buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(ProdutoFisico.class, id);
        } finally {
            em.close();
        }
    }

    public List<ProdutoFisico> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM ProdutoFisico", ProdutoFisico.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(ProdutoFisico produto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(produto);
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

    public void remover(ProdutoFisico produto) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(produto) ? produto : em.merge(produto));
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
