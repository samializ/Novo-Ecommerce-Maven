# 🔄 FLUXO COMPLETO DO SISTEMA E-COMMERCE

## 📊 DIAGRAMA DE FLUXO GERAL

```
CLIENTE → PEDIDO → ITENS → PRODUTOS
                ↓
           PAGAMENTO → BOLETO
                ↓
           FINALIZAÇÃO
                ↓
           HISTÓRICO
```

---

## 🎯 PERGUNTA PRINCIPAL: COMO GERENCIAR PAGAMENTOS SE RELACIONA COM GERENCIAR PEDIDOS?

### ⚠️ IMPORTANTE: São 2 Menus SEPARADOS mas RELACIONADOS

O sistema tem **2 menus distintos**:
1. **Menu de Pedidos** (opção 4 do menu principal)
2. **Menu de Pagamentos** (opção 5 do menu principal)

---

## 📝 FLUXO DETALHADO: DO PEDIDO ATÉ O PAGAMENTO

### 🟢 FASE 1: CRIAÇÃO DO PEDIDO (Menu Pedidos - Opção 1)

**Onde:** Menu Principal → 4. Gerenciar Pedidos → 1. Criar Pedido

**O que acontece:**
1. Sistema solicita ID do cliente
2. Cliente é buscado no banco de dados
3. Pedido é criado com:
   - Status: "PENDENTE"
   - Data atual
   - Cliente associado
   - Lista de itens: VAZIA
   - Total: R$ 0,00
   - **SEM PAGAMENTO ainda**

**Estado do Pedido após criação:**
```
Pedido #123
├── Cliente: João Silva (CPF: 123.456.789-00)
├── Status: PENDENTE
├── Data: 23/10/2025
├── Itens: [] (vazio)
├── Total: R$ 0,00
└── Pagamento: null (não existe ainda)
```

---

### 🟡 FASE 2: ADIÇÃO DE ITENS (Menu Pedidos - Opção 4)

**Onde:** Menu Principal → 4. Gerenciar Pedidos → 4. Gerenciar Itens do Pedido

**O que acontece:**
1. Sistema solicita número do pedido
2. Abre submenu de itens do pedido
3. Permite:
   - **Adicionar itens** (com validação de estoque)
   - **Editar quantidade** (com validação de estoque)
   - **Remover itens** (devolve estoque)
   - **Listar itens**

**Validações automáticas:**
- ✅ Verifica estoque disponível (produtos físicos)
- ✅ Diminui estoque ao adicionar item
- ✅ Devolve estoque ao remover item
- ✅ Ajusta estoque ao editar quantidade
- ✅ Calcula total automaticamente

**Estado do Pedido após adicionar itens:**
```
Pedido #123
├── Cliente: João Silva (CPF: 123.456.789-00)
├── Status: PENDENTE
├── Data: 23/10/2025
├── Itens: 
│   ├── Mouse Gamer x2 = R$ 300,00
│   └── Teclado Mecânico x1 = R$ 450,00
├── Total: R$ 750,00 (calculado automaticamente)
└── Pagamento: null (AINDA não existe)
```

---

### 🔵 FASE 3: CRIAÇÃO DO PAGAMENTO (Menu Pagamentos - Opção 1)

**⚠️ ATENÇÃO:** Agora você sai do Menu de Pedidos e vai para o **Menu de Pagamentos**!

**Onde:** Menu Principal → **5. Gerenciar Pagamentos** → 1. Criar Pagamento/Boleto

**O que acontece:**
1. Sistema solicita **número do pedido** que receberá o pagamento
2. Pedido é buscado no banco
3. Sistema exibe:
   - Número do pedido
   - Cliente
   - Valor total
4. Sistema cria **Boleto** automaticamente com:
   - Valor = Total do pedido
   - Status: "PENDENTE"
   - Data de vencimento (calculada)
   - Código de barras (gerado)
5. **Boleto é associado ao Pedido**

