public abstract class ProdutoFactory {
    public abstract Produto criarProduto();

    public Produto gerarProduto(String nome, double preco) {
        Produto produto = criarProduto();
        produto.setNome(nome);
        produto.setPreco(preco);
        return produto;
    }
    
}
