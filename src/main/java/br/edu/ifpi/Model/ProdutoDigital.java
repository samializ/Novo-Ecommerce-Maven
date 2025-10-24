package br.edu.ifpi.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto_digital")
public class ProdutoDigital extends Produto {
    @Column(name = "url_download")
    private String urlDownload;

    @Column(name = "tamanho_arquivo")
    private Integer tamanhoArquivo; // Integer para aceitar NULL do banco - Tamanho em MB

    // Construtor padrão
    public ProdutoDigital() {
        super();
    }

    // Construtor com parâmetros
    public ProdutoDigital(String nome, Double preco, String descricao, String urlDownload) {
        super(nome, preco, descricao);
        this.urlDownload = urlDownload;
    }

    // Construtor completo
    public ProdutoDigital(String nome, Double preco, String descricao, String urlDownload, Integer tamanhoArquivo) {
        super(nome, preco, descricao);
        this.urlDownload = urlDownload;
        this.tamanhoArquivo = tamanhoArquivo;
    }

    public String getUrlDownload() { return urlDownload; }
    public void setUrlDownload(String urlDownload) { this.urlDownload = urlDownload; }

    public Integer getTamanhoArquivo() { return tamanhoArquivo; }
    public void setTamanhoArquivo(Integer tamanhoArquivo) { this.tamanhoArquivo = tamanhoArquivo; }

    /**
     * Retorna o tamanho do arquivo em MB conforme diagrama UML
     * @return Tamanho em MB
     */
    public Integer getTamanhoArquivoMB() {
        return this.tamanhoArquivo;
    }
}