**Estado do Pedido após criar pagamento:**
```
Pedido #123
├── Cliente: João Silva (CPF: 123.456.789-00)
├── Status: PENDENTE
├── Data: 23/10/2025
├── Itens: 
│   ├── Mouse Gamer x2 = R$ 300,00
│   └── Teclado Mecânico x1 = R$ 450,00
├── Total: R$ 750,00
└── Pagamento: Boleto #456 ✅ (AGORA EXISTE!)
    ├── Valor: R$ 750,00
    ├── Status: PENDENTE
    ├── Vencimento: 30/10/2025
    └── Código: 34191.79001 01043.510047 91020.150008 1 84310000075000
```

---

### 🟣 FASE 4: FINALIZAÇÃO DO PEDIDO (Menu Pedidos - Opção 6)

**⚠️ ATENÇÃO:** Você volta ao Menu de Pedidos para finalizar!

**Onde:** Menu Principal → 4. Gerenciar Pedidos → **6. Finalizar Pedido**

**O que acontece:**
1. Sistema solicita número do pedido
2. Exibe resumo:
   - Cliente
   - Total de itens
   - Valor total
3. Solicita confirmação
4. **Validações automáticas:**
   - ✅ Pedido tem pagamento associado?
   - ✅ Pedido tem pelo menos 1 item?
5. Se validado:
   - Chama `pedido.finalizarPedido()`
   - Processa pagamento: `pagamento.processarPagamento()`
   - Atualiza status do pedido:
     - Se pagamento OK: "FINALIZADO"
     - Se pagamento recusado: "PAGAMENTO_RECUSADO"

**Estado final do Pedido:**
```
Pedido #123
├── Cliente: João Silva (CPF: 123.456.789-00)
├── Status: FINALIZADO ✅
├── Data: 23/10/2025
├── Itens: 
│   ├── Mouse Gamer x2 = R$ 300,00
│   └── Teclado Mecânico x1 = R$ 450,00
├── Total: R$ 750,00
└── Pagamento: Boleto #456
    ├── Valor: R$ 750,00
    ├── Status: PAGO ✅
    ├── Vencimento: 30/10/2025
    └── Código: 34191.79001 01043.510047 91020.150008 1 84310000075000
```

---

## 🔄 FLUXO COMPLETO PASSO A PASSO

### Cenário: Cliente faz uma compra do zero

```
PASSO 1: Cadastrar Cliente (Menu Clientes)
│
├─→ Menu Principal → 3. Gerenciar Clientes → 1. Cadastrar
│   Input: Nome, CPF, Email, Senha, Endereço
│   Output: Cliente #789 criado
│
PASSO 2: Criar Pedido (Menu Pedidos)
│
├─→ Menu Principal → 4. Gerenciar Pedidos → 1. Criar Pedido
│   Input: ID do Cliente (789)
│   Output: Pedido #123 criado (VAZIO, sem itens)
│
PASSO 3: Adicionar Itens ao Pedido (Menu Pedidos)
│
├─→ Menu Principal → 4. Gerenciar Pedidos → 4. Gerenciar Itens
│   │
│   ├─→ Informar número do pedido (123)
│   │
│   ├─→ Submenu abre:
│   │   ├─→ 1. Adicionar Item
│   │   │   Input: ID produto (5), Quantidade (2)
│   │   │   Validação: Estoque suficiente?
│   │   │   Action: Diminui estoque, adiciona item
│   │   │   Output: Item adicionado! Estoque atualizado
│   │   │
│   │   ├─→ 1. Adicionar Item (novamente)
│   │   │   Input: ID produto (7), Quantidade (1)
│   │   │   Output: Item adicionado!
│   │   │
│   │   └─→ 0. Voltar
│   │
│   Output: Pedido #123 agora tem 2 itens, Total: R$ 750,00
│
PASSO 4: Criar Pagamento (Menu Pagamentos) ⚠️ MUDOU DE MENU!
│
├─→ Menu Principal → 5. Gerenciar Pagamentos → 1. Criar Pagamento
│   Input: Número do pedido (123)
│   Output: Boleto #456 criado e associado ao Pedido #123
│   Valor: R$ 750,00 (pegou do total do pedido)
│
PASSO 5: Finalizar Pedido (Menu Pedidos) ⚠️ VOLTOU AO MENU PEDIDOS!
│
└─→ Menu Principal → 4. Gerenciar Pedidos → 6. Finalizar Pedido
    Input: Número do pedido (123)
    Validações:
    ├─→ Tem pagamento? SIM ✅ (Boleto #456)
    ├─→ Tem itens? SIM ✅ (2 itens)
    └─→ Processar pagamento
        └─→ pagamento.processarPagamento()
            ├─→ Boleto processa
            └─→ Retorna sucesso
    
    Output: ✅ Pedido finalizado com sucesso!
            Status: FINALIZADO
            Pagamento: APROVADO
```

