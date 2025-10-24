package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto_fisico")
public class ProdutoFisico extends Produto {
    @Column(name = "estoque")
    private Integer estoque; // Integer para aceitar NULL do banco

    // Construtor padrão
    public ProdutoFisico() {
        super();
    }

    // Construtor com parâmetros
    public ProdutoFisico(String nome, Double preco, String descricao, Double peso, Integer estoque) {
        super(nome, preco, descricao, peso); // Chama construtor da classe base com peso
        this.estoque = estoque;
    }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    
    /**
     * Verifica se há estoque suficiente para a quantidade solicitada
     * @param quantidade Quantidade desejada
     * @return true se há estoque suficiente
     */
    public boolean temEstoqueSuficiente(Integer quantidade) {
        if (this.estoque == null || quantidade == null) {
            return false;
        }
        return this.estoque >= quantidade;
    }
    
    /**
     * Diminui o estoque ao vender o produto
     * @param quantidade Quantidade vendida
     * @return true se conseguiu diminuir o estoque
     */
    public boolean diminuirEstoque(Integer quantidade) {
        if (!temEstoqueSuficiente(quantidade)) {
            return false;
        }
        this.estoque -= quantidade;
        return true;
    }
    
    /**
     * Aumenta o estoque ao receber novos produtos
     * @param quantidade Quantidade a adicionar
     */
    public void aumentarEstoque(Integer quantidade) {
        if (quantidade != null && quantidade > 0) {
            if (this.estoque == null) {
                this.estoque = 0;
            }
            this.estoque += quantidade;
        }
    }
    
    /**
     * Verifica se o produto está fora de estoque
     * @return true se estoque zero ou nulo
     */
    public boolean isForaDeEstoque() {
        return this.estoque == null || this.estoque <= 0;
    }
    
    /**
     * Verifica se o estoque está baixo (menor que 10 unidades)
     * @return true se estoque baixo
     */
    public boolean isEstoqueBaixo() {
        return this.estoque != null && this.estoque > 0 && this.estoque < 10;
    }
    
    // Peso herdado da classe Produto
}
