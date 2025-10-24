package br.edu.ifpi.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Column;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pagamento")
public abstract class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "status_pagamento")
    private String statusPagamento; // "PENDENTE" ou "PAGO"

    // Construtor padrão
    public Pagamento() {
        this.statusPagamento = "PENDENTE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }

    /**
     * Marca o pagamento como pago
     */
    public void marcarComoPago() {
        this.statusPagamento = "PAGO";
    }

    /**
     * Verifica se o pagamento foi realizado
     */
    public boolean isPago() {
        return "PAGO".equals(this.statusPagamento);
    }

    /**
     * Processa o pagamento conforme diagrama UML
     * Retorna boolean para compatibilidade
     * @return true se o pagamento foi processado com sucesso
     */
    public abstract boolean processarPagamento();

    /**
     * Processa o pagamento conforme diagrama UML: processarPagamento(): double, boolean
     * Retorna valor e status do pagamento
     * @param valor Valor a ser processado
     * @return ResultadoPagamento contendo valor e status
     */
    public abstract ResultadoPagamento processarPagamento(Double valor);
}
