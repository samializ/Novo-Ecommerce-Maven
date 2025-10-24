package br.edu.ifpi;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    
    /**
     * Gera código de boleto baseado no pedido
     * Formato: XXXXX.XXXXX XXXXX.XXXXXX XXXXX.XXXXXX X XXXXXXXXXXXXX
     */
    private static String gerarCodigoBoleto(br.edu.ifpi.Model.Pedido pedido) {
        // Gera código único baseado no número do pedido e timestamp
        long timestamp = System.currentTimeMillis();
        String parte1 = String.format("%05d", pedido.getNumeroPedido() % 100000);
        String parte2 = String.format("%05d", (timestamp / 1000) % 100000);
        String parte3 = String.format("%05d", pedido.getCliente().getId() % 100000);
        String parte4 = String.format("%06d", (int)(pedido.getTotal() * 100) % 1000000);
        String parte5 = String.format("%05d", (timestamp / 10000) % 100000);
        String parte6 = String.format("%06d", pedido.getNumeroPedido() % 1000000);
        String digitoVerificador = "1"; // Simplificado
        String valorCodificado = String.format("%014d", (long)(pedido.getTotal() * 100));
        
        return String.format("%s.%s %s.%s %s.%s %s %s",
            parte1, parte2, parte3, parte4, parte5, parte6, digitoVerificador, valorCodificado);
    }
    
    /**
     * Formata data de YYYY-MM-DD para DD/MM/YYYY
     */
    private static String formatarData(String data) {
        try {
            String[] partes = data.split("-");
            if (partes.length == 3) {
                return partes[2] + "/" + partes[1] + "/" + partes[0];
            }
        } catch (Exception e) {
            // Se falhar, retorna data original
        }
        return data;
    }
    
    /**
     * Autentica o administrador no sistema
     * Login padrão: admin / admin
     * @return true se autenticado com sucesso
     */
    private static boolean autenticarAdmin(Scanner scanner) {
        br.edu.ifpi.DAO.AdministradorDAO adminDAO = new br.edu.ifpi.DAO.AdministradorDAO();
        int tentativas = 0;
        final int MAX_TENTATIVAS = 3;
        
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║     AUTENTICAÇÃO DE ADMINISTRADOR         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("📝 Login padrão: admin / admin");
        
        while (tentativas < MAX_TENTATIVAS) {
            try {
                System.out.print("\n👤 Email do administrador: ");
                String email = scanner.nextLine().trim();
                
                System.out.print("🔒 Senha: ");
                String senha = scanner.nextLine().trim();
                
                // Busca administrador por email
                java.util.List<br.edu.ifpi.Model.Administrador> admins = adminDAO.listarTodos();
                br.edu.ifpi.Model.Administrador adminEncontrado = null;
                
                for (br.edu.ifpi.Model.Administrador admin : admins) {
                    if (admin.getEmail().equalsIgnoreCase(email)) {
                        adminEncontrado = admin;
                        break;
                    }
                }
                
                // Se não encontrou nenhum admin e é a primeira tentativa, cria o admin padrão
                if (admins.isEmpty() && tentativas == 0) {
                    System.out.println("\n⚠️  Nenhum administrador cadastrado!");
                    System.out.println("📋 Criando administrador padrão...");
                    br.edu.ifpi.Model.Administrador adminPadrao = new br.edu.ifpi.Model.Administrador(
                        "Administrador",
                        "admin",
                        "admin"
                    );
                    adminDAO.salvar(adminPadrao);
                    System.out.println("✅ Administrador padrão criado!");
                    System.out.println("   Email: admin");
                    System.out.println("   Senha: admin");
                    System.out.println("\n🔄 Por favor, faça login novamente.");
                    continue;
                }
                
                // Verifica credenciais
                if (adminEncontrado != null && adminEncontrado.getSenha().equals(senha)) {
                    System.out.println("\n✅ Login realizado com sucesso!");
                    System.out.println("👤 Bem-vindo, " + adminEncontrado.getNome() + "!");
                    return true;
                } else {
                    tentativas++;
                    if (tentativas < MAX_TENTATIVAS) {
                        System.out.println("\n❌ Email ou senha incorretos!");
                        System.out.println("⚠️  Tentativa " + tentativas + " de " + MAX_TENTATIVAS);
                    }
                }
                
            } catch (Exception e) {
                System.out.println("❌ Erro ao autenticar: " + e.getMessage());
                tentativas++;
            }
        }
        
        System.out.println("\n❌ ACESSO NEGADO! Número máximo de tentativas excedido.");
        return false;
    }
    
    public static void main(String[] args) {
        // Adiciona shutdown hook para fechar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔒 Fechando recursos do sistema...");
            JPAUtil.close();
            System.out.println("✅ Sistema encerrado com sucesso!");
        }));

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   BEM-VINDO AO SISTEMA E-COMMERCE    ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        // ========================
        // AUTENTICAÇÃO OBRIGATÓRIA
        // ========================
        if (!autenticarAdmin(scanner)) {
            System.out.println("\n⛔ Sistema encerrado por falta de autenticação.");
            scanner.close();
            return;
        }
        
        int opcao = -1;
        
        do {
            try {
                System.out.println("\n========== MENU PRINCIPAL ==========");
                System.out.println("1. Gerenciar Administradores");
                System.out.println("2. Gerenciar Clientes");
                System.out.println("3. Gerenciar Produto Digital");
                System.out.println("4. Gerenciar Produto Físico");
                System.out.println("5. Gerenciar Pedidos (com Itens)");  // ← MUDANÇA
                System.out.println("6. Gerenciar Pagamentos");            // ← MUDANÇA: numeração
                System.out.println("0. Sair");
                System.out.println("====================================");
                System.out.print("Escolha uma opção: ");
                
                if (!scanner.hasNextInt()) {
                    System.out.println("❌ ERRO: Digite apenas números!");
                    scanner.nextLine();
                    continue;
                }
                
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        menuAdministradores(scanner);
                        break;
                    case 2:
                        menuClientes(scanner);
                        break;
                    case 3:
                        menuProdutoDigital(scanner);
                        break;
                    case 4:
                        menuProdutoFisico(scanner);
                        break;
                    case 5:
                        menuPedidos(scanner);
                        break;
                    case 6:  // ← MUDANÇA: era 7
                        menuPagamentos(scanner);
                        break;
                    case 0:
                        System.out.println("\n👋 Até logo! Encerrando sistema...");
                        break;
                    default:
                        System.out.println("❌ Opção inválida! Digite um número entre 0 e 6.");  // ← MUDANÇA
                }
                
            } catch (InputMismatchException e) {
                System.out.println("❌ ERRO: Entrada inválida! Digite apenas números.");
                scanner.nextLine();
                opcao = -1;
            } catch (Exception e) {
                System.out.println("❌ ERRO inesperado: " + e.getMessage());
                scanner.nextLine();
                opcao = -1;
            }
        } while (opcao != 0);
        scanner.close();
    }

    private static void menuProdutoDigital(Scanner scanner) {
        br.edu.ifpi.DAO.ProdutoDigitalDAO dao = new br.edu.ifpi.DAO.ProdutoDigitalDAO();
        int opcao;
        do {
            System.out.println("\n-- Produto Digital --");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar");
            System.out.println("3. Editar");
            System.out.println("4. Remover");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    
                    if (nome == null || nome.trim().isEmpty()) {
                        System.out.println("❌ Nome não pode ser vazio!");
                        break;
                    }
                    
                    System.out.print("Preço: ");
                    double preco = scanner.nextDouble();
                    scanner.nextLine();
                    
                    if (preco <= 0) {
                        System.out.println("❌ Preço deve ser maior que zero!");
                        break;
                    }
                    
                    System.out.print("Descrição: ");
                    String desc = scanner.nextLine();
                    
                    System.out.print("URL Download: ");
                    String url = scanner.nextLine();
                    
                    if (url == null || url.trim().isEmpty()) {
                        System.out.println("❌ URL de download não pode ser vazia!");
                        break;
                    }
                    
                    // Validação básica de URL
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        System.out.println("⚠️ ATENÇÃO: URL deve começar com http:// ou https://");
                        System.out.print("Deseja continuar mesmo assim? (S/N): ");
                        String continuar = scanner.nextLine().trim().toUpperCase();
                        if (!continuar.equals("S") && !continuar.equals("SIM")) {
                            System.out.println("❌ Cadastro cancelado.");
                            break;
                        }
                    }
                    
                    br.edu.ifpi.Model.ProdutoDigital pd = new br.edu.ifpi.Model.ProdutoDigital(nome, preco, desc, url);
                    dao.salvar(pd);
                    System.out.println("✅ Produto digital cadastrado com sucesso!");
                    break;
                case 2:
                    System.out.println("-- Lista de Produtos Digitais --");
                    for (br.edu.ifpi.Model.ProdutoDigital p : dao.listarTodos()) {
                        System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: " + p.getPreco() + " | URL: " + p.getUrlDownload());
                    }
                    break;
                case 3:
                    System.out.print("ID do produto para editar: ");
                    Long idEdit = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.ProdutoDigital produtoEdit = dao.buscarPorId(idEdit);
                    if (produtoEdit != null) {
                        System.out.println("\n--- Editando: " + produtoEdit.getNome() + " ---");
                        
                        System.out.print("Novo nome (atual: " + produtoEdit.getNome() + "): ");
                        String novoNome = scanner.nextLine();
                        if (novoNome != null && !novoNome.trim().isEmpty()) {
                            produtoEdit.setNome(novoNome);
                        }
                        
                        System.out.print("Novo preço (atual: R$ " + produtoEdit.getPreco() + "): ");
                        double novoPreco = scanner.nextDouble();
                        scanner.nextLine();
                        if (novoPreco <= 0) {
                            System.out.println("⚠️ Preço inválido, mantendo preço atual.");
                        } else {
                            produtoEdit.setPreco(novoPreco);
                        }
                        
                        System.out.print("Nova descrição: ");
                        String novaDesc = scanner.nextLine();
                        if (novaDesc != null && !novaDesc.trim().isEmpty()) {
                            produtoEdit.setDescricao(novaDesc);
                        }
                        
                        System.out.print("Nova URL (atual: " + produtoEdit.getUrlDownload() + "): ");
                        String novaUrl = scanner.nextLine();
                        if (novaUrl != null && !novaUrl.trim().isEmpty()) {
                            if (!novaUrl.startsWith("http://") && !novaUrl.startsWith("https://")) {
                                System.out.println("⚠️ ATENÇÃO: URL deve começar com http:// ou https://");
                                System.out.print("Deseja continuar mesmo assim? (S/N): ");
                                String continuar = scanner.nextLine().trim().toUpperCase();
                                if (continuar.equals("S") || continuar.equals("SIM")) {
                                    produtoEdit.setUrlDownload(novaUrl);
                                } else {
                                    System.out.println("⚠️ URL não atualizada.");
                                }
                            } else {
                                produtoEdit.setUrlDownload(novaUrl);
                            }
                        }
                        
                        dao.atualizar(produtoEdit);
                        System.out.println("✅ Produto digital atualizado!");
                    } else {
                        System.out.println("❌ Produto não encontrado.");
                    }
                    break;
                case 4: // REMOVER PRODUTO DIGITAL
                    try {
                        System.out.print("\nID do produto para remover: ");
                        Long idRem = scanner.nextLong();
                        scanner.nextLine();
                        
                        br.edu.ifpi.Model.ProdutoDigital produtoRem = dao.buscarPorId(idRem);
                        if (produtoRem == null) {
                            System.out.println("❌ Produto não encontrado.");
                            break;
                        }
                        
                        // Verificar se produto está em algum item de pedido
                        br.edu.ifpi.DAO.ItemPedidoDAO itemDAO = new br.edu.ifpi.DAO.ItemPedidoDAO();
                        java.util.List<br.edu.ifpi.Model.ItemPedido> itensComProduto = new java.util.ArrayList<>();
                        
                        for (br.edu.ifpi.Model.ItemPedido item : itemDAO.listarTodos()) {
                            if (item.getProduto() != null && item.getProduto().getId().equals(produtoRem.getId())) {
                                itensComProduto.add(item);
                            }
                        }
                        
                        System.out.println("\n--- INFORMAÇÕES DO PRODUTO ---");
                        System.out.println("📦 Nome: " + produtoRem.getNome());
                        System.out.println("💰 Preço: R$ " + String.format("%.2f", produtoRem.getPreco()));
                        System.out.println("🔗 URL: " + produtoRem.getUrlDownload());
                        
                        if (!itensComProduto.isEmpty()) {
                            System.out.println("\n⚠️  Este produto está em " + itensComProduto.size() + " item(ns) de pedido(s).");
                            System.out.println("💡 O produto será removido, mas os itens dos pedidos continuarão com a referência.");
                        }
                        
                        System.out.print("\n⚠️ Confirma a remoção do produto? (S/N): ");
                        String confirma = scanner.nextLine().trim().toUpperCase();
                        
                        if (confirma.equals("S") || confirma.equals("SIM")) {
                            // Remover referência do produto nos itens antes de deletar o produto
                            for (br.edu.ifpi.Model.ItemPedido item : itensComProduto) {
                                item.setProduto(null);
                                itemDAO.atualizar(item);
                            }
                            
                            // Agora pode remover o produto
                            dao.remover(produtoRem);
                            System.out.println("✅ Produto digital removido com sucesso!");
                            if (!itensComProduto.isEmpty()) {
                                System.out.println("⚠️  " + itensComProduto.size() + " item(ns) de pedido tiveram a referência ao produto removida.");
                            }
                        } else {
                            System.out.println("❌ Remoção cancelada.");
                        }
                        
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao remover produto: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuProdutoFisico(Scanner scanner) {
        br.edu.ifpi.DAO.ProdutoFisicoDAO dao = new br.edu.ifpi.DAO.ProdutoFisicoDAO();
        int opcao;
        do {
            System.out.println("\n-- Produto Físico --");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar");
            System.out.println("3. Editar");
            System.out.println("4. Remover");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    
                    if (nome == null || nome.trim().isEmpty()) {
                        System.out.println("❌ Nome não pode ser vazio!");
                        break;
                    }
                    
                    System.out.print("Preço: ");
                    double preco = scanner.nextDouble();
                    scanner.nextLine();
                    
                    if (preco <= 0) {
                        System.out.println("❌ Preço deve ser maior que zero!");
                        break;
                    }
                    
                    System.out.print("Descrição: ");
                    String desc = scanner.nextLine();
                    
                    System.out.print("Peso (kg): ");
                    double peso = scanner.nextDouble();
                    
                    if (peso <= 0) {
                        System.out.println("❌ Peso deve ser maior que zero!");
                        break;
                    }
                    
                    System.out.print("Estoque inicial: ");
                    int estoque = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (estoque < 0) {
                        System.out.println("❌ Estoque não pode ser negativo!");
                        break;
                    }
                    
                    br.edu.ifpi.Model.ProdutoFisico pf = new br.edu.ifpi.Model.ProdutoFisico(nome, preco, desc, peso, estoque);
                    dao.salvar(pf);
                    System.out.println("✅ Produto físico cadastrado com sucesso!");
                    System.out.println("📦 Estoque inicial: " + estoque + " unidades");
                    break;
                case 2:
                    System.out.println("-- Lista de Produtos Físicos --");
                    for (br.edu.ifpi.Model.ProdutoFisico p : dao.listarTodos()) {
                        System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: " + p.getPreco() + " | Peso: " + p.getPeso() + " | Estoque: " + p.getEstoque());
                    }
                    break;
                case 3:
                    System.out.print("ID do produto para editar: ");
                    Long idEdit = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.ProdutoFisico produtoEdit = dao.buscarPorId(idEdit);
                    if (produtoEdit != null) {
                        System.out.println("\n--- Editando: " + produtoEdit.getNome() + " ---");
                        System.out.println("📦 Estoque atual: " + produtoEdit.getEstoque() + " unidades");
                        
                        System.out.print("Novo nome (atual: " + produtoEdit.getNome() + "): ");
                        String novoNome = scanner.nextLine();
                        if (novoNome != null && !novoNome.trim().isEmpty()) {
                            produtoEdit.setNome(novoNome);
                        }
                        
                        System.out.print("Novo preço (atual: R$ " + produtoEdit.getPreco() + "): ");
                        double novoPreco = scanner.nextDouble();
                        scanner.nextLine();
                        if (novoPreco <= 0) {
                            System.out.println("⚠️ Preço inválido, mantendo preço atual.");
                        } else {
                            produtoEdit.setPreco(novoPreco);
                        }
                        
                        System.out.print("Nova descrição: ");
                        String novaDesc = scanner.nextLine();
                        if (novaDesc != null && !novaDesc.trim().isEmpty()) {
                            produtoEdit.setDescricao(novaDesc);
                        }
                        
                        System.out.print("Novo peso (atual: " + produtoEdit.getPeso() + " kg): ");
                        double novoPeso = scanner.nextDouble();
                        if (novoPeso <= 0) {
                            System.out.println("⚠️ Peso inválido, mantendo peso atual.");
                        } else {
                            produtoEdit.setPeso(novoPeso);
                        }
                        
                        System.out.print("Ajustar estoque? (S/N): ");
                        scanner.nextLine();
                        String ajustarEstoque = scanner.nextLine().trim().toUpperCase();
                        if (ajustarEstoque.equals("S") || ajustarEstoque.equals("SIM")) {
                            System.out.print("Digite a quantidade a adicionar (+) ou remover (-): ");
                            int ajuste = scanner.nextInt();
                            scanner.nextLine();
                            
                            int estoqueAtual = produtoEdit.getEstoque();
                            int novoEstoque = estoqueAtual + ajuste;
                            
                            if (novoEstoque < 0) {
                                System.out.println("❌ Estoque não pode ficar negativo!");
                                System.out.println("   Estoque atual: " + estoqueAtual);
                                System.out.println("   Ajuste solicitado: " + ajuste);
                            } else {
                                produtoEdit.setEstoque(novoEstoque);
                                System.out.println("📦 Estoque ajustado: " + estoqueAtual + " → " + novoEstoque + " unidades");
                            }
                        }
                        
                        dao.atualizar(produtoEdit);
                        System.out.println("✅ Produto físico atualizado!");
                    } else {
                        System.out.println("❌ Produto não encontrado.");
                    }
                    break;
                case 4: // REMOVER PRODUTO FÍSICO
                    try {
                        System.out.print("\nID do produto para remover: ");
                        Long idRem = scanner.nextLong();
                        scanner.nextLine();
                        
                        br.edu.ifpi.Model.ProdutoFisico produtoRem = dao.buscarPorId(idRem);
                        if (produtoRem == null) {
                            System.out.println("❌ Produto não encontrado.");
                            break;
                        }
                        
                        // Verificar se produto está em algum item de pedido
                        br.edu.ifpi.DAO.ItemPedidoDAO itemDAO = new br.edu.ifpi.DAO.ItemPedidoDAO();
                        java.util.List<br.edu.ifpi.Model.ItemPedido> itensComProduto = new java.util.ArrayList<>();
                        
                        for (br.edu.ifpi.Model.ItemPedido item : itemDAO.listarTodos()) {
                            if (item.getProduto() != null && item.getProduto().getId().equals(produtoRem.getId())) {
                                itensComProduto.add(item);
                            }
                        }
                        
                        System.out.println("\n--- INFORMAÇÕES DO PRODUTO ---");
                        System.out.println("📦 Nome: " + produtoRem.getNome());
                        System.out.println("💰 Preço: R$ " + String.format("%.2f", produtoRem.getPreco()));
                        System.out.println("⚖️  Peso: " + produtoRem.getPeso() + " kg");
                        System.out.println("📊 Estoque: " + produtoRem.getEstoque() + " unidades");
                        
                        if (!itensComProduto.isEmpty()) {
                            System.out.println("\n⚠️  Este produto está em " + itensComProduto.size() + " item(ns) de pedido(s).");
                            System.out.println("💡 O produto será removido, mas os itens dos pedidos continuarão com a referência.");
                        }
                        
                        System.out.print("\n⚠️ Confirma a remoção do produto? (S/N): ");
                        String confirma = scanner.nextLine().trim().toUpperCase();
                        
                        if (confirma.equals("S") || confirma.equals("SIM")) {
                            // Remover referência do produto nos itens antes de deletar o produto
                            for (br.edu.ifpi.Model.ItemPedido item : itensComProduto) {
                                item.setProduto(null);
                                itemDAO.atualizar(item);
                            }
                            
                            // Agora pode remover o produto
                            dao.remover(produtoRem);
                            System.out.println("✅ Produto físico removido com sucesso!");
                            if (!itensComProduto.isEmpty()) {
                                System.out.println("⚠️  " + itensComProduto.size() + " item(ns) de pedido tiveram a referência ao produto removida.");
                            }
                        } else {
                            System.out.println("❌ Remoção cancelada.");
                        }
                        
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao remover produto: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuAdministradores(Scanner scanner) {
        br.edu.ifpi.DAO.AdministradorDAO dao = new br.edu.ifpi.DAO.AdministradorDAO();
        int opcao;
        do {
            System.out.println("\n-- Administradores --");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar");
            System.out.println("3. Editar");
            System.out.println("4. Remover");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();
                    br.edu.ifpi.Model.Administrador adm = new br.edu.ifpi.Model.Administrador(nome, email, senha);
                    dao.salvar(adm);
                    System.out.println("Administrador cadastrado!");
                    break;
                case 2:
                    System.out.println("-- Lista de Administradores --");
                    for (br.edu.ifpi.Model.Administrador a : dao.listarTodos()) {
                        System.out.println("ID: " + a.getId() + " | Nome: " + a.getNome() + " | Email: " + a.getEmail());
                    }
                    break;
                case 3:
                    System.out.print("ID do administrador para editar: ");
                    Long idEdit = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Administrador admEdit = dao.buscarPorId(idEdit);
                    if (admEdit != null) {
                        System.out.print("Novo nome: ");
                        admEdit.setNome(scanner.nextLine());
                        System.out.print("Novo email: ");
                        admEdit.setEmail(scanner.nextLine());
                        System.out.print("Nova senha: ");
                        admEdit.setSenha(scanner.nextLine());
                        dao.atualizar(admEdit);
                        System.out.println("Administrador atualizado!");
                    } else {
                        System.out.println("Administrador não encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("ID do administrador para remover: ");
                    Long idRem = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Administrador admRem = dao.buscarPorId(idRem);
                    if (admRem != null) {
                        dao.remover(admRem);
                        System.out.println("Administrador removido!");
                    } else {
                        System.out.println("Administrador não encontrado.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuClientes(Scanner scanner) {
        br.edu.ifpi.DAO.ClienteDAO dao = new br.edu.ifpi.DAO.ClienteDAO();
        int opcao = -1; // Inicialização para evitar erro de compilação
        do {
            try {
                System.out.println("\n=== GESTÃO DE CLIENTES ===");
                System.out.println("1. Cadastrar Cliente");
                System.out.println("2. Listar Todos os Clientes");
                System.out.println("3. Editar Cliente");
                System.out.println("4. Remover Cliente");
                System.out.println("5. Ver Histórico de Pedidos");
                System.out.println("0. Voltar ao Menu Principal");
                System.out.print("Escolha uma opção: ");
                
                if (!scanner.hasNextInt()) {
                    System.out.println("❌ ERRO: Digite apenas números!");
                    scanner.nextLine();
                    continue;
                }
                
                opcao = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcao) {
                    case 1: // CADASTRAR CLIENTE
                        try {
                            System.out.println("\n--- CADASTRO DE CLIENTE ---");
                            
                            // Validação do Nome
                            System.out.print("Nome completo: ");
                            String nome = scanner.nextLine().trim();
                            if (nome.isEmpty() || nome.length() < 3) {
                                System.out.println("❌ ERRO: Nome deve ter no mínimo 3 caracteres!");
                                break;
                            }
                            
                            // Validação do CPF
                            System.out.print("CPF (somente números ou formato XXX.XXX.XXX-XX): ");
                            String cpf = scanner.nextLine().trim();
                            if (!br.edu.ifpi.Model.Cliente.validarFormatoCPF(cpf)) {
                                System.out.println("❌ ERRO: CPF inválido! Deve ter 11 dígitos.");
                                break;
                            }
                            String cpfFormatado = br.edu.ifpi.Model.Cliente.formatarCPF(cpf);
                            
                            // Validação do Email
                            System.out.print("Email: ");
                            String email = scanner.nextLine().trim().toLowerCase();
                            if (!validarEmail(email)) {
                                System.out.println("❌ ERRO: Email inválido! Use o formato: exemplo@dominio.com");
                                break;
                            }
                            
                            // Validação da Senha
                            System.out.print("Senha (mín. 6 caracteres): ");
                            String senha = scanner.nextLine();
                            if (senha.length() < 6) {
                                System.out.println("❌ ERRO: Senha deve ter no mínimo 6 caracteres!");
                                break;
                            }
                            
                            // Validação do Endereço
                            System.out.print("Endereço completo: ");
                            String endereco = scanner.nextLine().trim();
                            if (endereco.isEmpty() || endereco.length() < 10) {
                                System.out.println("❌ ERRO: Endereço deve ter no mínimo 10 caracteres!");
                                break;
                            }
                            
                            br.edu.ifpi.Model.Cliente cliente = new br.edu.ifpi.Model.Cliente(nome, email, senha, cpfFormatado, endereco);
                            dao.salvar(cliente);
                            System.out.println("✅ Cliente cadastrado com sucesso!");
                            System.out.println("   ID: " + cliente.getId());
                            System.out.println("   CPF: " + cpfFormatado);
                            
                        } catch (Exception e) {
                            System.out.println("❌ ERRO ao cadastrar cliente: " + e.getMessage());
                            if (e.getMessage().contains("cpf")) {
                                System.out.println("   ⚠️ Este CPF já está cadastrado no sistema!");
                            }
                        }
                        break;
                        
                    case 2: // LISTAR CLIENTES
                        try {
                            System.out.println("\n--- LISTA DE CLIENTES ---");
                            java.util.List<br.edu.ifpi.Model.Cliente> clientes = dao.listarTodos();
                            
                            if (clientes.isEmpty()) {
                                System.out.println("⚠️ Nenhum cliente cadastrado.");
                            } else {
                                System.out.println(String.format("%-5s | %-18s | %-25s | %-30s | %-30s | %-8s", 
                                    "ID", "CPF", "Nome", "Email", "Endereço", "Pedidos"));
                                System.out.println("-".repeat(130));
                                
                                for (br.edu.ifpi.Model.Cliente c : clientes) {
                                    System.out.println(String.format("%-5d | %-18s | %-25s | %-30s | %-30s | %-8d", 
                                        c.getId(), 
                                        c.getCpf(),
                                        c.getNome().substring(0, Math.min(25, c.getNome().length())),
                                        c.getEmail().substring(0, Math.min(30, c.getEmail().length())),
                                        c.getEndereco().substring(0, Math.min(30, c.getEndereco().length())),
                                        c.getPedidos().size()));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("❌ ERRO ao listar clientes: " + e.getMessage());
                        }
                        break;
                        
                    case 3: // EDITAR CLIENTE
                        try {
                            System.out.print("\nID do cliente para editar: ");
                            if (!scanner.hasNextLong()) {
                                System.out.println("❌ ERRO: ID inválido!");
                                scanner.nextLine();
                                break;
                            }
                            
                            Long idEdit = scanner.nextLong();
                            scanner.nextLine();
                            
                            br.edu.ifpi.Model.Cliente clienteEdit = dao.buscarPorId(idEdit);
                            if (clienteEdit == null) {
                                System.out.println("❌ Cliente não encontrado!");
                                break;
                            }
                            
                            System.out.println("Dados atuais:");
                            System.out.println("   Nome: " + clienteEdit.getNome());
                            System.out.println("   CPF: " + clienteEdit.getCpf());
                            System.out.println("   Email: " + clienteEdit.getEmail());
                            System.out.println("   Endereço: " + clienteEdit.getEndereco());
                            
                            System.out.print("\nNovo nome (Enter para manter): ");
                            String novoNome = scanner.nextLine().trim();
                            if (!novoNome.isEmpty() && novoNome.length() >= 3) {
                                clienteEdit.setNome(novoNome);
                            }
                            
                            System.out.print("Novo CPF (Enter para manter): ");
                            String novoCpf = scanner.nextLine().trim();
                            if (!novoCpf.isEmpty()) {
                                if (br.edu.ifpi.Model.Cliente.validarFormatoCPF(novoCpf)) {
                                    clienteEdit.setCpf(br.edu.ifpi.Model.Cliente.formatarCPF(novoCpf));
                                } else {
                                    System.out.println("⚠️ CPF inválido, mantendo CPF atual.");
                                }
                            }
                            
                            System.out.print("Novo email (Enter para manter): ");
                            String novoEmail = scanner.nextLine().trim();
                            if (!novoEmail.isEmpty() && validarEmail(novoEmail)) {
                                clienteEdit.setEmail(novoEmail.toLowerCase());
                            }
                            
                            System.out.print("Nova senha (Enter para manter): ");
                            String novaSenha = scanner.nextLine();
                            if (!novaSenha.isEmpty() && novaSenha.length() >= 6) {
                                clienteEdit.setSenha(novaSenha);
                            }
                            
                            System.out.print("Novo endereço (Enter para manter): ");
                            String novoEndereco = scanner.nextLine().trim();
                            if (!novoEndereco.isEmpty() && novoEndereco.length() >= 10) {
                                clienteEdit.setEndereco(novoEndereco);
                            }
                            
                            dao.atualizar(clienteEdit);
                            System.out.println("✅ Cliente atualizado com sucesso!");
                            
                        } catch (Exception e) {
                            System.out.println("❌ ERRO ao editar cliente: " + e.getMessage());
                        }
                        break;
                        
                    case 4: // REMOVER CLIENTE
                        try {
                            System.out.print("\nID do cliente para remover: ");
                            if (!scanner.hasNextLong()) {
                                System.out.println("❌ ERRO: ID inválido!");
                                scanner.nextLine();
                                break;
                            }
                            
                            Long idRem = scanner.nextLong();
                            scanner.nextLine();
                            
                            br.edu.ifpi.Model.Cliente clienteRem = dao.buscarPorId(idRem);
                            if (clienteRem == null) {
                                System.out.println("❌ Cliente não encontrado!");
                                break;
                            }
                            
                            // Verificar se cliente tem pedidos associados
                            br.edu.ifpi.DAO.PedidoDAO pedidoDAO = new br.edu.ifpi.DAO.PedidoDAO();
                            java.util.List<br.edu.ifpi.Model.Pedido> pedidosDoCliente = new java.util.ArrayList<>();
                            
                            for (br.edu.ifpi.Model.Pedido pedido : pedidoDAO.listarTodos()) {
                                if (pedido.getCliente() != null && pedido.getCliente().getId().equals(clienteRem.getId())) {
                                    pedidosDoCliente.add(pedido);
                                }
                            }
                            
                            System.out.println("\n--- INFORMAÇÕES DO CLIENTE ---");
                            System.out.println("👤 Nome: " + clienteRem.getNome());
                            System.out.println("📧 Email: " + clienteRem.getEmail());
                            System.out.println("📇 CPF: " + clienteRem.getCpf());
                            
                            if (!pedidosDoCliente.isEmpty()) {
                                System.out.println("\n⚠️  ATENÇÃO! Este cliente possui " + pedidosDoCliente.size() + " pedido(s) associado(s):");
                                System.out.println("-".repeat(70));
                                for (br.edu.ifpi.Model.Pedido p : pedidosDoCliente) {
                                    System.out.println("📦 Pedido #" + p.getNumeroPedido() + 
                                        " | Status: " + p.getStatus() + 
                                        " | Valor: R$ " + String.format("%.2f", p.getTotal()) +
                                        " | Itens: " + p.getItens().size());
                                }
                                System.out.println("-".repeat(70));
                                
                                System.out.print("\n🗑️  Deseja excluir os pedidos junto com o cliente? (S/N): ");
                                String excluirPedidos = scanner.nextLine().trim().toUpperCase();
                                
                                if (excluirPedidos.equals("S") || excluirPedidos.equals("SIM")) {
                                    System.out.print("⚠️  CONFIRMAÇÃO FINAL: Isso excluirá o cliente E " + pedidosDoCliente.size() + " pedido(s). Confirma? (S/N): ");
                                    String confirmaFinal = scanner.nextLine().trim().toUpperCase();
                                    
                                    if (confirmaFinal.equals("S") || confirmaFinal.equals("SIM")) {
                                        // Remover pedidos primeiro
                                        for (br.edu.ifpi.Model.Pedido p : pedidosDoCliente) {
                                            pedidoDAO.remover(p);
                                            System.out.println("🗑️  Pedido #" + p.getNumeroPedido() + " removido");
                                        }
                                        
                                        // Buscar cliente novamente para ter estado atualizado
                                        clienteRem = dao.buscarPorId(idRem);
                                        
                                        // Remover cliente
                                        if (clienteRem != null) {
                                            dao.remover(clienteRem);
                                            System.out.println("✅ Cliente e seus pedidos removidos com sucesso!");
                                        } else {
                                            System.out.println("✅ Pedidos removidos, mas cliente já não existe mais.");
                                        }
                                    } else {
                                        System.out.println("❌ Remoção cancelada.");
                                    }
                                } else {
                                    System.out.println("❌ Não é possível remover o cliente sem remover os pedidos associados.");
                                    System.out.println("💡 Dica: Remova os pedidos manualmente primeiro ou confirme a exclusão conjunta.");
                                }
                            } else {
                                // Cliente sem pedidos, remoção normal
                                System.out.print("\n⚠️ Confirma a remoção do cliente? (S/N): ");
                                String confirma = scanner.nextLine().trim().toUpperCase();
                                
                                if (confirma.equals("S") || confirma.equals("SIM")) {
                                    dao.remover(clienteRem);
                                    System.out.println("✅ Cliente removido com sucesso!");
                                } else {
                                    System.out.println("❌ Remoção cancelada.");
                                }
                            }
                            
                        } catch (Exception e) {
                            System.out.println("❌ ERRO ao remover cliente: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                        
                    case 5: // VER HISTÓRICO DE PEDIDOS
                        try {
                            System.out.print("\nID do cliente: ");
                            if (!scanner.hasNextLong()) {
                                System.out.println("❌ ERRO: ID inválido!");
                                scanner.nextLine();
                                break;
                            }
                            
                            Long idCliente = scanner.nextLong();
                            scanner.nextLine();
                            
                            br.edu.ifpi.Model.Cliente cliente = dao.buscarPorId(idCliente);
                            if (cliente == null) {
                                System.out.println("❌ Cliente não encontrado!");
                                break;
                            }
                            
                            System.out.println("\n--- HISTÓRICO DE PEDIDOS ---");
                            System.out.println("Cliente: " + cliente.getNome() + " | " + cliente.getEmail());
                            
                            java.util.List<br.edu.ifpi.Model.Pedido> historico = cliente.getHistoricoPedido();
                            if (historico.isEmpty()) {
                                System.out.println("⚠️ Cliente ainda não realizou pedidos.");
                            } else {
                                System.out.println("Total de pedidos: " + historico.size());
                                System.out.println(String.format("%-10s | %-15s | %-10s", 
                                    "Nº Pedido", "Qtd. Itens", "Pagamento"));
                                System.out.println("-".repeat(50));
                                
                                for (br.edu.ifpi.Model.Pedido p : historico) {
                                    String pagamento = p.getPagamento() != null ? 
                                        p.getPagamento().getClass().getSimpleName() : "Sem pagamento";
                                    System.out.println(String.format("%-10d | %-15d | %-10s", 
                                        p.getNumeroPedido(), 
                                        p.getItens().size(),
                                        pagamento));
                                }
                            }
                            
                        } catch (Exception e) {
                            System.out.println("❌ ERRO ao buscar histórico: " + e.getMessage());
                        }
                        break;
                        
                    case 0:
                        System.out.println("Voltando ao menu principal...");
                        break;
                        
                    default:
                        System.out.println("❌ Opção inválida! Digite um número entre 0 e 5.");
                }
                
            } catch (Exception e) {
                System.out.println("❌ ERRO inesperado: " + e.getMessage());
                scanner.nextLine(); // Limpa o buffer
                opcao = -1; // Continua no loop
            }
            
        } while (opcao != 0);
    }
    
    /**
     * Valida o formato de email
     * @param email Email a ser validado
     * @return true se o email for válido
     */
    private static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Validação básica de email
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private static void menuPedidos(Scanner scanner) {
        br.edu.ifpi.DAO.PedidoDAO dao = new br.edu.ifpi.DAO.PedidoDAO();
        int opcao;
        do {
            System.out.println("\n=== GESTÃO DE PEDIDOS ===");
            System.out.println("1. Cadastrar Novo Pedido");
            System.out.println("2. Listar Todos os Pedidos");
            System.out.println("3. Ver Detalhes do Pedido");
            System.out.println("4. Gerenciar Itens do Pedido");  // ← MUDANÇA: Integrado aqui
            System.out.println("5. Adicionar Pagamento ao Pedido");
            System.out.println("6. Finalizar Pedido (Processar Venda)");
            System.out.println("7. Editar Cliente do Pedido");
            System.out.println("8. Remover Pedido");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1: // CADASTRAR PEDIDO
                    System.out.print("ID do cliente: ");
                    Long idCliente = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Cliente cliente = new br.edu.ifpi.DAO.ClienteDAO().buscarPorId(idCliente);
                    if (cliente == null) {
                        System.out.println("❌ Cliente não encontrado.");
                        break;
                    }
                    br.edu.ifpi.Model.Pedido pedido = new br.edu.ifpi.Model.Pedido();
                    pedido.setCliente(cliente);
                    dao.salvar(pedido);
                    System.out.println("✅ Pedido cadastrado! Número: " + pedido.getNumeroPedido());
                    System.out.println("📝 Agora adicione itens ao pedido (Opção 4)");
                    break;
                    
                case 2: // LISTAR PEDIDOS
                    System.out.println("\n--- LISTA DE PEDIDOS ---");
                    System.out.println(String.format("%-10s | %-25s | %-15s | %-10s | %-10s", 
                        "Nº Pedido", "Cliente", "Data", "Status", "Itens"));
                    System.out.println("-".repeat(80));
                    for (br.edu.ifpi.Model.Pedido p : dao.listarTodos()) {
                        String nomeCliente = p.getCliente() != null ? p.getCliente().getNome() : "Sem cliente";
                        String data = p.getData() != null ? p.getData().substring(0, Math.min(10, p.getData().length())) : "-";
                        System.out.println(String.format("%-10d | %-25s | %-15s | %-10s | %-10d",
                            p.getNumeroPedido(),
                            nomeCliente.substring(0, Math.min(25, nomeCliente.length())),
                            data,
                            p.getStatus(),
                            p.getItens().size()));
                    }
                    break;
                    
                case 3: // VER DETALHES DO PEDIDO
                    System.out.print("\nNúmero do pedido: ");
                    Long idDetalhes = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoDetalhes = dao.buscarPorId(idDetalhes);
                    if (pedidoDetalhes != null) {
                        System.out.println("\n=== DETALHES DO PEDIDO #" + pedidoDetalhes.getNumeroPedido() + " ===");
                        System.out.println("👤 Cliente: " + (pedidoDetalhes.getCliente() != null ? pedidoDetalhes.getCliente().getNome() : "N/A"));
                        System.out.println("📅 Data: " + pedidoDetalhes.getData());
                        System.out.println("📊 Status: " + pedidoDetalhes.getStatus());
                        System.out.println("📦 Itens no pedido: " + pedidoDetalhes.getItens().size());
                        System.out.println("\n--- ITENS ---");
                        if (pedidoDetalhes.getItens().isEmpty()) {
                            System.out.println("⚠️ Nenhum item adicionado ainda.");
                        } else {
                            for (br.edu.ifpi.Model.ItemPedido item : pedidoDetalhes.getItens()) {
                                System.out.println("  • " + item.getProduto().getNome() + 
                                    " | Qtd: " + item.getQuantidade() + 
                                    " | Preço: R$ " + item.getProduto().getPreco() +
                                    " | Subtotal: R$ " + String.format("%.2f", item.getDouble()));
                            }
                        }
                        System.out.println("\n💰 VALOR TOTAL: R$ " + String.format("%.2f", pedidoDetalhes.getTotal()));
                        System.out.println("💳 Pagamento: " + (pedidoDetalhes.getPagamento() != null ? 
                            pedidoDetalhes.getPagamento().getClass().getSimpleName() : "Não definido"));
                    } else {
                        System.out.println("❌ Pedido não encontrado.");
                    }
                    break;
                    
                case 4: // GERENCIAR ITENS DO PEDIDO (INTEGRADO)
                    System.out.print("\nNúmero do pedido: ");
                    Long idPedidoItens = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoItens = dao.buscarPorId(idPedidoItens);
                    if (pedidoItens == null) {
                        System.out.println("❌ Pedido não encontrado.");
                        break;
                    }
                    gerenciarItensDoPedido(scanner, pedidoItens, dao);
                    break;
                    
                case 5: // ADICIONAR PAGAMENTO
                    System.out.print("\nNúmero do pedido: ");
                    Long idPagamento = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoPagamento = dao.buscarPorId(idPagamento);
                    if (pedidoPagamento == null) {
                        System.out.println("❌ Pedido não encontrado.");
                        break;
                    }
                    System.out.println("Escolha o tipo de pagamento:");
                    System.out.println("1. Boleto");
                    System.out.print("Opção: ");
                    int tipoPagamento = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (tipoPagamento == 1) {
                        System.out.print("Código do boleto: ");
                        String codigoBoleto = scanner.nextLine();
                        br.edu.ifpi.Model.Boleto boleto = new br.edu.ifpi.Model.Boleto(codigoBoleto);
                        pedidoPagamento.setPagamento(boleto);
                        dao.atualizar(pedidoPagamento);
                        System.out.println("✅ Pagamento (Boleto) adicionado ao pedido!");
                    } else {
                        System.out.println("❌ Opção inválida!");
                    }
                    break;
                    
                case 6: // FINALIZAR PEDIDO / PROCESSAR VENDA
                    System.out.print("\nNúmero do pedido para finalizar: ");
                    Long idFinalizar = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoFinalizar = dao.buscarPorId(idFinalizar);
                    if (pedidoFinalizar == null) {
                        System.out.println("❌ Pedido não encontrado.");
                        break;
                    }
                    
                    System.out.println("\n=== FINALIZANDO PEDIDO #" + pedidoFinalizar.getNumeroPedido() + " ===");
                    System.out.println("Cliente: " + pedidoFinalizar.getCliente().getNome());
                    System.out.println("Total de itens: " + pedidoFinalizar.getItens().size());
                    System.out.println("Valor total: R$ " + String.format("%.2f", pedidoFinalizar.getTotal()));
                    System.out.print("\n⚠️ Confirma a finalização? (S/N): ");
                    String confirma = scanner.nextLine().trim().toUpperCase();
                    
                    if (confirma.equals("S") || confirma.equals("SIM")) {
                        boolean sucesso = pedidoFinalizar.finalizarPedido();
                        if (sucesso) {
                            dao.atualizar(pedidoFinalizar);
                            // Adiciona ao histórico do cliente
                            if (pedidoFinalizar.getCliente() != null) {
                                pedidoFinalizar.getCliente().adicionarPedidoHistorico(pedidoFinalizar);
                            }
                        }
                    } else {
                        System.out.println("❌ Finalização cancelada.");
                    }
                    break;
                    
                case 7: // EDITAR PEDIDO
                    System.out.print("Número do pedido para editar: ");
                    Long idEdit = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoEdit = dao.buscarPorId(idEdit);
                    if (pedidoEdit != null) {
                        System.out.print("Novo ID do cliente: ");
                        Long novoIdCliente = scanner.nextLong();
                        scanner.nextLine();
                        br.edu.ifpi.Model.Cliente novoCliente = new br.edu.ifpi.DAO.ClienteDAO().buscarPorId(novoIdCliente);
                        if (novoCliente != null) {
                            pedidoEdit.setCliente(novoCliente);
                            dao.atualizar(pedidoEdit);
                            System.out.println("✅ Pedido atualizado!");
                        } else {
                            System.out.println("❌ Cliente não encontrado.");
                        }
                    } else {
                        System.out.println("❌ Pedido não encontrado.");
                    }
                    break;
                    
                case 8: // REMOVER PEDIDO
                    System.out.print("Número do pedido para remover: ");
                    Long idRem = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Pedido pedidoRem = dao.buscarPorId(idRem);
                    if (pedidoRem != null) {
                        System.out.print("⚠️ Confirma a remoção? (S/N): ");
                        String confirmaRem = scanner.nextLine().trim().toUpperCase();
                        if (confirmaRem.equals("S") || confirmaRem.equals("SIM")) {
                            dao.remover(pedidoRem);
                            System.out.println("✅ Pedido removido!");
                        } else {
                            System.out.println("❌ Remoção cancelada.");
                        }
                    } else {
                        System.out.println("❌ Pedido não encontrado.");
                    }
                    break;
                    
                case 0:
                    break;
                    
                default:
                    System.out.println("❌ Opção inválida!");
            }
        } while (opcao != 0);
    }
    
    /**
     * Submenu para gerenciar itens de um pedido específico
     * Conforme diagrama UML: ItemPedido está associado ao Pedido
     */
    private static void gerenciarItensDoPedido(Scanner scanner, br.edu.ifpi.Model.Pedido pedido, br.edu.ifpi.DAO.PedidoDAO pedidoDAO) {
        br.edu.ifpi.DAO.ItemPedidoDAO itemDAO = new br.edu.ifpi.DAO.ItemPedidoDAO();
        int opcao;
        do {
            System.out.println("\n=== ITENS DO PEDIDO #" + pedido.getNumeroPedido() + " ===");
            System.out.println("Cliente: " + pedido.getCliente().getNome());
            System.out.println("Total de itens: " + pedido.getItens().size());
            System.out.println("Valor total: R$ " + String.format("%.2f", pedido.getTotal()));
            System.out.println("\n1. Adicionar Item");
            System.out.println("2. Listar Itens");
            System.out.println("3. Editar Quantidade do Item");
            System.out.println("4. Remover Item");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            
            switch (opcao) {
                case 1: // ADICIONAR ITEM
                    System.out.print("\nID do produto: ");
                    Long idProduto = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.Produto produto = new br.edu.ifpi.DAO.ProdutoDAO().buscarPorId(idProduto);
                    if (produto == null) {
                        System.out.println("❌ Produto não encontrado.");
                        break;
                    }
                    
                    // Mostrar informações do produto
                    System.out.println("Produto: " + produto.getNome() + " - R$ " + produto.getPreco());
                    
                    // Verificar se é produto físico e mostrar estoque
                    if (produto instanceof br.edu.ifpi.Model.ProdutoFisico) {
                        br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) produto;
                        System.out.println("📦 Estoque disponível: " + prodFisico.getEstoque() + " unidades");
                        
                        if (prodFisico.isForaDeEstoque()) {
                            System.out.println("❌ Produto FORA DE ESTOQUE!");
                            break;
                        }
                        if (prodFisico.isEstoqueBaixo()) {
                            System.out.println("⚠️ ATENÇÃO: Estoque baixo!");
                        }
                    }
                    
                    System.out.print("Quantidade: ");
                    int quantidade = scanner.nextInt();
                    scanner.nextLine();
                    
                    // Validações de quantidade
                    if (quantidade <= 0) {
                        System.out.println("❌ Quantidade deve ser maior que zero!");
                        break;
                    }
                    
                    // Validar estoque para produtos físicos
                    if (produto instanceof br.edu.ifpi.Model.ProdutoFisico) {
                        br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) produto;
                        if (!prodFisico.temEstoqueSuficiente(quantidade)) {
                            System.out.println("❌ ESTOQUE INSUFICIENTE!");
                            System.out.println("   Disponível: " + prodFisico.getEstoque() + " unidades");
                            System.out.println("   Solicitado: " + quantidade + " unidades");
                            break;
                        }
                    }
                    
                    // Criar e adicionar item ao pedido
                    br.edu.ifpi.Model.ItemPedido item = new br.edu.ifpi.Model.ItemPedido(produto, quantidade, pedido);
                    pedido.adicionarItem(item); // Usa método do diagrama
                    
                    // Diminuir estoque para produtos físicos
                    if (produto instanceof br.edu.ifpi.Model.ProdutoFisico) {
                        br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) produto;
                        if (prodFisico.diminuirEstoque(quantidade)) {
                            new br.edu.ifpi.DAO.ProdutoFisicoDAO().atualizar(prodFisico);
                            System.out.println("📦 Estoque atualizado: " + prodFisico.getEstoque() + " unidades restantes");
                        }
                    }
                    
                    itemDAO.salvar(item);
                    pedidoDAO.atualizar(pedido);
                    System.out.println("✅ Item adicionado ao pedido!");
                    System.out.println("💰 Subtotal do item: R$ " + String.format("%.2f", item.getDouble()));
                    break;
                    
                case 2: // LISTAR ITENS
                    System.out.println("\n--- ITENS DO PEDIDO ---");
                    if (pedido.getItens().isEmpty()) {
                        System.out.println("⚠️ Nenhum item no pedido.");
                    } else {
                        System.out.println(String.format("%-5s | %-30s | %-10s | %-12s | %-12s", 
                            "ID", "Produto", "Qtd", "Preço Unit.", "Subtotal"));
                        System.out.println("-".repeat(75));
                        for (br.edu.ifpi.Model.ItemPedido i : pedido.getItens()) {
                            System.out.println(String.format("%-5d | %-30s | %-10d | R$ %-9.2f | R$ %-9.2f",
                                i.getId(),
                                i.getProduto().getNome().substring(0, Math.min(30, i.getProduto().getNome().length())),
                                i.getQuantidade(),
                                i.getProduto().getPreco(),
                                i.getDouble()));
                        }
                        System.out.println("-".repeat(75));
                        System.out.println("💰 TOTAL: R$ " + String.format("%.2f", pedido.getTotal()));
                    }
                    break;
                    
                case 3: // EDITAR QUANTIDADE
                    System.out.print("\nID do item para editar: ");
                    Long idEdit = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.ItemPedido itemEdit = itemDAO.buscarPorId(idEdit);
                    if (itemEdit != null && itemEdit.getPedido().getNumeroPedido().equals(pedido.getNumeroPedido())) {
                        System.out.println("Produto: " + itemEdit.getProduto().getNome());
                        System.out.println("Quantidade atual: " + itemEdit.getQuantidade());
                        
                        // Mostrar estoque disponível para produtos físicos
                        if (itemEdit.getProduto() instanceof br.edu.ifpi.Model.ProdutoFisico) {
                            br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) itemEdit.getProduto();
                            int estoqueDisponivel = prodFisico.getEstoque() + itemEdit.getQuantidade(); // Estoque + quantidade atual do item
                            System.out.println("📦 Estoque disponível (incluindo quantidade atual): " + estoqueDisponivel + " unidades");
                        }
                        
                        System.out.print("Nova quantidade: ");
                        int novaQtd = scanner.nextInt();
                        scanner.nextLine();
                        
                        if (novaQtd <= 0) {
                            System.out.println("❌ Quantidade deve ser maior que zero!");
                            break;
                        }
                        
                        // Validar estoque para produtos físicos
                        if (itemEdit.getProduto() instanceof br.edu.ifpi.Model.ProdutoFisico) {
                            br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) itemEdit.getProduto();
                            int qtdAtual = itemEdit.getQuantidade();
                            int diferenca = novaQtd - qtdAtual;
                            
                            if (diferenca > 0) { // Aumentando quantidade
                                if (!prodFisico.temEstoqueSuficiente(diferenca)) {
                                    System.out.println("❌ ESTOQUE INSUFICIENTE para aumentar quantidade!");
                                    System.out.println("   Estoque disponível: " + prodFisico.getEstoque() + " unidades");
                                    System.out.println("   Necessário: " + diferenca + " unidades adicionais");
                                    break;
                                }
                                prodFisico.diminuirEstoque(diferenca);
                            } else if (diferenca < 0) { // Diminuindo quantidade
                                prodFisico.aumentarEstoque(-diferenca); // diferenca é negativo, então inverte
                            }
                            
                            if (diferenca != 0) {
                                new br.edu.ifpi.DAO.ProdutoFisicoDAO().atualizar(prodFisico);
                                System.out.println("📦 Estoque atualizado: " + prodFisico.getEstoque() + " unidades");
                            }
                        }
                        
                        itemEdit.setQuantidade(novaQtd);
                        itemDAO.atualizar(itemEdit);
                        pedidoDAO.atualizar(pedido);
                        System.out.println("✅ Quantidade atualizada!");
                        System.out.println("💰 Novo subtotal: R$ " + String.format("%.2f", itemEdit.getDouble()));
                    } else {
                        System.out.println("❌ Item não encontrado neste pedido.");
                    }
                    break;
                    
                case 4: // REMOVER ITEM
                    System.out.print("\nID do item para remover: ");
                    Long idRem = scanner.nextLong();
                    scanner.nextLine();
                    br.edu.ifpi.Model.ItemPedido itemRem = itemDAO.buscarPorId(idRem);
                    if (itemRem != null && itemRem.getPedido().getNumeroPedido().equals(pedido.getNumeroPedido())) {
                        System.out.println("Produto: " + itemRem.getProduto().getNome());
                        System.out.println("Quantidade: " + itemRem.getQuantidade() + " unidades");
                        System.out.print("⚠️ Confirma a remoção? (S/N): ");
                        String confirmaRem = scanner.nextLine().trim().toUpperCase();
                        if (confirmaRem.equals("S") || confirmaRem.equals("SIM")) {
                            // Devolver estoque para produtos físicos
                            if (itemRem.getProduto() instanceof br.edu.ifpi.Model.ProdutoFisico) {
                                br.edu.ifpi.Model.ProdutoFisico prodFisico = (br.edu.ifpi.Model.ProdutoFisico) itemRem.getProduto();
                                prodFisico.aumentarEstoque(itemRem.getQuantidade());
                                new br.edu.ifpi.DAO.ProdutoFisicoDAO().atualizar(prodFisico);
                                System.out.println("📦 Estoque devolvido: " + prodFisico.getEstoque() + " unidades disponíveis");
                            }
                            
                            pedido.removerItem(itemRem); // Usa método do diagrama
                            itemDAO.remover(itemRem);
                            pedidoDAO.atualizar(pedido);
                            System.out.println("✅ Item removido!");
                        } else {
                            System.out.println("❌ Remoção cancelada.");
                        }
                    } else {
                        System.out.println("❌ Item não encontrado neste pedido.");
                    }
                    break;
                    
                case 0:
                    break;
                    
                default:
                    System.out.println("❌ Opção inválida!");
            }
            
            // Recarrega o pedido para atualizar a lista de itens
            pedido = pedidoDAO.buscarPorId(pedido.getNumeroPedido());
            
        } while (opcao != 0);
    }

    private static void menuPagamentos(Scanner scanner) {
        br.edu.ifpi.DAO.PagamentoDAO dao = new br.edu.ifpi.DAO.PagamentoDAO();
        br.edu.ifpi.DAO.PedidoDAO pedidoDAO = new br.edu.ifpi.DAO.PedidoDAO();
        int opcao;
        do {
            System.out.println("\n=== GERENCIAR PAGAMENTOS (BOLETOS) ===");
            System.out.println("1. Criar Boleto para Pedido");
            System.out.println("2. Listar Todos os Boletos");
            System.out.println("3. Ver Detalhes do Boleto");
            System.out.println("4. Remover Boleto");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1: // CRIAR BOLETO PARA PEDIDO
                    try {
                        System.out.println("\n--- CRIAR BOLETO PARA PEDIDO ---");
                        
                        // Passo 1: Solicitar número do pedido
                        System.out.print("📋 Número do Pedido: ");
                        Long numeroPedido = scanner.nextLong();
                        scanner.nextLine();
                        
                        // Passo 2: Buscar o pedido
                        br.edu.ifpi.Model.Pedido pedido = pedidoDAO.buscarPorId(numeroPedido);
                        if (pedido == null) {
                            System.out.println("❌ ERRO: Pedido #" + numeroPedido + " não encontrado!");
                            break;
                        }
                        
                        // Passo 3: Validar se pedido já tem pagamento
                        if (pedido.getPagamento() != null) {
                            System.out.println("⚠️ ATENÇÃO: Este pedido já possui um pagamento associado!");
                            System.out.println("   Tipo: " + pedido.getPagamento().getClass().getSimpleName());
                            System.out.println("   ID: " + pedido.getPagamento().getId());
                            System.out.print("Deseja substituir? (S/N): ");
                            String substituir = scanner.nextLine().trim().toUpperCase();
                            if (!substituir.equals("S") && !substituir.equals("SIM")) {
                                System.out.println("❌ Operação cancelada.");
                                break;
                            }
                        }
                        
                        // Passo 4: Validar se pedido tem itens
                        if (pedido.getItens().isEmpty()) {
                            System.out.println("⚠️ ATENÇÃO: Este pedido não possui itens!");
                            System.out.print("Deseja criar boleto mesmo assim? (S/N): ");
                            String continuar = scanner.nextLine().trim().toUpperCase();
                            if (!continuar.equals("S") && !continuar.equals("SIM")) {
                                System.out.println("❌ Operação cancelada.");
                                break;
                            }
                        }
                        
                        // Passo 5: Mostrar resumo do pedido
                        System.out.println("\n--- RESUMO DO PEDIDO ---");
                        System.out.println("📋 Número: #" + pedido.getNumeroPedido());
                        System.out.println("👤 Cliente: " + pedido.getCliente().getNome());
                        System.out.println("📇 CPF: " + pedido.getCliente().getCpf());
                        System.out.println("📦 Total de itens: " + pedido.getItens().size());
                        System.out.println("💰 Valor total: R$ " + String.format("%.2f", pedido.getTotal()));
                        System.out.println("📅 Data: " + pedido.getData());
                        System.out.println("📊 Status: " + pedido.getStatus());
                        
                        // Passo 6: Gerar código do boleto automaticamente
                        String codigoBoleto = gerarCodigoBoleto(pedido);
                        System.out.println("\n--- GERANDO BOLETO ---");
                        System.out.println("🔢 Código: " + codigoBoleto);
                        
                        // Passo 7: Calcular vencimento (7 dias a partir de hoje)
                        String vencimento = java.time.LocalDate.now().plusDays(7).toString();
                        System.out.println("📅 Vencimento: " + formatarData(vencimento));
                        System.out.println("💵 Valor: R$ " + String.format("%.2f", pedido.getTotal()));
                        
                        // Passo 8: Confirmar criação
                        System.out.print("\n✅ Confirma criação do boleto? (S/N): ");
                        String confirma = scanner.nextLine().trim().toUpperCase();
                        
                        if (!confirma.equals("S") && !confirma.equals("SIM")) {
                            System.out.println("❌ Criação de boleto cancelada.");
                            break;
                        }
                        
                        // Passo 9: Criar o boleto com valor
                        br.edu.ifpi.Model.Boleto boleto = new br.edu.ifpi.Model.Boleto(codigoBoleto, vencimento, pedido.getTotal());
                        
                        // Passo 10: Associar boleto ao pedido
                        pedido.setPagamento(boleto);
                        pedidoDAO.atualizar(pedido);
                        
                        System.out.println("\n✅ BOLETO CRIADO COM SUCESSO!");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("📋 PEDIDO: #" + pedido.getNumeroPedido());
                        System.out.println("👤 CLIENTE: " + pedido.getCliente().getNome());
                        System.out.println("📇 CPF: " + pedido.getCliente().getCpf());
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("🔢 CÓDIGO DO BOLETO:");
                        System.out.println("   " + codigoBoleto);
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("📅 VENCIMENTO: " + formatarData(vencimento));
                        System.out.println("💵 VALOR: R$ " + String.format("%.2f", pedido.getTotal()));
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("⚠️ IMPORTANTE:");
                        System.out.println("   - Boleto associado ao Pedido #" + pedido.getNumeroPedido());
                        System.out.println("   - Para finalizar a compra, vá em:");
                        System.out.println("     Menu Pedidos → Finalizar Pedido");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao criar boleto: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 2: // LISTAR TODOS OS BOLETOS
                    try {
                        System.out.println("\n--- LISTA DE BOLETOS ---");
                        java.util.List<br.edu.ifpi.Model.Pagamento> pagamentos = dao.listarTodos();
                        
                        if (pagamentos.isEmpty()) {
                            System.out.println("⚠️ Nenhum boleto cadastrado.");
                        } else {
                            int totalBoletos = 0;
                            System.out.println(String.format("%-5s | %-40s | %-12s | %-12s | %-10s", 
                                "ID", "Código do Boleto", "Vencimento", "Valor", "Status"));
                            System.out.println("-".repeat(95));
                            
                            for (br.edu.ifpi.Model.Pagamento p : pagamentos) {
                                if (p instanceof br.edu.ifpi.Model.Boleto) {
                                    br.edu.ifpi.Model.Boleto bol = (br.edu.ifpi.Model.Boleto) p;
                                    String venc = bol.getVencimento() != null ? formatarData(bol.getVencimento()) : "Não definido";
                                    String valorStr = bol.getValor() != null ? String.format("R$ %.2f", bol.getValor()) : "R$ 0,00";
                                    String status = bol.getStatusPagamento() != null ? bol.getStatusPagamento() : "PENDENTE";
                                    System.out.println(String.format("%-5d | %-40s | %-12s | %-12s | %-10s",
                                        bol.getId(),
                                        bol.getCodigoBoleto().substring(0, Math.min(40, bol.getCodigoBoleto().length())),
                                        venc,
                                        valorStr,
                                        status));
                                    totalBoletos++;
                                }
                            }
                            System.out.println("-".repeat(95));
                            System.out.println("📊 Total de boletos: " + totalBoletos);
                        }
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao listar boletos: " + e.getMessage());
                    }
                    break;
                    
                case 3: // VER DETALHES DO BOLETO
                    try {
                        System.out.print("\n📋 ID do boleto: ");
                        Long idBoleto = scanner.nextLong();
                        scanner.nextLine();
                        
                        br.edu.ifpi.Model.Pagamento pagamento = dao.buscarPorId(idBoleto);
                        
                        if (pagamento == null) {
                            System.out.println("❌ Boleto não encontrado!");
                            break;
                        }
                        
                        if (!(pagamento instanceof br.edu.ifpi.Model.Boleto)) {
                            System.out.println("❌ Este pagamento não é um boleto!");
                            break;
                        }
                        
                        br.edu.ifpi.Model.Boleto boleto = (br.edu.ifpi.Model.Boleto) pagamento;
                        
                        // Buscar pedido associado
                        br.edu.ifpi.Model.Pedido pedidoAssociado = null;
                        for (br.edu.ifpi.Model.Pedido ped : pedidoDAO.listarTodos()) {
                            if (ped.getPagamento() != null && ped.getPagamento().getId().equals(boleto.getId())) {
                                pedidoAssociado = ped;
                                break;
                            }
                        }
                        
                        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("           DETALHES DO BOLETO");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("🆔 ID: " + boleto.getId());
                        System.out.println("🔢 Código: " + boleto.getCodigoBoleto());
                        System.out.println("📅 Vencimento: " + (boleto.getVencimento() != null ? formatarData(boleto.getVencimento()) : "Não definido"));
                        System.out.println("💵 Valor: " + (boleto.getValor() != null ? String.format("R$ %.2f", boleto.getValor()) : "R$ 0,00"));
                        System.out.println("📊 Status: " + (boleto.getStatusPagamento() != null ? boleto.getStatusPagamento() : "PENDENTE"));
                        
                        if (pedidoAssociado != null) {
                            System.out.println("\n--- PEDIDO ASSOCIADO ---");
                            System.out.println("📋 Número: #" + pedidoAssociado.getNumeroPedido());
                            System.out.println("👤 Cliente: " + pedidoAssociado.getCliente().getNome());
                            System.out.println("📇 CPF: " + pedidoAssociado.getCliente().getCpf());
                            System.out.println("💰 Valor: R$ " + String.format("%.2f", pedidoAssociado.getTotal()));
                            System.out.println("📊 Status: " + pedidoAssociado.getStatus());
                        } else {
                            System.out.println("\n⚠️ Nenhum pedido associado a este boleto");
                        }
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao buscar detalhes: " + e.getMessage());
                    }
                    break;
                    
                case 4: // REMOVER BOLETO
                    try {
                        System.out.print("\n📋 ID do boleto para remover: ");
                        Long idRem = scanner.nextLong();
                        scanner.nextLine();
                        
                        br.edu.ifpi.Model.Pagamento pagamentoRem = dao.buscarPorId(idRem);
                        
                        if (pagamentoRem == null) {
                            System.out.println("❌ Boleto não encontrado!");
                            break;
                        }
                        
                        // Verificar se está associado a algum pedido
                        br.edu.ifpi.Model.Pedido pedidoAssociado = null;
                        for (br.edu.ifpi.Model.Pedido ped : pedidoDAO.listarTodos()) {
                            if (ped.getPagamento() != null && ped.getPagamento().getId().equals(pagamentoRem.getId())) {
                                pedidoAssociado = ped;
                                break;
                            }
                        }
                        
                        if (pedidoAssociado != null) {
                            System.out.println("⚠️ ATENÇÃO: Este boleto está associado ao Pedido #" + pedidoAssociado.getNumeroPedido());
                            System.out.print("Deseja remover mesmo assim? Isso desvinculará o pagamento do pedido. (S/N): ");
                            String confirma = scanner.nextLine().trim().toUpperCase();
                            if (!confirma.equals("S") && !confirma.equals("SIM")) {
                                System.out.println("❌ Remoção cancelada.");
                                break;
                            }
                            // Desvincular do pedido
                            pedidoAssociado.setPagamento(null);
                            pedidoDAO.atualizar(pedidoAssociado);
                        }
                        
                        dao.remover(pagamentoRem);
                        System.out.println("✅ Boleto removido com sucesso!");
                        
                    } catch (Exception e) {
                        System.out.println("❌ ERRO ao remover boleto: " + e.getMessage());
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }
}
