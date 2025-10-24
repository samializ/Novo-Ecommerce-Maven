package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {
    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf; // Formato: XXX.XXX.XXX-XX
    
    @Column(name = "endereco", nullable = false)
    private String endereco;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "historico_id")
    private Historico historico;

    // Construtor padrão
    public Cliente() {
        super();
        this.historico = new Historico();
    }

    // Construtor com parâmetros
    public Cliente(String nome, String email, String senha, String cpf, String endereco) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.endereco = endereco;
        this.historico = new Historico();
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    public Historico getHistorico() { return historico; }
    public void setHistorico(Historico historico) { this.historico = historico; }

    /**
     * Adiciona um pedido ao histórico do cliente
     * Conforme diagrama UML - adiciona ao histórico do cliente
     * @param pedido Pedido a ser adicionado
     */
    public void adicionarPedidoHistorico(Pedido pedido) {
        if (pedido != null && !this.pedidos.contains(pedido)) {
            this.pedidos.add(pedido);
            if (pedido.getCliente() != this) {
                pedido.setCliente(this);
            }
            // Adiciona também ao objeto Historico
            if (this.historico != null) {
                this.historico.adicionarPedido(pedido);
            }
        }
    }

    /**
     * Retorna o histórico de pedidos do cliente
     * Conforme diagrama UML - retorna List<Pedido>
     * @return Lista de pedidos realizados pelo cliente
     */
    public List<Pedido> getHistoricoPedido() {
        if (this.historico != null) {
            return this.historico.getPedidos();
        }
        return new ArrayList<>(this.pedidos);
    }
    
    /**
     * Valida se o CPF está no formato correto
     * @param cpf CPF a ser validado
     * @return true se CPF é válido
     */
    public static boolean validarFormatoCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        // Remove pontos e traço
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        // CPF deve ter 11 dígitos
        if (cpfLimpo.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Formata o CPF para o padrão XXX.XXX.XXX-XX
     * @param cpf CPF sem formatação
     * @return CPF formatado
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null) return null;
        
        // Remove tudo que não é número
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        if (cpfLimpo.length() != 11) {
            return cpf; // Retorna original se não tiver 11 dígitos
        }
        
        return cpfLimpo.substring(0, 3) + "." + 
               cpfLimpo.substring(3, 6) + "." + 
               cpfLimpo.substring(6, 9) + "-" + 
               cpfLimpo.substring(9, 11);
    }
}
