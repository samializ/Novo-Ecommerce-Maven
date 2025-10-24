# 🧪 GUIA COMPLETO DE TESTES DO SISTEMA E-COMMERCE

## ⚠️ ANTES DE TESTAR - RESOLVER ERRO DO CPF

### 🔴 PROBLEMA ATUAL
```
ERROR: column "cpf" of relation "cliente" does not exist
```

**Causa:** Você adicionou o campo CPF no código Java, mas o banco de dados PostgreSQL ainda não tem essa coluna.

---

### ✅ SOLUÇÃO APLICADA

O arquivo `persistence.xml` foi alterado para:
```xml
<property name="hibernate.hbm2ddl.auto" value="create"/>
```

**O que isso faz:**
- ❌ **APAGA** todas as tabelas existentes
- ✅ **RECRIA** todas as tabelas com a nova estrutura (incluindo CPF)
- ✅ Banco fica limpo para testar do zero

---

### 📋 PASSO 1: RECRIAR O BANCO

1. **Execute o sistema UMA VEZ:**
   ```
   Run → Run: Main
   ```

2. **Aguarde a mensagem:**
   ```
   === SISTEMA E-COMMERCE ===
   ```

3. **Feche o sistema** (opção 0)

4. **O banco foi recriado!** Agora volte ao `persistence.xml` e mude para:
   ```xml
   <property name="hibernate.hbm2ddl.auto" value="update"/>
   ```

**Por quê mudar de volta para `update`?**
- `create`: Apaga tudo sempre que rodar (perde dados)
- `update`: Mantém dados e só adiciona colunas novas

---

## 🧪 ROTEIRO COMPLETO DE TESTES

### 📊 Ordem dos Testes

```
1. Produtos (Físico e Digital)
   └─→ Precisa ter produtos para vender

2. Clientes
   └─→ Precisa ter clientes para fazer pedidos

3. Pedidos
   └─→ Cria pedido e adiciona itens

4. Pagamentos
   └─→ Cria boleto para o pedido

5. Finalização
   └─→ Finaliza o pedido completo
```

---

## 🎯 TESTE 1: CADASTRAR PRODUTOS

### 1.1 Cadastrar Produto Físico ✅

**Menu:** `6. Produtos → 1. Físico → 1. Cadastrar`

**Dados de Teste:**
```
Nome: Mouse Gamer RGB
Preço: 150.50
Descrição: Mouse gamer com RGB 16 milhões de cores
Peso (kg): 0.150
Estoque inicial: 20
```

**Resultado Esperado:**
```
✅ Produto físico cadastrado com sucesso!
📦 Estoque inicial: 20 unidades
```

**✅ O que validar:**
- Nome não vazio
- Preço > 0
- Peso > 0
- Estoque >= 0

---

### 1.2 Cadastrar Produto Digital ✅

**Menu:** `6. Produtos → 2. Digital → 1. Cadastrar`

**Dados de Teste:**
```
Nome: Curso de Java Completo
Preço: 297.00
Descrição: Curso completo de Java do zero ao avançado
URL Download: https://exemplo.com/curso-java.zip
```

**Resultado Esperado:**
```
✅ Produto digital cadastrado com sucesso!
```

**✅ O que validar:**
- Nome não vazio
- Preço > 0
- URL começa com http:// ou https://

---

### 1.3 Listar Produtos ✅

**Menu:** `6. Produtos → 1. Físico → 2. Listar`

**Resultado Esperado:**
```
-- Lista de Produtos Físicos --
ID: 1 | Nome: Mouse Gamer RGB | Preço: 150.5 | Peso: 0.15 | Estoque: 20
```

---

### 1.4 Teste de Validações ❌

**Tente cadastrar produto com:**
- Preço = 0 → ❌ Deve dar erro
- Peso = -5 → ❌ Deve dar erro
- Estoque = -10 → ❌ Deve dar erro
- Nome vazio → ❌ Deve dar erro

---

## 👤 TESTE 2: CADASTRAR CLIENTES

### 2.1 Cadastrar Cliente Válido ✅

