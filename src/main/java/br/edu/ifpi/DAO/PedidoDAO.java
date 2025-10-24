package br.edu.ifpi.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import br.edu.ifpi.Model.Pedido;
import br.edu.ifpi.JPAUtil;
import java.util.List;

public class PedidoDAO {

    public void salvar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(pedido);
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

    public Pedido buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Pedido.class, id);
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM Pedido", Pedido.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(pedido);
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

    public void remover(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(pedido) ? pedido : em.merge(pedido));
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
