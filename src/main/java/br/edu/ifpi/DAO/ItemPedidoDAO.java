package br.edu.ifpi.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import br.edu.ifpi.Model.ItemPedido;
import br.edu.ifpi.JPAUtil;
import java.util.List;

public class ItemPedidoDAO {

    public void salvar(ItemPedido item) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(item);
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

    public ItemPedido buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(ItemPedido.class, id);
        } finally {
            em.close();
        }
    }

    public List<ItemPedido> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM ItemPedido", ItemPedido.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(ItemPedido item) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(item);
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

    public void remover(ItemPedido item) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(item) ? item : em.merge(item));
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