**Menu:** `3. Gerenciar Clientes → 1. Cadastrar Cliente`

**Dados de Teste:**
```
Nome completo: João Silva Santos
CPF: 08692944319
Email: joao.silva@gmail.com
Senha: senha123
Endereço completo: Rua das Flores, 123, Centro, São Paulo - SP
```

**Resultado Esperado:**
```
✅ Cliente cadastrado com sucesso!
   ID: 1
   CPF: 086.929.443-19
```

**✅ O que validar:**
- CPF formatado automaticamente
- Email em formato válido
- Nome >= 3 caracteres
- Senha >= 6 caracteres
- Endereço >= 10 caracteres

---

### 2.2 Cadastrar Segundo Cliente ✅

**Dados de Teste:**
```
Nome: Maria Oliveira
CPF: 12345678900
Email: maria.oliveira@gmail.com
Senha: 123456
Endereço: Av. Paulista, 1000, Bela Vista, São Paulo - SP
```

---

### 2.3 Listar Clientes ✅

**Menu:** `3. Gerenciar Clientes → 2. Listar Todos os Clientes`

**Resultado Esperado:**
```
--- LISTA DE CLIENTES ---
ID    | CPF                | Nome                     | Email                         | Endereço                      | Pedidos
1     | 086.929.443-19     | João Silva Santos        | joao.silva@gmail.com          | Rua das Flores, 123...        | 0
2     | 123.456.789-00     | Maria Oliveira           | maria.oliveira@gmail.com      | Av. Paulista, 1000...         | 0
```

---

### 2.4 Teste de Validações ❌

**Tente cadastrar com:**
- CPF duplicado → ❌ "Este CPF já está cadastrado"
- CPF 111.111.111-11 → ❌ "CPF inválido"
- CPF com 10 dígitos → ❌ "CPF inválido"
- Email sem @ → ❌ "Email inválido"
- Senha com 5 caracteres → ❌ "Mínimo 6 caracteres"
- Nome com 2 letras → ❌ "Mínimo 3 caracteres"
- Endereço com 5 letras → ❌ "Mínimo 10 caracteres"

---

## 🛒 TESTE 3: CRIAR E GERENCIAR PEDIDOS

### 3.1 Criar Pedido ✅

**Menu:** `4. Gerenciar Pedidos → 1. Criar Pedido`

**Dados de Teste:**
```
ID do cliente: 1
```

**Resultado Esperado:**
```
✅ Pedido criado com sucesso! ID: 1
```

**Anote o número do pedido:** `1`

---

### 3.2 Adicionar Itens ao Pedido ✅

**Menu:** `4. Gerenciar Pedidos → 4. Gerenciar Itens do Pedido`

**Passo a Passo:**

**1. Informe o pedido:**
```
Número do pedido: 1
```

**2. Adicione primeiro item:**
```
Escolha: 1 (Adicionar Item)
ID do produto: 1
Quantidade: 2
```

**Resultado Esperado:**
```
Produto: Mouse Gamer RGB - R$ 150.50
📦 Estoque disponível: 20 unidades
Quantidade: 2

✅ Item adicionado ao pedido!
📦 Estoque atualizado: 18 unidades restantes
💰 Subtotal do item: R$ 301.00
```

**3. Adicione segundo item:**
```
Escolha: 1 (Adicionar Item)
ID do produto: 2
Quantidade: 1
```

**4. Liste os itens:**
```
Escolha: 2 (Listar Itens)
```

**Resultado Esperado:**
```
--- ITENS DO PEDIDO ---
ID    | Produto                        | Qtd        | Preço Unit.  | Subtotal
1     | Mouse Gamer RGB                | 2          | R$ 150.50    | R$ 301.00
2     | Curso de Java Completo         | 1          | R$ 297.00    | R$ 297.00
─────────────────────────────────────────────────────────────────────────────
💰 TOTAL: R$ 598.00
```

**5. Volte ao menu:**
```
Escolha: 0 (Voltar)
```

---

### 3.3 Ver Detalhes do Pedido ✅

