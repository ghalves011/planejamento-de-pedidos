# 📦 Sistema de Planejamento de Pedidos

## 📌 Descrição

Aplicação desktop desenvolvida em Java com Swing para gerenciamento completo de pedidos de produção e compra.

O sistema permite realizar operações CRUD, aplicar filtros, visualizar status automaticamente e manipular dados diretamente pela interface.

---

## 🚀 Funcionalidades

### 📥 Cadastro

* Cadastro de novos pedidos com:

  * Pedido de produção
  * Pedido de compra
  * Empresa
  * Datas (início, prazo, entrega)
  * Quantidade de itens
  * Observações

### 📋 Listagem e Edição

* Exibição em tabela interativa
* Edição direta dos dados (inline editing)
* Atualização automática no banco de dados

### 🎨 Destaque Visual Inteligente

* 🟢 Verde → Pedido **PRONTO**
* 🔵 Azul → Pedido **ENTREGUE**
* 🔴 Vermelho → Pedido com **DATA DE INÍCIO ULTRAPASSADA**
* 🟡 Amarelo → Pedido **PRÓXIMO DO PRAZO DE INÍCIO**

### 🔎 Filtros

* Filtro por:

  * Empresa
  * Pedido de produção/compra
  * Status (PRONTO, EM ANDAMENTO, ENTREGUE)
* Busca em tempo real

### ⚙️ Operações

* ✏️ Atualização automática ao editar
* ❌ Exclusão de pedidos
* 🖨️ Impressão da tabela
* 📤 Exportação para CSV
* 📥 Importação de dados via Excel

---

## 🛠 Tecnologias Utilizadas

* Java
* Swing (GUI)
* JDBC
* MySQL

---

## 🗄️ Banco de Dados

Banco utilizado:

```sql
PlanejamentoDePedidos
```

### Tabela principal:

```sql
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_de_producao VARCHAR(100),
    pedido_de_compra VARCHAR(100),
    empresa VARCHAR(100),
    previsao_de_inicio DATE,
    prazo DATE,
    prontos INT,
    total_de_itens INT,
    observacoes TEXT,
    data_de_entrega DATE,
    status VARCHAR(50)
);
```

---

## ⚙️ Configuração

Configure a conexão no arquivo `PedidoDAO.java`:

```java
String url = "jdbc:mysql://localhost:3306/PlanejamentoDePedidos";
String user = "root";
String password = "SUA_SENHA";
```

---

## ▶️ Como Executar

### 1. Compilar

```bash
javac -d bin src/*.java
```

### 2. Gerar JAR

```bash
jar cfe PlanejamentoDePedidos.jar TelaPrincipal -C bin .
```

### 3. Executar

```bash
java -jar PlanejamentoDePedidos.jar
```

---

## 🧠 Regras de Negócio

O status do pedido é calculado automaticamente:

* **ENTREGUE** → quando há data de entrega
* **EM ANDAMENTO** → quando itens prontos < total
* **PRONTO** → quando todos os itens estão concluídos

---

## 📁 Estrutura do Projeto

```bash
src/
├── TelaPrincipal.java
├── TelaCadastroPedidos.java
├── TelaListagemPedidos.java
├── Pedido.java
├── PedidoDAO.java
└── ImportPedidos.java
```

---

## 🎯 Objetivo

Projeto acadêmico com foco em:

* Programação Orientada a Objetos
* Integração com banco de dados (JDBC)
* Interfaces gráficas com Swing
* Manipulação de dados em tempo real

---

## 🔮 Melhorias Futuras

* Sistema de login e autenticação
* Dashboard com gráficos
* Geração de relatórios PDF
* Integração com API
* Melhorias de UI/UX

---

## 👨‍💻 Autor

Guilherme Henrique Alves