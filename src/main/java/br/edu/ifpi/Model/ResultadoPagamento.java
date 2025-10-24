package br.edu.ifpi.Model;

/**
 * Classe auxiliar para retornar múltiplos valores do processamento de pagamento
 * Conforme diagrama UML: processarPagamento(): double, boolean
 */
public class ResultadoPagamento {
    private Double valor; // Double para aceitar NULL
    private Boolean sucesso; // Boolean para aceitar NULL

    public ResultadoPagamento(Double valor, Boolean sucesso) {
        this.valor = valor;
        this.sucesso = sucesso;
    }

    public Double getValor() {
        return valor;
    }

    public Boolean isSucesso() {
        return sucesso;
    }

    @Override
    public String toString() {
        return "ResultadoPagamento{" +
                "valor=" + valor +
                ", sucesso=" + sucesso +
                '}';
    }
}
