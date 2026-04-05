# Sistema de Vendas e Estoque - Persistência com ORM (Hibernate)

**Pontifícia Universidade Católica de Minas Gerais (PUC Minas)** **Curso:** Ciência da Computação  
**Disciplina:** Engenharia de Software 2  
**Autores:** João Pedro Torres e Lucas Carneiro Nassau Malta

## 📌 Sobre o Projeto
Este projeto é uma aplicação Java de linha de comando (CLI) desenvolvida para demonstrar a aplicação prática de **Mapeamento Objeto-Relacional (MOR/ORM)** utilizando o framework **Hibernate**. 

O sistema implementa o estudo de caso de um gerenciador de vendas e estoque. O modelo de domínio abrange conceitos fundamentais de orientação a objetos mapeados para um banco de dados relacional, incluindo:
* **Herança (Estratégia JOINED):** Especialização da classe `Produto` para `ProdutoEletronico` e `ProdutoPerecivel`.
* **Relacionamentos (1..N e N..1):** Associação entre `Pedido` e `Item`, e entre `Item` e `Produto`.
* **Transações e Cascade:** Atualização automática de tabelas dependentes e controle de rollback em caso de falha.

---

## 🛠️ Tecnologias Utilizadas
* **Linguagem:** Java 17 (ou superior)
* **Gerenciador de Dependências:** Maven
* **ORM:** Hibernate Core (6.x)
* **Banco de Dados:** MySQL Server (8.x)
* **Utilitários:** Lombok (Geração automática de Getters/Setters)

---

## ⚙️ Configuração do Ambiente

Para executar o projeto localmente, é necessário configurar o banco de dados MySQL para que o Hibernate consiga estabelecer a conexão e gerar as tabelas automaticamente.

### 1. Criando o Banco e o Usuário no MySQL
Acesse o prompt do MySQL (`mysql -u root -p` ou `sudo mysql` em distribuições Linux) e execute os seguintes comandos:

```sql
-- Cria o banco de dados do projeto
CREATE DATABASE sistema_vendas;

-- Cria o usuário dedicado para a aplicação
CREATE USER 'aluno'@'localhost' IDENTIFIED BY 'pucminas';

-- Concede as permissões necessárias
GRANT ALL PRIVILEGES ON sistema_vendas.* TO 'aluno'@'localhost';
FLUSH PRIVILEGES;
```
**Nota para usuários Linux (Ubuntu/Debian):** Caso ocorra erro de `Access Denied` ao tentar usar o root, lembre-se de que o MySQL pode exigir a execução inicial via sudo mysql sem senha, devido ao plugin auth_socket padrão do sistema.

---

## 🚀 Como Executar

### Opção 1: Via IDE (VS Code, IntelliJ, Eclipse)

1. Abra a pasta raiz do projeto na sua IDE.
2. Aguarde o Maven sincronizar e baixar as dependências (pom.xml).
3. Navegue até o arquivo src/main/java/com/puc/Main.java.
4. Execute o método main. O console interativo será aberto no terminal da IDE.

### Opção 2: Via Terminal (Maven)

Abra o terminal na raiz do projeto (onde está o pom.xml) e execute:

```bash
# Limpa builds antigos e compila o projeto
mvn clean compile

# Executa a classe principal
mvn exec:java -Dexec.mainClass="com.puc.Main"
```

---

## 🖥️ Funcionalidades do Sistema

Ao rodar a aplicação, um menu interativo será exibido no console:

1. Cadastrar Produto Eletrônico: Registra um produto com atributos gerais + voltagem.
2. Cadastrar Produto Perecível: Registra um produto com atributos gerais + data de validade.
3. Listar Todos os Produtos: Exibe o catálogo completo, demonstrando o funcionamento do polimorfismo nas consultas do Hibernate.
4. Criar Novo Pedido: Abre um fluxo de "carrinho de compras", onde é possível adicionar múltiplos itens. Ao finalizar, o estoque dos produtos é atualizado automaticamente.
5. Listar Pedidos Realizados: Exibe o histórico de vendas, detalhando os itens, quantidades e o valor total de cada pedido.
6. Remover Produto: Exclui um produto do sistema pelo ID, validando se o item não possui vínculos com pedidos existentes para garantir a integridade dos dados.
7. Remover Pedido: Cancela uma venda realizada, removendo o registro do pedido e seus itens, além de restaurar automaticamente as quantidades vendidas ao estoque.
