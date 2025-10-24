package br.edu.ifpi.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Historico conforme diagrama UML
 * Armazena o histórico de pedidos dos clientes
 */
@Entity
@Table(name = "historico")
public class Historico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Pedido> itensPedidos = new ArrayList<>();

    // Construtor padrão
    public Historico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Pedido> getItensPedidos() { return itensPedidos; }
    public void setItensPedidos(List<Pedido> itensPedidos) { this.itensPedidos = itensPedidos; }

    /**
     * Adiciona um pedido ao histórico
     * @param pedido Pedido a ser adicionado
     */
    public void adicionarPedido(Pedido pedido) {
        if (pedido != null && !this.itensPedidos.contains(pedido)) {
            this.itensPedidos.add(pedido);
        }
    }

    /**
     * Remove um pedido do histórico
     * @param pedido Pedido a ser removido
     */
    public void removerPedido(Pedido pedido) {
        if (pedido != null) {
            this.itensPedidos.remove(pedido);
        }
    }

    /**
     * Retorna todos os pedidos do histórico
     * @return Lista de pedidos
     */
    public List<Pedido> getPedidos() {
        return new ArrayList<>(this.itensPedidos);
    }

    /**
     * Retorna a quantidade de pedidos no histórico
     * @return Quantidade de pedidos
     */
    public int getQuantidadePedidos() {
        return this.itensPedidos.size();
    }
}
