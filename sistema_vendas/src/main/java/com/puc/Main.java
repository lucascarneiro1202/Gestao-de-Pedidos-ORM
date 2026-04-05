package com.puc;

import com.puc.model.Item;
import com.puc.model.Pedido;
import com.puc.model.Produto;
import com.puc.model.ProdutoEletronico;
import com.puc.model.ProdutoPerecivel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static SessionFactory sessionFactory;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Iniciando o sistema e conectando ao banco de dados...");
        sessionFactory = new Configuration().configure().buildSessionFactory();

        boolean rodando = true;

        while (rodando) {
            System.out.println("\n=== SISTEMA DE VENDAS E ESTOQUE ===");
            System.out.println("1. Cadastrar Produto Eletrônico");
            System.out.println("2. Cadastrar Produto Perecível");
            System.out.println("3. Alterar Produto");
            System.out.println("4. Listar Todos os Produtos");
            System.out.println("5. Remover Produto");
            System.out.println("6. Criar Novo Pedido");
            System.out.println("7. Listar Pedidos Realizados");
            System.out.println("8. Remover Pedido");
            System.out.println("9. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (opcao) {
                case 1: cadastrarEletronico(); break;
                case 2: cadastrarPerecivel(); break;
                case 3: alterarProduto(); break;
                case 4: listarProdutos(); break;
                case 5: removerProduto(); break;
                case 6: criarPedido(); break;
                case 7: listarPedidos(); break;
                case 8: removerPedido(); break;
                case 9:
                    System.out.println("Encerrando o sistema...");
                    rodando = false;
                    break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        }

        scanner.close();
        sessionFactory.close();
    }

    // ==========================================
    // MÉTODOS DE PRODUTO (Já estavam funcionando)
    // ==========================================

    private static void cadastrarEletronico() {
        System.out.println("\n-- Cadastrando Produto Eletrônico --");
        ProdutoEletronico pe = new ProdutoEletronico();
        
        System.out.print("Nome do produto: ");
        pe.setNome(scanner.nextLine());
        System.out.print("Preço: ");
        pe.setPreco(scanner.nextDouble());
        System.out.print("Quantidade em Estoque: ");
        pe.setEstoque(scanner.nextInt());
        scanner.nextLine();
        System.out.print("Voltagem (ex: 110v, 220v, Bivolt): ");
        pe.setVoltagem(scanner.nextLine());

        salvarNoBanco(pe);
    }

    private static void cadastrarPerecivel() {
        System.out.println("\n-- Cadastrando Produto Perecível --");
        ProdutoPerecivel pp = new ProdutoPerecivel();
        
        System.out.print("Nome do produto: ");
        pp.setNome(scanner.nextLine());
        System.out.print("Preço: ");
        pp.setPreco(scanner.nextDouble());
        System.out.print("Quantidade em Estoque: ");
        pp.setEstoque(scanner.nextInt());
        scanner.nextLine();
        System.out.print("Data de Validade (dd/MM/yyyy): ");
        String dataString = scanner.nextLine();
        
        try {
            Date dataValidade = new SimpleDateFormat("dd/MM/yyyy").parse(dataString);
            pp.setDataValidade(dataValidade);
            salvarNoBanco(pp);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido! Cadastro cancelado.");
        }
    }

    private static void salvarNoBanco(Object obj) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(obj);
            transaction.commit();
            System.out.println("✅ Operação realizada com sucesso!");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("❌ Erro ao salvar: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    private static void listarProdutos() {
        System.out.println("\n-- Lista de Produtos no Banco --");
        Session session = sessionFactory.openSession();
        try {
            List<Produto> produtos = session.createQuery("from Produto", Produto.class).list();
            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado ainda.");
                return;
            }
            for (Produto p : produtos) {
                System.out.printf("ID: %d | %s | Preço: R$%.2f | Estoque: %d\n", 
                                  p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
            }
        } finally {
            session.close();
        }
    }

    private static void alterarProduto() {
        System.out.println("\n-- Alterando Produto --");
        listarProdutos();
        System.out.print("Digite o ID do produto que deseja alterar: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); 

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Produto produto = session.get(Produto.class, id);

            if (produto == null) {
                System.out.println("❌ Produto não encontrado!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Alterar Nome
            System.out.print("Novo nome (atual: " + produto.getNome() + ") [Enter para manter]: ");
            String novoNome = scanner.nextLine();
            if (!novoNome.isEmpty()) produto.setNome(novoNome);
            
            // Alterar Preço
            String precoAtualFormatado = String.valueOf(produto.getPreco()).replace(".", ",");
            System.out.print("Novo preço (atual: " + precoAtualFormatado + ") [Enter para manter]: ");
            String novoPrecoStr = scanner.nextLine().replace(",", ".");
            if (!novoPrecoStr.isEmpty()) produto.setPreco(Double.parseDouble(novoPrecoStr));
            
            // Alterar Estoque
            System.out.print("Novo estoque (atual: " + produto.getEstoque() + ") [Enter para manter]: ");
            String novoEstoqueStr = scanner.nextLine();
            if (!novoEstoqueStr.isEmpty()) produto.setEstoque(Integer.parseInt(novoEstoqueStr));

            // Lógica específica para cada tipo
            if (produto instanceof ProdutoEletronico pe) {
                System.out.print("Nova voltagem (atual: " + pe.getVoltagem() + ") [Enter para manter]: ");
                String novaVoltagem = scanner.nextLine();
                if (!novaVoltagem.isEmpty()) pe.setVoltagem(novaVoltagem);
                
            } else if (produto instanceof ProdutoPerecivel pp) {
                String dataFormatada = sdf.format(pp.getDataValidade());
                System.out.print("Nova data (atual: " + dataFormatada + ") [Enter para manter]: ");
                String novaDataStr = scanner.nextLine();
                if (!novaDataStr.isEmpty()) pp.setDataValidade(sdf.parse(novaDataStr));
            }

            transaction.commit();
            System.out.println("✅ Produto atualizado com sucesso!");

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            // Se o erro for de formato, damos uma mensagem melhor que 'null'
            String msg = (e.getMessage() == null) ? "Formato de dado inválido!" : e.getMessage();
            System.out.println("❌ Erro ao alterar: " + msg);
        } finally {
            session.close();
        }
    }

    private static void removerProduto() {
        System.out.println("\n-- Removendo Produto --");
        listarProdutos();
        System.out.print("Digite o ID do produto que deseja excluir: ");
        Long id = scanner.nextLong();

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            // Busca o objeto no banco
            Produto produto = session.get(Produto.class, id);

            if (produto != null) {
                // Remove o objeto
                session.remove(produto);
                transaction.commit();
                System.out.println("✅ Produto removido com sucesso!");
            } else {
                System.out.println("❌ Produto não encontrado.");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            // Erro comum: tentar apagar produto que já está em um pedido (chave estrangeira)
            System.out.println("❌ Erro ao remover: Este produto pode estar vinculado a um pedido existente.");
        } finally {
            session.close();
        }
    }

    // ==========================================
    // NOVOS MÉTODOS DE PEDIDO E ITEM
    // ==========================================

    private static void criarPedido() {
        System.out.println("\n-- Criando Novo Pedido --");
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Pedido pedido = new Pedido();
            pedido.setData(new Date()); // Data atual
            pedido.setItens(new ArrayList<>());
            double valorTotalPedido = 0.0;

            boolean adicionandoItens = true;
            while (adicionandoItens) {
                listarProdutos(); // Mostra as opções para o usuário
                System.out.print("\nDigite o ID do Produto para adicionar (ou 0 para finalizar o pedido): ");
                Long idProduto = scanner.nextLong();

                if (idProduto == 0) {
                    adicionandoItens = false;
                    continue;
                }

                // Busca o produto no banco de dados
                Produto produto = session.get(Produto.class, idProduto);

                if (produto == null) {
                    System.out.println("Produto não encontrado!");
                } else if (produto.getEstoque() <= 0) {
                    System.out.println("Produto sem estoque!");
                } else {
                    System.out.print("Quantidade desejada: ");
                    int qtd = scanner.nextInt();

                    if (qtd > produto.getEstoque()) {
                        System.out.println("Estoque insuficiente! Apenas " + produto.getEstoque() + " unidades disponíveis.");
                    } else {
                        // Cria o item do pedido
                        Item item = new Item();
                        item.setProduto(produto);
                        item.setQuantidade(qtd);
                        item.setValorItem(produto.getPreco());
                        item.setPedido(pedido); // Relaciona o item com o pedido pai

                        pedido.getItens().add(item); // Adiciona na lista do pedido
                        valorTotalPedido += (produto.getPreco() * qtd);

                        // Atualiza o estoque do produto
                        produto.setEstoque(produto.getEstoque() - qtd);
                        session.merge(produto); // Atualiza o produto no banco

                        System.out.println("Item adicionado ao pedido!");
                    }
                }
            }

            if (!pedido.getItens().isEmpty()) {
                pedido.setValorTotal(valorTotalPedido);
                
                // Salva o pedido. O Hibernate vai salvar os itens automaticamente 
                // por causa do cascade = CascadeType.ALL na sua entidade Pedido!
                session.persist(pedido); 
                transaction.commit();
                System.out.printf("✅ Pedido finalizado com sucesso! Valor Total: R$%.2f\n", valorTotalPedido);
            } else {
                System.out.println("Pedido cancelado (vazio).");
                transaction.rollback();
            }

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("❌ Erro ao criar pedido: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    private static void listarPedidos() {
        System.out.println("\n-- Lista de Pedidos Realizados --");
        Session session = sessionFactory.openSession();
        try {
            List<Pedido> pedidos = session.createQuery("from Pedido", Pedido.class).list();
            
            if (pedidos.isEmpty()) {
                System.out.println("Nenhum pedido realizado ainda.");
                return;
            }

            for (Pedido p : pedidos) {
                System.out.printf("Pedido #%d | Data: %s | Valor Total: R$%.2f\n", 
                                  p.getId(), p.getData().toString(), p.getValorTotal());
                
                System.out.println("   Itens:");
                for (Item i : p.getItens()) {
                    System.out.printf("   -> %dx %s (R$%.2f cada)\n", 
                                      i.getQuantidade(), i.getProduto().getNome(), i.getValorItem());
                }
                System.out.println("-------------------------------------------------");
            }
        } finally {
            session.close();
        }
    }

    private static void removerPedido() {
        System.out.println("\n-- Removendo Pedido (Cancelamento) --");
        listarPedidos();
        System.out.print("Digite o ID do pedido que deseja excluir: ");
        Long id = scanner.nextLong();

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Pedido pedido = session.get(Pedido.class, id);

            if (pedido != null) {
                // Devolver os itens ao estoque antes de excluir o pedido
                for (Item item : pedido.getItens()) {
                    Produto p = item.getProduto();
                    p.setEstoque(p.getEstoque() + item.getQuantidade());
                    session.merge(p); // Atualiza o estoque do produto no banco
                }

                // Remove o pedido (os Itens serão removidos automaticamente pelo CascadeType.ALL)
                session.remove(pedido);
                transaction.commit();
                System.out.println("✅ Pedido removido e estoque devolvido!");
            } else {
                System.out.println("❌ Pedido não encontrado.");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("❌ Erro ao remover pedido: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}