---

## 🤔 POR QUE PAGAMENTO É UM MENU SEPARADO?

### Razões de Design:

1. **Separação de Responsabilidades**
   - Pedidos: gerencia produtos, itens, cliente
   - Pagamentos: gerencia formas de pagamento, valores, status

2. **Flexibilidade**
   - Você pode criar o pedido primeiro
   - Adicionar itens aos poucos
   - Criar pagamento depois (quando cliente decidir pagar)

3. **Múltiplas Formas de Pagamento**
   - Hoje: apenas Boleto
   - Futuro: Cartão de Crédito, PIX, etc.
   - Cada um com suas regras e validações

4. **Casos de Uso Reais**
   - Cliente monta carrinho (pedido + itens)
   - Vai para checkout (cria pagamento)
   - Finaliza compra (processa tudo)

---

## 📋 RELACIONAMENTO ENTRE MENUS

```
MENU PRINCIPAL
│
├── 3. Gerenciar Clientes
│   └── Cadastra clientes (com CPF agora!)
│
├── 4. Gerenciar Pedidos
│   ├── 1. Criar Pedido (precisa de Cliente)
│   ├── 2. Listar Pedidos
│   ├── 3. Ver Detalhes do Pedido
│   ├── 4. Gerenciar Itens do Pedido ⭐
│   │   ├── Adicionar item (valida estoque)
│   │   ├── Editar quantidade (valida estoque)
│   │   ├── Remover item (devolve estoque)
│   │   └── Listar itens
│   ├── 5. Atualizar Status
│   ├── 6. Finalizar Pedido ⭐ (processa pagamento)
│   ├── 7. Cancelar Pedido
│   └── 8. Remover Pedido
│
├── 5. Gerenciar Pagamentos ⚠️ MENU SEPARADO!
│   ├── 1. Criar Pagamento/Boleto ⭐ (associa ao pedido)
│   ├── 2. Listar Pagamentos
│   ├── 3. Ver Detalhes do Pagamento
│   ├── 4. Atualizar Status do Pagamento
│   └── 5. Remover Pagamento
│
└── 6. Gerenciar Produtos
    ├── Produtos Digitais
    └── Produtos Físicos (com controle de estoque)
```

---

## ⚡ VALIDAÇÕES CRÍTICAS

### No Menu de Pedidos:

#### Ao Adicionar Item:
```java
1. Produto existe?
2. É produto físico?
   └─→ Tem estoque suficiente?
3. Quantidade > 0?
4. Diminui estoque
5. Adiciona item ao pedido
6. Atualiza total
```

#### Ao Finalizar Pedido:
```java
1. Pedido tem pagamento associado?
   └─→ NÃO: ❌ Erro: "Nenhum pagamento associado"
2. Pedido tem pelo menos 1 item?
   └─→ NÃO: ❌ Erro: "Pedido sem itens"
3. Processa pagamento
   └─→ pagamento.processarPagamento()
4. Atualiza status baseado no resultado
```

### No Menu de Pagamentos:

#### Ao Criar Pagamento:
```java
1. Pedido existe?
2. Pedido já tem pagamento?
   └─→ SIM: ⚠️ Aviso ou erro
3. Cria Boleto com valor do pedido
4. Associa Boleto ao Pedido
5. Salva no banco
```

---

## 💡 EXEMPLO PRÁTICO DE USO