**Menu:** `4. Gerenciar Pedidos → 3. Ver Detalhes do Pedido`

**Dados:**
```
Número do pedido: 1
```

**Resultado Esperado:**
```
=== DETALHES DO PEDIDO #1 ===
📋 Status: PENDENTE
👤 Cliente: João Silva Santos (ID: 1)
📇 CPF: 086.929.443-19
📅 Data: 2025-10-23...
📦 Total de itens: 2
💰 Valor total: R$ 598.00

--- ITENS ---
(lista dos 2 itens)
```

---

### 3.4 Teste de Validações de Estoque ❌

**Tente adicionar item com quantidade maior que estoque:**

```
ID do produto: 1 (Mouse - estoque atual: 18)
Quantidade: 25
```

**Resultado Esperado:**
```
❌ ESTOQUE INSUFICIENTE!
   Disponível: 18 unidades
   Solicitado: 25 unidades
```

---

### 3.5 Editar Quantidade de Item ✅

**Menu:** `Gerenciar Itens do Pedido → 3. Editar Quantidade`

**Dados:**
```
ID do item: 1
Nova quantidade: 5
```

**Resultado Esperado:**
```
Produto: Mouse Gamer RGB
Quantidade atual: 2
📦 Estoque disponível: 20 unidades (incluindo quantidade atual)

Nova quantidade: 5

✅ Quantidade atualizada!
📦 Estoque atualizado: 15 unidades
💰 Novo subtotal: R$ 752.50
```

**Cálculo:**
- Estoque antes: 18
- Diferença: 5 - 2 = 3 (precisa de 3 a mais)
- Estoque depois: 18 - 3 = 15 ✅

---

### 3.6 Remover Item do Pedido ✅

**Menu:** `Gerenciar Itens do Pedido → 4. Remover Item`

**Dados:**
```
ID do item: 2
Confirma? S
```

**Resultado Esperado:**
```
Produto: Curso de Java Completo
Quantidade: 1 unidades
⚠️ Confirma a remoção? (S/N): S

(Produto digital não tem estoque, então não devolve)
✅ Item removido!
```

---

## 💳 TESTE 4: CRIAR BOLETO PARA PAGAMENTO

### 4.1 Criar Boleto ✅

**⚠️ IMPORTANTE:** Agora você muda de menu!

**Menu:** `5. Gerenciar Pagamentos → 1. Criar Boleto para Pedido`

**Dados:**
```
📋 Número do Pedido: 1
```

**Sistema mostra resumo:**
```
--- RESUMO DO PEDIDO ---
📋 Número: #1
👤 Cliente: João Silva Santos
📇 CPF: 086.929.443-19
📦 Total de itens: 1 (removemos um)
💰 Valor total: R$ 752.50
📅 Data: 2025-10-23...
📊 Status: PENDENTE
```

**Sistema gera boleto:**
```
--- GERANDO BOLETO ---
🔢 Código: 00001.17305 00001.000075 00000.000001 1 00000000075250
📅 Vencimento: 30/10/2025
💵 Valor: R$ 752.50

✅ Confirma criação do boleto? (S/N): S
```

