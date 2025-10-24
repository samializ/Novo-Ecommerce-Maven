package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroPedido;

    @Column(name = "data")
    private String data;

    @Column(name = "status")
    private String status;

    @Column(name = "items_pedido")
    private Integer itemsPedido; // Integer para aceitar NULL do banco - Quantidade de itens no pedido (conforme diagrama)

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemPedido> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Pagamento pagamento;

    // Construtor padrão
    public Pedido() {
        this.data = java.time.LocalDateTime.now().toString();
        this.status = "PENDENTE";
    }

    // Construtor com parâmetros
    public Pedido(Cliente cliente, Pagamento pagamento) {
        this.cliente = cliente;
        this.pagamento = pagamento;
        this.data = java.time.LocalDateTime.now().toString();
        this.status = "PENDENTE";
    }

    public Long getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(Long numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getItemsPedido() { return itemsPedido; }
    public void setItemsPedido(Integer itemsPedido) { this.itemsPedido = itemsPedido; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { 
        this.itens = itens; 
        this.itemsPedido = itens != null ? itens.size() : 0; // Atualiza contador
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }

    /**
     * Adiciona um pedido (conforme diagrama UML)
     * Retorna a quantidade de itens
     * @param pedido Pedido a ser adicionado (self-reference para fluent API)
     * @return Quantidade de itens no pedido
     */
    public Integer adicionarPedido(Pedido pedido) {
        // Este método parece ser para fluent API ou clonagem
        if (pedido != null && pedido != this) {
            // Copia os itens do pedido fornecido
            this.itens.addAll(pedido.getItens());
            this.itemsPedido = this.itens.size();
        }
        return this.itemsPedido != null ? this.itemsPedido : 0;
    }

    /**
     * Calcula o valor total do pedido somando todos os itens
     * Conforme diagrama UML: getTotal(): double
     * @return Total do pedido
     */
    public Double getTotal() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            if (item.getProduto() != null && item.getProduto().getPreco() != null && item.getQuantidade() != null) {
                total += item.getProduto().getPreco() * item.getQuantidade();
            }
        }
        return total;
    }

    /**
     * Retorna o histórico de pedidos do cliente deste pedido
     * @return Lista de pedidos do cliente
     */
    public List<Pedido> getHistoricoCliente() {
        if (this.cliente != null) {
            return this.cliente.getHistoricoPedido();
        }
        return new ArrayList<>();
    }

    /**
     * Adiciona um item ao pedido
     * Atualiza o contador itemsPedido conforme diagrama
     * @param item Item a ser adicionado
     */
    public void adicionarItem(ItemPedido item) {
        if (item != null) {
            this.itens.add(item);
            item.setPedido(this);
            this.itemsPedido = this.itens.size(); // Atualiza contador
        }
    }
    
    /**
     * Remove um item do pedido
     * Atualiza o contador itemsPedido
     * @param item Item a ser removido
     */
    public void removerItem(ItemPedido item) {
        if (item != null) {
            this.itens.remove(item);
            this.itemsPedido = this.itens.size(); // Atualiza contador
        }
    }
    
    /**
     * Finaliza o pedido processando o pagamento
     * Conforme diagrama UML - relacionamento Pedido → Pagamento
     * @return true se o pagamento foi processado com sucesso
     */
    public boolean finalizarPedido() {
        if (this.pagamento == null) {
            System.out.println("❌ Erro: Nenhum pagamento associado ao pedido!");
            return false;
        }
        
        if (this.itens.isEmpty()) {
            System.out.println("❌ Erro: Pedido sem itens!");
            return false;
        }
        
        // Processa o pagamento
        boolean pagamentoOk = this.pagamento.processarPagamento();
        
        if (pagamentoOk) {
            this.status = "FINALIZADO";
            System.out.println("✅ Pedido finalizado com sucesso!");
            System.out.println("📦 Número do pedido: " + this.numeroPedido);
            System.out.println("💰 Valor total: R$ " + String.format("%.2f", this.getTotal()));
            return true;
        } else {
            this.status = "PAGAMENTO_RECUSADO";
            System.out.println("❌ Pagamento recusado!");
            return false;
        }
    }
    
    /**
     * Processa venda completa: valida pedido, processa pagamento e atualiza status
     * Método auxiliar que engloba todo o fluxo de venda
     * @return ResultadoPagamento com valor e status
     */
    public ResultadoPagamento processarVenda() {
        if (this.pagamento == null || this.itens.isEmpty()) {
            return new ResultadoPagamento(0.0, false);
        }
        
        Double valorTotal = this.getTotal();
        ResultadoPagamento resultado = this.pagamento.processarPagamento(valorTotal);
        
        if (resultado.isSucesso()) {
            this.status = "APROVADO";
        } else {
            this.status = "RECUSADO";
        }
        
        return resultado;
    }
}
