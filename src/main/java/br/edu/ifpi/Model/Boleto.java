package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "boleto")
public class Boleto extends Pagamento {
    @Column(name = "codigo_boleto", nullable = false)
    private String codigoBoleto;

    @Column(name = "vencimento")
    private String vencimento;

    // Construtor padrão
    public Boleto() {
        super();
    }

    // Construtor com parâmetros
    public Boleto(String codigoBoleto) {
        super();
        this.codigoBoleto = codigoBoleto;
    }

    // Construtor completo
    public Boleto(String codigoBoleto, String vencimento) {
        super();
        this.codigoBoleto = codigoBoleto;
        this.vencimento = vencimento;
    }

    // Construtor com valor
    public Boleto(String codigoBoleto, String vencimento, Double valor) {
        super();
        this.codigoBoleto = codigoBoleto;
        this.vencimento = vencimento;
        this.setValor(valor);
    }

    public String getCodigoBoleto() { return codigoBoleto; }
    public void setCodigoBoleto(String codigoBoleto) { this.codigoBoleto = codigoBoleto; }

    public String getVencimento() { return vencimento; }
    public void setVencimento(String vencimento) { this.vencimento = vencimento; }

    /**
     * Emite o boleto conforme diagrama UML
     * @return true se o boleto foi emitido com sucesso
     */
    public boolean emitirBoleto() {
        if (this.codigoBoleto != null && !this.codigoBoleto.isEmpty()) {
            System.out.println("✅ Boleto emitido: " + this.codigoBoleto);
            if (this.vencimento != null) {
                System.out.println("📅 Vencimento: " + this.vencimento);
            }
            if (this.getValor() != null) {
                System.out.println("💵 Valor: R$ " + String.format("%.2f", this.getValor()));
            }
            return true;
        }
        System.out.println("❌ Erro ao emitir boleto: código inválido");
        return false;
    }

    @Override
    public boolean processarPagamento() {
        // Implementação do processamento de boleto
        boolean sucesso = emitirBoleto();
        if (sucesso) {
            this.marcarComoPago();
        }
        return sucesso;
    }

    /**
     * Processa o pagamento conforme diagrama UML: processarPagamento(): double, boolean
     * @param valor Valor do pagamento
     * @return ResultadoPagamento com valor e status
     */
    @Override
    public ResultadoPagamento processarPagamento(Double valor) {
        boolean sucesso = emitirBoleto();
        return new ResultadoPagamento(valor, sucesso);
    }
}