### Caso 1: Fluxo Normal ✅

```
1. Cadastrar Cliente
   └─→ Cliente #1 (João, CPF: 123.456.789-00)

2. Criar Pedido
   └─→ Pedido #1 (Cliente: João, Status: PENDENTE)

3. Adicionar Itens
   ├─→ Mouse x2 (R$ 150 cada) = R$ 300
   └─→ Teclado x1 (R$ 450) = R$ 450
   Total: R$ 750

4. Criar Pagamento (OUTRO MENU!)
   └─→ Boleto #1 (Valor: R$ 750, associado ao Pedido #1)

5. Finalizar Pedido
   └─→ ✅ Pedido finalizado! Status: FINALIZADO
```

### Caso 2: Tentativa de Finalizar sem Pagamento ❌

```
1. Criar Pedido #2
2. Adicionar Itens (R$ 500)
3. Tentar Finalizar Pedido
   └─→ ❌ ERRO: "Nenhum pagamento associado ao pedido!"
   
Solução: Ir ao Menu de Pagamentos e criar pagamento primeiro!
```

### Caso 3: Tentativa de Finalizar sem Itens ❌

```
1. Criar Pedido #3
2. Criar Pagamento (R$ 0,00 pois não tem itens)
3. Tentar Finalizar Pedido
   └─→ ❌ ERRO: "Pedido sem itens!"
   
Solução: Adicionar itens ao pedido primeiro!
```

### Caso 4: Estoque Insuficiente ❌

```
1. Criar Pedido #4
2. Adicionar Item: Mouse x10
   └─→ Estoque disponível: 5 unidades
   └─→ ❌ ERRO: "ESTOQUE INSUFICIENTE!"
       Disponível: 5 unidades
       Solicitado: 10 unidades
```

---

## 🎓 RESUMO PARA O USUÁRIO

### O que você precisa saber:

1. **Pedido e Pagamento são SEPARADOS mas CONECTADOS**
   - Você cria o pedido primeiro
   - Adiciona itens ao pedido
   - Depois cria o pagamento (em outro menu)
   - Por fim, finaliza o pedido (que processa o pagamento)

2. **Ordem correta:**
   ```
   Cliente → Pedido → Itens → Pagamento → Finalização
   ```

3. **Você não paga no menu de pedidos:**
   - No menu de pedidos você cria e gerencia o pedido
   - No menu de pagamentos você cria o método de pagamento
   - A finalização do pedido é que processa o pagamento

4. **Pagamento é associado ao pedido:**
   - Quando você cria um pagamento, informa o número do pedido
   - Sistema pega o valor total do pedido automaticamente
   - Boleto fica associado ao pedido

5. **Finalização processa tudo:**
   - Valida se tem pagamento
   - Valida se tem itens
   - Processa o pagamento
   - Atualiza status do pedido
   - Pronto! Venda concluída

---

## 🔐 ADIÇÃO DO CPF

### Mudanças implementadas:

1. **Cliente.java:**
   - Campo `cpf` (String, único, 14 caracteres)
   - Método `validarFormatoCPF(cpf)`
   - Método `formatarCPF(cpf)`

2. **Menu Clientes:**
   - Cadastro solicita CPF
   - Validação automática
   - Formatação automática (XXX.XXX.XXX-XX)
   - Listagem mostra CPF
   - Edição permite alterar CPF

3. **Validações:**
   - ✅ CPF deve ter 11 dígitos
   - ✅ CPF não pode ter todos dígitos iguais
   - ✅ CPF é único no sistema
   - ✅ Formatação automática

---

## 📊 DIAGRAMA DE CLASSES RELACIONADAS

```
Cliente (com CPF agora!)
   ↓ 1:N
Pedido
   ↓ 1:N           ↓ 1:1
ItemPedido    Pagamento
   ↓ N:1             ↓
Produto          Boleto
   ↓
ProdutoFisico (estoque)
ProdutoDigital
```

---

**Data:** 23/10/2025  
**Sistema:** E-commerce Maven com JPA/Hibernate  
**Status:** Completo e Documentado
