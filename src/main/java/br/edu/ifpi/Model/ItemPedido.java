package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade; // Integer para aceitar NULL do banco

    @ManyToOne(fetch = FetchType.EAGER)
    private Pedido pedido;

    // Construtor padrão
    public ItemPedido() {}

    // Construtor com parâmetros
    public ItemPedido(Produto produto, Integer quantidade, Pedido pedido) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.pedido = pedido;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    /**
     * Retorna o ID do item do pedido (conforme diagrama UML)
     * @return ID do item
     */
    public Long getItemPedidoID() {
        return this.id;
    }

    /**
     * Busca um ItemPedido por quantidade (conforme diagrama UML)
     * @param quantidade Quantidade a buscar
     * @return O próprio item se a quantidade bater
     */
    public ItemPedido getItemPedido(Integer quantidade) {
        if (this.quantidade != null && this.quantidade.equals(quantidade)) {
            return this;
        }
        return null;
    }

    /**
     * Calcula o subtotal do item (preço × quantidade)
     * Conforme diagrama UML: getDouble(): double
     * @return Subtotal do item
     */
    public Double getDouble() {
        if (this.produto != null && this.produto.getPreco() != null && this.quantidade != null) {
            return this.produto.getPreco() * this.quantidade;
        }
        return 0.0;
    }
}
