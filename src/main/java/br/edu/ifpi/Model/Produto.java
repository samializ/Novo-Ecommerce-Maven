package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "produto")
public abstract class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "preco", nullable = false)
    private Double preco; // Double para aceitar NULL do banco

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "peso")
    private Double peso; // Double para aceitar NULL do banco - conforme diagrama UML

    // Construtor padrão
    public Produto() {}

    // Construtor com parâmetros
    public Produto(String nome, Double preco, String descricao) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }

    // Construtor completo com peso
    public Produto(String nome, Double preco, String descricao, Double peso) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.peso = peso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
}
