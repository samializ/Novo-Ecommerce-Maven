# 📚 GUIA COMPLETO DO SISTEMA E-COMMERCE - TODAS AS RELAÇÕES E FLUXOS

## 🎯 ÍNDICE
1. [Todas as Relações JPA (@ManyToOne, @OneToMany, @OneToOne)](#relacoes-jpa)
2. [Como Criar Boleto para Pagar](#criar-boleto)
3. [Como Finalizar Pedido com Boleto](#finalizar-pedido)
4. [Perguntas e Respostas Frequentes](#perguntas-frequentes)
5. [Enunciado Mastigado do Sistema](#enunciado-mastigado)

---

## 🔗 1. TODAS AS RELAÇÕES JPA DO SISTEMA {#relacoes-jpa}

### 📋 O que são as anotações de relacionamento?

As anotações JPA definem como as entidades (classes) se relacionam no banco de dados. Existem 4 tipos principais:

| Anotação | Significado | Exemplo do Mundo Real |
|----------|-------------|------------------------|
| `@OneToOne` | 1 para 1 | Uma pessoa tem UM CPF |
| `@OneToMany` | 1 para Muitos | Um cliente tem VÁRIOS pedidos |
| `@ManyToOne` | Muitos para 1 | Vários pedidos pertencem a UM cliente |
| `@ManyToMany` | Muitos para Muitos | Vários alunos têm várias disciplinas |

---

### 🏗️ RELAÇÃO 1: Cliente → Pedidos (@OneToMany)

**Arquivo:** `Cliente.java` (linha 23)

```java
@OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
private List<Pedido> pedidos = new ArrayList<>();
```

#### O que significa?

- **@OneToMany**: UM cliente pode ter VÁRIOS pedidos
- **mappedBy = "cliente"**: O relacionamento é mapeado pelo campo `cliente` na classe `Pedido`
- **fetch = FetchType.EAGER**: Quando buscar um cliente, traz os pedidos junto (carregamento imediato)
- **List<Pedido>**: Lista de pedidos do cliente

#### No mundo real:
```
Cliente: João Silva
├── Pedido #1 (R$ 100,00)
├── Pedido #2 (R$ 250,00)
└── Pedido #3 (R$ 450,00)
```

#### No código:
```java
Cliente cliente = clienteDAO.buscarPorId(1);
System.out.println("Total de pedidos: " + cliente.getPedidos().size()); // 3
```

---

### 🏗️ RELAÇÃO 2: Cliente → Histórico (@OneToOne)

**Arquivo:** `Cliente.java` (linha 26)

```java
@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
@JoinColumn(name = "historico_id")
private Historico historico;
```

#### O que significa?

- **@OneToOne**: UM cliente tem UM histórico (e vice-versa)
- **cascade = CascadeType.ALL**: Operações no cliente afetam o histórico
  - Se salvar cliente → salva histórico
  - Se deletar cliente → deleta histórico
- **@JoinColumn(name = "historico_id")**: Nome da coluna que armazena a chave estrangeira
- **fetch = FetchType.EAGER**: Traz histórico junto com cliente

#### No mundo real:
```
Cliente: João Silva → Histórico de João
Cliente: Maria Santos → Histórico de Maria
```

#### No código:
```java
Cliente cliente = new Cliente("João", "joao@email.com", "senha", "123.456.789-00", "Rua A");
// Histórico é criado automaticamente no construtor
System.out.println("Histórico ID: " + cliente.getHistorico().getId());
```

---

### 🏗️ RELAÇÃO 3: Pedido → Itens (@OneToMany)

**Arquivo:** `Pedido.java` (linha 33)

```java
@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private List<ItemPedido> itens = new ArrayList<>();
```

#### O que significa?

- **@OneToMany**: UM pedido tem VÁRIOS itens
- **mappedBy = "pedido"**: Mapeado pelo campo `pedido` em `ItemPedido`
- **cascade = CascadeType.ALL**: Operações em cascata
  - Salvar pedido → salva todos os itens
  - Deletar pedido → deleta todos os itens
- **List<ItemPedido>**: Lista de itens do pedido

#### No mundo real:
```
Pedido #123
├── Item 1: Mouse x2 = R$ 300,00
├── Item 2: Teclado x1 = R$ 450,00
└── Item 3: Monitor x1 = R$ 1.200,00
Total: R$ 1.950,00
```

#### No código:
```java
Pedido pedido = pedidoDAO.buscarPorId(123);
for (ItemPedido item : pedido.getItens()) {
    System.out.println(item.getProduto().getNome() + " x" + item.getQuantidade());
}
```

---

### 🏗️ RELAÇÃO 4: Pedido → Cliente (@ManyToOne)

**Arquivo:** `Pedido.java` (linha 36)

```java
@ManyToOne(fetch = FetchType.EAGER)
private Cliente cliente;
```

#### O que significa?

- **@ManyToOne**: VÁRIOS pedidos pertencem a UM cliente
- **fetch = FetchType.EAGER**: Traz cliente junto com pedido
- Esta é a **outra ponta** da relação @OneToMany do Cliente

#### No mundo real:
```
Pedido #1 → Cliente: João
Pedido #2 → Cliente: João
Pedido #3 → Cliente: Maria
```

#### No código:
```java
Pedido pedido = pedidoDAO.buscarPorId(1);
System.out.println("Cliente: " + pedido.getCliente().getNome()); // João
```

---

### 🏗️ RELAÇÃO 5: Pedido → Pagamento (@OneToOne)

**Arquivo:** `Pedido.java` (linha 39)

```java
@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private Pagamento pagamento;
```

#### O que significa?

- **@OneToOne**: UM pedido tem UM pagamento (boleto, cartão, etc.)
- **cascade = CascadeType.ALL**: Salvar/deletar pedido afeta pagamento
- **Pagamento**: Classe abstrata (pode ser Boleto, CartaoCredito, etc.)

#### No mundo real:
```
Pedido #123 → Boleto #456
   Código: 34191.79001 01043.510047...
   Vencimento: 30/10/2025
   Valor: R$ 750,00
```

#### No código:
```java
Pedido pedido = pedidoDAO.buscarPorId(123);
if (pedido.getPagamento() != null) {
    System.out.println("Forma de pagamento: " + pedido.getPagamento().getClass().getSimpleName());
    // "Boleto"
}
```

---

### 🏗️ RELAÇÃO 6: ItemPedido → Produto (@ManyToOne)

**Arquivo:** `ItemPedido.java` (linha 19)

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "produto_id")
private Produto produto;
```

#### O que significa?

- **@ManyToOne**: VÁRIOS itens podem ter o MESMO produto
- **@JoinColumn(name = "produto_id")**: Coluna que guarda ID do produto
- **fetch = FetchType.EAGER**: Traz produto junto com item

#### No mundo real:
```
ItemPedido #1 → Produto: Mouse Gamer
ItemPedido #2 → Produto: Mouse Gamer (mesmo produto)
ItemPedido #3 → Produto: Teclado
```

#### No código:
```java
ItemPedido item = itemDAO.buscarPorId(1);
System.out.println("Produto: " + item.getProduto().getNome()); // Mouse Gamer
System.out.println("Preço: R$ " + item.getProduto().getPreco()); // R$ 150,00
System.out.println("Quantidade: " + item.getQuantidade()); // 2
System.out.println("Subtotal: R$ " + item.getDouble()); // R$ 300,00
```

---

### 🏗️ RELAÇÃO 7: ItemPedido → Pedido (@ManyToOne)

**Arquivo:** `ItemPedido.java` (linha 25)

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "pedido_id")
private Pedido pedido;
```

#### O que significa?

- **@ManyToOne**: VÁRIOS itens pertencem a UM pedido
- Esta é a **outra ponta** da relação @OneToMany do Pedido

#### No mundo real:
```
Item #1 → Pedido #123
Item #2 → Pedido #123
Item #3 → Pedido #456
```

#### No código:
```java
ItemPedido item = itemDAO.buscarPorId(1);
System.out.println("Pedido: #" + item.getPedido().getNumeroPedido()); // #123
System.out.println("Cliente: " + item.getPedido().getCliente().getNome()); // João
```

---

### 🏗️ RELAÇÃO 8: Histórico → Pedidos (@OneToMany)

**Arquivo:** `Historico.java` (linha 18)

```java
@OneToMany(fetch = FetchType.EAGER)
@JoinTable(name = "historico_pedidos",
    joinColumns = @JoinColumn(name = "historico_id"),
    inverseJoinColumns = @JoinColumn(name = "pedido_id"))
private List<Pedido> pedidos = new ArrayList<>();
```

#### O que significa?

- **@OneToMany**: UM histórico tem VÁRIOS pedidos
- **@JoinTable**: Cria uma tabela intermediária `historico_pedidos`
- **joinColumns**: Coluna do histórico
- **inverseJoinColumns**: Coluna do pedido

#### No mundo real:
```
Histórico de João:
├── Pedido #1 (23/10/2025)
├── Pedido #2 (22/10/2025)
└── Pedido #3 (20/10/2025)
```

---

## 📊 DIAGRAMA COMPLETO DE RELACIONAMENTOS

```
┌─────────────────────────────────────────────────────────────────┐
│                     DIAGRAMA DE RELAÇÕES                        │
└─────────────────────────────────────────────────────────────────┘

            ┌─────────────┐
            │   Cliente   │
            │  (Usuario)  │
            └──────┬──────┘
                   │
         ┌─────────┴─────────┐
         │                   │
    @OneToMany          @OneToOne
         │                   │
    ┌────▼─────┐      ┌──────▼────────┐
    │  Pedido  │      │   Histórico   │
    └────┬─────┘      └───────────────┘
         │                   
    ┌────┴────┬──────────────────────┐
    │         │                      │
@OneToMany  @ManyToOne          @OneToOne
    │         │                      │
┌───▼────┐  ┌▼────────┐      ┌──────▼────────┐
│ItemPed │  │ Cliente │      │  Pagamento    │
└───┬────┘  └─────────┘      │  (abstrato)   │
    │                        └───────┬───────┘
@ManyToOne                           │
    │                            @extends
┌───▼────────┐                       │
│  Produto   │                  ┌────▼─────┐
│ (abstrato) │                  │  Boleto  │
└─────┬──────┘                  └──────────┘
      │
   @extends
      │
      ├─────────────────┐
      │                 │
┌─────▼────────┐  ┌────▼─────────┐
│ProdutoFisico │  │ProdutoDigital│
│  (estoque)   │  │   (url)      │
└──────────────┘  └──────────────┘
```

---

## 💳 2. COMO CRIAR BOLETO PARA PAGAR {#criar-boleto}

### 📝 Passo a Passo COMPLETO

#### ⚠️ IMPORTANTE: Você precisa ter um PEDIDO criado antes!

---

### PASSO 1: Criar o Pedido (se ainda não criou)

**Menu:** `Menu Principal` → `4. Gerenciar Pedidos` → `1. Criar Pedido`

```
=== GERENCIAR PEDIDOS ===
1. Criar Pedido
```

**O que fazer:**
```
ID do cliente: 1
```

**O que acontece:**
- Sistema cria pedido VAZIO
- Status: PENDENTE
- Sem itens
- Sem pagamento ainda

**Resultado:**
```
✅ Pedido criado com sucesso! ID: 123
```

---

### PASSO 2: Adicionar Itens ao Pedido

**Menu:** `Menu Principal` → `4. Gerenciar Pedidos` → `4. Gerenciar Itens do Pedido`

```
Número do pedido: 123
```

**Submenu abre:**
```
=== ITENS DO PEDIDO #123 ===
1. Adicionar Item
2. Listar Itens
3. Editar Quantidade do Item
4. Remover Item
0. Voltar
```

**Escolha:** `1. Adicionar Item`

```
ID do produto: 5
Produto: Mouse Gamer - R$ 150.00
📦 Estoque disponível: 10 unidades
Quantidade: 2

✅ Item adicionado ao pedido!
📦 Estoque atualizado: 8 unidades restantes
💰 Subtotal do item: R$ 300.00
```

**Repita** para adicionar mais itens se quiser.

**Escolha:** `0. Voltar` quando terminar

---

### PASSO 3: CRIAR BOLETO (⚠️ AGORA SIM!)

**Menu:** `Menu Principal` → `5. Gerenciar Pagamentos` → `1. Criar Boleto para Pedido`

```
=== GERENCIAR PAGAMENTOS (BOLETOS) ===
1. Criar Boleto para Pedido   ← ESCOLHA ESTA OPÇÃO!
```

#### O que o sistema pergunta:

**Pergunta 1:**
```
📋 Número do Pedido: 123
```
**Explicação:** Digite o número do pedido que você criou no PASSO 1

---

**O sistema mostra o RESUMO:**
```
--- RESUMO DO PEDIDO ---
📋 Número: #123
👤 Cliente: João Silva
📇 CPF: 123.456.789-00
📦 Total de itens: 2
💰 Valor total: R$ 750.00
📅 Data: 2025-10-23T23:00:00
📊 Status: PENDENTE
```

---

**O sistema GERA O BOLETO automaticamente:**
```
--- GERANDO BOLETO ---
🔢 Código: 00123.17305 00001.000000 00000.000123 1 00000000075000
📅 Vencimento: 30/10/2025
💵 Valor: R$ 750.00
```

**Explicação:**
- **Código do boleto**: Gerado automaticamente (linha digitável)
- **Vencimento**: 7 dias a partir de hoje
- **Valor**: Pego automaticamente do pedido

---

**Pergunta 2:**
```
✅ Confirma criação do boleto? (S/N): S
```
**O que digitar:** `S` (para SIM) ou `N` (para NÃO)

---

**Resultado final:**
```
✅ BOLETO CRIADO COM SUCESSO!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 PEDIDO: #123
👤 CLIENTE: João Silva
📇 CPF: 123.456.789-00
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔢 CÓDIGO DO BOLETO:
   00123.17305 00001.000000 00000.000123 1 00000000075000
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📅 VENCIMENTO: 30/10/2025
💵 VALOR: R$ 750.00
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ IMPORTANTE:
   - Boleto associado ao Pedido #123
   - Para finalizar a compra, vá em:
     Menu Pedidos → Finalizar Pedido
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 🎯 O QUE FOI FEITO AUTOMATICAMENTE:

1. ✅ Sistema pegou o pedido #123
2. ✅ Sistema pegou o valor total (R$ 750,00)
3. ✅ Sistema gerou código do boleto
4. ✅ Sistema definiu vencimento (7 dias)
5. ✅ Sistema criou o boleto no banco de dados
6. ✅ **Sistema ASSOCIOU o boleto ao pedido** ← IMPORTANTE!

---

### 💡 Perguntas Comuns:

**P: Preciso informar o valor do boleto?**  
R: NÃO! O sistema pega automaticamente do pedido.

**P: Preciso informar o código do boleto?**  
R: NÃO! O sistema gera automaticamente.

**P: Como sei qual pedido usar?**  
R: Use o número que apareceu quando você criou o pedido.

**P: Posso criar boleto para pedido sem itens?**  
R: Sim, mas o sistema avisa. É recomendado adicionar itens primeiro.

**P: Posso criar outro boleto para o mesmo pedido?**  
R: Sim, mas o sistema avisa que já existe um e pergunta se quer substituir.

---

## ✅ 3. COMO FINALIZAR PEDIDO COM BOLETO {#finalizar-pedido}

### 📝 Passo a Passo COMPLETO

⚠️ **IMPORTANTE:** Você precisa ter criado o boleto ANTES (veja seção anterior)

---

### PASSO 1: Ir ao Menu de Pedidos

**Menu:** `Menu Principal` → `4. Gerenciar Pedidos` → `6. Finalizar Pedido`

```
=== GERENCIAR PEDIDOS ===
6. Finalizar Pedido (Processar Venda)  ← ESCOLHA ESTA OPÇÃO!
```

---

### PASSO 2: Informar o Número do Pedido

```
📋 Número do pedido para finalizar: 123
```

**Explicação:** Digite o número do pedido que tem o boleto criado

---

### PASSO 3: Sistema Mostra o Resumo

```
=== FINALIZANDO PEDIDO #123 ===
Cliente: João Silva
Total de itens: 2
Valor total: R$ 750.00

⚠️ Confirma a finalização? (S/N): 
```

**O que digitar:** `S` (para SIM) ou `N` (para NÃO)

---

### PASSO 4: Sistema Valida TUDO Automaticamente

#### Validação 1: Tem pagamento (boleto)?
```java
if (pedido.getPagamento() == null) {
    ❌ ERRO: "Nenhum pagamento associado ao pedido!"
    return false;
}
```

#### Validação 2: Tem itens?
```java
if (pedido.getItens().isEmpty()) {
    ❌ ERRO: "Pedido sem itens!"
    return false;
}
```

#### Validação 3: Processa o pagamento
```java
boolean pagamentoOk = pedido.getPagamento().processarPagamento();
// Chama o método emitirBoleto() do Boleto
```

---

### PASSO 5: Resultado da Finalização

**Se TUDO estiver OK:**
```
✅ Boleto emitido: 00123.17305 00001.000000 00000.000123 1 00000000075000
📅 Vencimento: 30/10/2025
✅ Pedido finalizado com sucesso!
📦 Número do pedido: 123
💰 Valor total: R$ 750.00
```

**O que aconteceu:**
1. ✅ Sistema verificou que tem boleto
2. ✅ Sistema verificou que tem itens
3. ✅ Sistema processou o boleto
4. ✅ Status do pedido mudou para: **FINALIZADO**

---

**Se NÃO tiver boleto:**
```
❌ ERRO: Nenhum pagamento associado ao pedido!

💡 SOLUÇÃO:
   Vá em: Menu Principal → 5. Gerenciar Pagamentos → 1. Criar Boleto
```

---

**Se NÃO tiver itens:**
```
❌ ERRO: Pedido sem itens!

💡 SOLUÇÃO:
   Vá em: Menu Principal → 4. Gerenciar Pedidos → 4. Gerenciar Itens
```

---

## ❓ 4. PERGUNTAS E RESPOSTAS FREQUENTES {#perguntas-frequentes}

### 📋 Sobre Relações

**P1: O que significa @OneToMany?**  
R: Um tem muitos. Exemplo: Um cliente tem muitos pedidos.

**P2: O que significa @ManyToOne?**  
R: Muitos têm um. Exemplo: Muitos pedidos pertencem a um cliente.

**P3: O que significa @OneToOne?**  
R: Um tem um. Exemplo: Um pedido tem um pagamento.

**P4: O que é mappedBy?**  
R: Indica qual campo na outra classe mapeia o relacionamento.

**P5: O que é cascade = CascadeType.ALL?**  
R: Operações em cascata. Se salvar A, salva B automaticamente.

**P6: O que é fetch = FetchType.EAGER?**  
R: Carregamento imediato. Traz tudo junto na consulta.

---

### 💳 Sobre Boletos

**P7: Preciso criar boleto para cada pedido?**  
R: SIM! Todo pedido precisa de um pagamento (boleto) para ser finalizado.

**P8: O boleto é criado automaticamente?**  
R: NÃO! Você deve ir no Menu de Pagamentos e criar manualmente.

**P9: Onde eu informo o valor do boleto?**  
R: Em lugar nenhum! O sistema pega do pedido automaticamente.

**P10: Onde eu informo o código do boleto?**  
R: Em lugar nenhum! O sistema gera automaticamente.

**P11: Como associar boleto ao pedido?**  
R: Ao criar o boleto, você informa o número do pedido. Sistema associa automaticamente.

**P12: Posso criar boleto antes de adicionar itens?**  
R: Pode, mas o valor será R$ 0,00. Recomendado adicionar itens primeiro.

**P13: Posso ter 2 boletos no mesmo pedido?**  
R: NÃO! Um pedido só pode ter UM pagamento. Se criar outro, substitui o anterior.

---

### 🛒 Sobre Pedidos

**P14: Qual a ordem correta: pedido, itens ou boleto?**  
R: 1º Pedido → 2º Itens → 3º Boleto → 4º Finalizar

**P15: Posso finalizar pedido sem boleto?**  
R: NÃO! Sistema dá erro: "Nenhum pagamento associado".

**P16: Posso finalizar pedido sem itens?**  
R: NÃO! Sistema dá erro: "Pedido sem itens".

**P17: O que acontece ao finalizar o pedido?**  
R: Status muda para "FINALIZADO" e boleto é processado (emitido).

**P18: Preciso informar qual boleto ao finalizar?**  
R: NÃO! O boleto já está associado ao pedido. Sistema usa automaticamente.

---

### 💰 Sobre Pagamentos

**P19: Onde fica o menu de pagamentos?**  
R: Menu Principal → 5. Gerenciar Pagamentos

**P20: Onde fica o menu de pedidos?**  
R: Menu Principal → 4. Gerenciar Pedidos

**P21: Por que tem 2 menus separados?**  
R: Separação de responsabilidades. Pedidos gerencia carrinho, Pagamentos gerencia formas de pagamento.

**P22: Pagamento é só boleto?**  
R: Por enquanto sim. No futuro pode ter cartão, PIX, etc.

---

## 📖 5. ENUNCIADO MASTIGADO DO SISTEMA {#enunciado-mastigado}

### 🎯 OBJETIVO DO SISTEMA

Criar um sistema de e-commerce onde:
- Clientes fazem pedidos
- Pedidos têm vários itens (produtos)
- Produtos podem ser físicos (com estoque) ou digitais (com URL)
- Pedidos precisam de pagamento (boleto)
- Sistema controla estoque automaticamente

---

### 📋 FLUXO COMPLETO DE UMA COMPRA

#### FASE 1: PREPARAÇÃO (Cadastros)

1. **Cadastrar Cliente** (Menu Clientes → Cadastrar)
   - Informar: Nome, CPF, Email, Senha, Endereço
   - Sistema valida CPF e email
   - Cliente recebe um ID

2. **Cadastrar Produtos** (Menu Produtos)
   - **Produto Físico:** Nome, Preço, Peso, Estoque inicial
   - **Produto Digital:** Nome, Preço, URL de download
   - Sistema valida valores positivos

---

#### FASE 2: CRIAÇÃO DO PEDIDO

3. **Criar Pedido** (Menu Pedidos → Criar)
   - Informar: ID do cliente
   - Sistema cria pedido VAZIO
   - Status: PENDENTE
   - Pedido recebe um número

---

#### FASE 3: MONTAGEM DO CARRINHO

4. **Adicionar Itens** (Menu Pedidos → Gerenciar Itens)
   - Informar: Número do pedido
   - Para cada produto:
     - Informar: ID do produto, Quantidade
     - Sistema valida estoque (produtos físicos)
     - Sistema diminui estoque automaticamente
     - Sistema adiciona item ao pedido
     - Sistema atualiza total do pedido

---

#### FASE 4: CRIAÇÃO DO PAGAMENTO (⚠️ MUDA DE MENU!)

5. **Criar Boleto** (Menu Pagamentos → Criar Boleto)
   - Informar: Número do pedido
   - Sistema mostra resumo do pedido
   - Sistema gera código do boleto automaticamente
   - Sistema calcula vencimento (7 dias)
   - Sistema pega valor do pedido automaticamente
   - Sistema **ASSOCIA** boleto ao pedido
   - Boleto fica salvo no banco de dados

---

#### FASE 5: FINALIZAÇÃO (⚠️ VOLTA AO MENU PEDIDOS!)

6. **Finalizar Pedido** (Menu Pedidos → Finalizar)
   - Informar: Número do pedido
   - Sistema valida:
     - ✅ Tem boleto associado?
     - ✅ Tem itens no pedido?
   - Sistema processa boleto (emite)
   - Sistema muda status para: FINALIZADO
   - PRONTO! Compra concluída!

---

### 🔄 RELACIONAMENTOS EXPLICADOS DE FORMA SIMPLES

#### Cliente e Pedido
```
Um CLIENTE pode fazer VÁRIOS PEDIDOS
Um PEDIDO pertence a UM CLIENTE

João Silva (Cliente)
├── Pedido #1
├── Pedido #2
└── Pedido #3
```

**No código:**
- Cliente tem: `List<Pedido> pedidos`
- Pedido tem: `Cliente cliente`

---

#### Pedido e ItemPedido
```
Um PEDIDO tem VÁRIOS ITENS
Um ITEM pertence a UM PEDIDO

Pedido #123
├── Item: Mouse x2
├── Item: Teclado x1
└── Item: Monitor x1
```

**No código:**
- Pedido tem: `List<ItemPedido> itens`
- ItemPedido tem: `Pedido pedido`

---

#### ItemPedido e Produto
```
Um ITEM tem UM PRODUTO
Um PRODUTO pode estar em VÁRIOS ITENS

Mouse Gamer (Produto)
├── Item do Pedido #123
├── Item do Pedido #456
└── Item do Pedido #789
```

**No código:**
- ItemPedido tem: `Produto produto`
- Produto NÃO precisa ter lista de itens

---

#### Pedido e Pagamento (Boleto)
```
Um PEDIDO tem UM PAGAMENTO
Um PAGAMENTO pertence a UM PEDIDO

Pedido #123 ← → Boleto #456
```

**No código:**
- Pedido tem: `Pagamento pagamento`
- Pagamento NÃO tem referência ao pedido (unidirecional)

---

### 🎓 RESUMO FINAL - PASSO A PASSO

```
┌─────────────────────────────────────────────────────────────┐
│                   FLUXO COMPLETO                            │
└─────────────────────────────────────────────────────────────┘

1️⃣ CADASTRAR CLIENTE
   └─→ Menu Clientes → Cadastrar
       Input: Nome, CPF, Email, Senha, Endereço

2️⃣ CADASTRAR PRODUTOS
   └─→ Menu Produtos → Físico ou Digital
       Input: Nome, Preço, Estoque/URL

3️⃣ CRIAR PEDIDO
   └─→ Menu Pedidos → Criar
       Input: ID do Cliente
       Output: Número do Pedido

4️⃣ ADICIONAR ITENS
   └─→ Menu Pedidos → Gerenciar Itens
       Input: Número do Pedido, ID Produto, Quantidade
       Sistema: Valida estoque, diminui automaticamente

5️⃣ CRIAR BOLETO (⚠️ OUTRO MENU!)
   └─→ Menu Pagamentos → Criar Boleto
       Input: Número do Pedido
       Sistema: Gera código, calcula vencimento, pega valor
       Output: Boleto associado ao Pedido

6️⃣ FINALIZAR PEDIDO (⚠️ VOLTA AO MENU PEDIDOS!)
   └─→ Menu Pedidos → Finalizar
       Input: Número do Pedido
       Sistema: Valida (tem boleto? tem itens?), processa
       Output: Status = FINALIZADO ✅

🎉 COMPRA CONCLUÍDA!
```

---

### 💡 DICAS IMPORTANTES

1. **Sempre criar nesta ordem:** Cliente → Pedido → Itens → Boleto → Finalizar
2. **Boleto é criado em OUTRO MENU** (Menu Pagamentos, não Menu Pedidos)
3. **Não precisa informar valor nem código do boleto** (sistema gera tudo)
4. **Ao criar boleto, informar o número do pedido** (sistema associa automaticamente)
5. **Ao finalizar, sistema usa o boleto associado** (não precisa informar qual)
6. **Sistema valida tudo automaticamente** (estoque, pagamento, itens)

---

**Data de Criação:** 23/10/2025  
**Sistema:** E-commerce Maven com JPA/Hibernate  
**Versão:** Completa e Documentada  
**Status:** ✅ Pronto para uso
