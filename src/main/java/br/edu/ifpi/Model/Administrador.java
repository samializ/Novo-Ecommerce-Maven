package br.edu.ifpi.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
public class Administrador extends Usuario {
    // Construtor padrão
    public Administrador() {
        super();
    }

    // Construtor com parâmetros
    public Administrador(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    /**
     * Adiciona um produto ao sistema (conforme diagrama UML)
     * @param produto Produto a ser adicionado
     */
    public void adicionarProduto(Produto produto) {
        if (produto != null) {
            br.edu.ifpi.DAO.ProdutoDAO dao = new br.edu.ifpi.DAO.ProdutoDAO();
            dao.salvar(produto);
            System.out.println("Produto adicionado pelo administrador: " + produto.getNome());
        }
    }

    /**
     * Remove um produto do sistema (conforme diagrama UML)
     * @param produto Produto a ser removido
     */
    public void removerProduto(Produto produto) {
        if (produto != null) {
            br.edu.ifpi.DAO.ProdutoDAO dao = new br.edu.ifpi.DAO.ProdutoDAO();
            dao.remover(produto);
            System.out.println("Produto removido pelo administrador: " + produto.getNome());
        }
    }

    /**
     * Visualiza os detalhes de um pedido (conforme diagrama UML)
     * @param pedido Pedido a ser visualizado
     */
    public void visualizarPedido(Pedido pedido) {
        if (pedido != null) {
            System.out.println("=== DETALHES DO PEDIDO ===");
            System.out.println("Número: " + pedido.getNumeroPedido());
            System.out.println("Cliente: " + (pedido.getCliente() != null ? pedido.getCliente().getNome() : "N/A"));
            System.out.println("Data: " + pedido.getData());
            System.out.println("Status: " + pedido.getStatus());
            System.out.println("Total de itens: " + pedido.getItens().size());
            System.out.println("Valor total: R$ " + String.format("%.2f", pedido.getTotal()));
            System.out.println("Pagamento: " + (pedido.getPagamento() != null ? pedido.getPagamento().getClass().getSimpleName() : "Não definido"));
        }
    }
}