**Resultado Esperado:**
```
✅ BOLETO CRIADO COM SUCESSO!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 PEDIDO: #1
👤 CLIENTE: João Silva Santos
📇 CPF: 086.929.443-19
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔢 CÓDIGO DO BOLETO:
   00001.17305 00001.000075 00000.000001 1 00000000075250
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📅 VENCIMENTO: 30/10/2025
💵 VALOR: R$ 752.50
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ IMPORTANTE:
   - Boleto associado ao Pedido #1
   - Para finalizar a compra, vá em:
     Menu Pedidos → Finalizar Pedido
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**✅ O que foi feito automaticamente:**
- Código do boleto gerado
- Vencimento calculado (7 dias)
- Valor pegado do pedido
- Boleto associado ao pedido

---

### 4.2 Listar Boletos ✅

**Menu:** `5. Gerenciar Pagamentos → 2. Listar Todos os Boletos`

**Resultado Esperado:**
```
--- LISTA DE BOLETOS ---
ID    | Código do Boleto                                   | Vencimento   | Tipo
1     | 00001.17305 00001.000075 00000.000001 1 000000...  | 30/10/2025   | Boleto
─────────────────────────────────────────────────────────────────────────────
📊 Total de boletos: 1
```

---

### 4.3 Ver Detalhes do Boleto ✅

**Menu:** `5. Gerenciar Pagamentos → 3. Ver Detalhes do Boleto`

**Dados:**
```
📋 ID do boleto: 1
```

**Resultado Esperado:**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
           DETALHES DO BOLETO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🆔 ID: 1
🔢 Código: 00001.17305...
📅 Vencimento: 30/10/2025

--- PEDIDO ASSOCIADO ---
📋 Número: #1
👤 Cliente: João Silva Santos
📇 CPF: 086.929.443-19
💰 Valor: R$ 752.50
📊 Status: PENDENTE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

### 4.4 Teste de Validações ❌

**Tente criar boleto para pedido inexistente:**
```
Número do Pedido: 999
```
**Resultado:** ❌ "Pedido #999 não encontrado!"

---

**Tente criar segundo boleto para mesmo pedido:**
```
Número do Pedido: 1
```
**Resultado:** ⚠️ "Este pedido já possui um pagamento associado!"

---

## ✅ TESTE 5: FINALIZAR PEDIDO

### 5.1 Finalizar Pedido com Sucesso ✅

**⚠️ IMPORTANTE:** Volte ao menu de pedidos!

**Menu:** `4. Gerenciar Pedidos → 6. Finalizar Pedido`

**Dados:**
```
📋 Número do pedido para finalizar: 1
```

**Sistema mostra resumo:**
```
=== FINALIZANDO PEDIDO #1 ===
Cliente: João Silva Santos
Total de itens: 1
Valor total: R$ 752.50

⚠️ Confirma a finalização? (S/N): S
```

**Resultado Esperado:**
```
✅ Boleto emitido: 00001.17305 00001.000075...
📅 Vencimento: 30/10/2025
✅ Pedido finalizado com sucesso!
📦 Número do pedido: 1
💰 Valor total: R$ 752.50
```

**✅ O que aconteceu:**
1. Sistema validou: tem boleto? ✅
2. Sistema validou: tem itens? ✅
3. Sistema processou boleto
4. Status mudou para: **FINALIZADO**

---

### 5.2 Verificar Status Atualizado ✅

**Menu:** `4. Gerenciar Pedidos → 3. Ver Detalhes do Pedido`

**Dados:**
```
Número: 1
```

**Resultado Esperado:**
```
📊 Status: FINALIZADO ✅
```

---

### 5.3 Teste de Validações ❌

**Crie um novo pedido SEM boleto:**
```
1. Criar Pedido (cliente 1)
2. Adicionar itens
3. Tentar Finalizar
```

**Resultado:** ❌ "Nenhum pagamento associado ao pedido!"

---

**Crie pedido SEM itens:**
```
1. Criar Pedido
2. NÃO adicionar itens
3. Criar boleto
4. Tentar Finalizar
```

**Resultado:** ❌ "Pedido sem itens!"

---

## 📊 TESTE 6: HISTÓRICO DO CLIENTE

### 6.1 Ver Histórico de Pedidos ✅

**Menu:** `3. Gerenciar Clientes → 5. Ver Histórico de Pedidos`

**Dados:**
```
ID do cliente: 1
```

**Resultado Esperado:**
```
=== HISTÓRICO DE PEDIDOS ===
Cliente: João Silva Santos
CPF: 086.929.443-19
Total de pedidos: 1

Pedido #1
├── Data: 2025-10-23...
├── Status: FINALIZADO
├── Itens: 1
└── Total: R$ 752.50
```

---

## 🔄 TESTE 7: FLUXO COMPLETO DO ZERO

Execute tudo em sequência:

```
✅ 1. Cadastrar Cliente
✅ 2. Cadastrar 2 Produtos Físicos
✅ 3. Criar Pedido
✅ 4. Adicionar 2 Itens
✅ 5. Criar Boleto
✅ 6. Finalizar Pedido
✅ 7. Verificar Histórico
```

**Tempo estimado:** 5-10 minutos

---

## 🚨 TESTE 8: CASOS DE ERRO

### 8.1 Estoque Zerado ❌

```
1. Produto com estoque 5
2. Adicionar 5 ao pedido
3. Tentar adicionar mais → ❌ "Produto FORA DE ESTOQUE"
```

---

### 8.2 CPF Duplicado ❌

```
1. Cadastrar cliente com CPF 123.456.789-00
2. Cadastrar outro com mesmo CPF → ❌ "CPF já cadastrado"
```

---

### 8.3 Email Inválido ❌

```
Tentar cadastrar com:
- email.com → ❌
- @gmail.com → ❌
- teste@ → ❌
```

---

## 📋 CHECKLIST COMPLETO DE TESTES

### ✅ Produtos
- [ ] Cadastrar Produto Físico
- [ ] Cadastrar Produto Digital
- [ ] Listar Produtos
- [ ] Editar Produto
- [ ] Validar: preço > 0
- [ ] Validar: estoque >= 0
- [ ] Validar: peso > 0
- [ ] Validar: URL válida (digital)

### ✅ Clientes
- [ ] Cadastrar Cliente
- [ ] Listar Clientes
- [ ] Editar Cliente
- [ ] Ver Histórico
- [ ] Validar: CPF único
- [ ] Validar: CPF formato correto
- [ ] Validar: Email formato correto
- [ ] Validar: Senha >= 6 caracteres

### ✅ Pedidos
- [ ] Criar Pedido
- [ ] Adicionar Item
- [ ] Listar Itens
- [ ] Editar Quantidade
- [ ] Remover Item
- [ ] Ver Detalhes
- [ ] Validar: Estoque suficiente
- [ ] Validar: Devolução de estoque ao remover
- [ ] Validar: Ajuste de estoque ao editar

### ✅ Pagamentos
- [ ] Criar Boleto
- [ ] Listar Boletos
- [ ] Ver Detalhes Boleto
- [ ] Validar: Código gerado automaticamente
- [ ] Validar: Valor do pedido
- [ ] Validar: Associação ao pedido

### ✅ Finalização
- [ ] Finalizar com sucesso
- [ ] Validar: Precisa ter boleto
- [ ] Validar: Precisa ter itens
- [ ] Verificar status atualizado
- [ ] Verificar no histórico

---

## 🎯 RESUMO DOS PASSOS

### Para Testar o Sistema Completo:

1. **Execute UMA VEZ** com `create` no persistence.xml
2. **Feche** o sistema
3. **Mude para `update`** no persistence.xml
4. **Execute novamente**
5. **Siga o roteiro** acima

### Ordem dos Testes:
```
Produtos → Clientes → Pedidos → Boletos → Finalização
```

### Tempo Total Estimado:
```
Teste Completo: 15-20 minutos
Teste Rápido: 5-7 minutos
```

---

## 💡 DICAS DE TESTE

1. **Sempre teste validações:** Tente cadastrar dados inválidos
2. **Anote IDs:** Anote IDs de clientes e produtos
3. **Teste estoque:** Adicione, edite e remova itens
4. **Teste fluxo completo:** Do cadastro à finalização
5. **Verifique mensagens:** Todas devem ser claras

---

## 🔍 COMO IDENTIFICAR ERROS

### Se der erro ao cadastrar:
1. Verifique as validações
2. Veja se campos estão vazios
3. Confira formato (CPF, Email)

### Se der erro ao listar:
1. Banco pode não ter dados
2. Conexão com banco pode estar fora

### Se der erro ao finalizar:
1. Tem boleto? Se não, crie no Menu Pagamentos
2. Tem itens? Se não, adicione itens

---

**Data:** 23/10/2025  
**Status:** ✅ Pronto para testar  
**Próximo passo:** Execute com `create`, depois mude para `update`
