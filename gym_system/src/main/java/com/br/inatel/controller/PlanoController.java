package com.br.inatel.controller;

import com.br.inatel.model.Plano;
import com.br.inatel.service.PlanoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PlanoController {

    private final PlanoService planoService;
    private final Scanner sc;

    public PlanoController(PlanoService planoService, Scanner sc) {
        this.planoService = planoService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU PLANO =====");
            System.out.println("1. Cadastrar novo plano");
            System.out.println("2. Atualizar plano");
            System.out.println("3. Listar todos");
            System.out.println("4. Buscar por nome");
            System.out.println("5. Buscar por faixa de preço");
            System.out.println("6. Excluir plano");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> atualizar();
                    case 3 -> listarTodos();
                    case 4 -> buscarPorNome();
                    case 5 -> buscarPorFaixaPreco();
                    case 6 -> excluir();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (BusinessException | NotFoundException e) {
                System.err.println("❌ Erro: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("⚠️ Erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        } while (opcao != 0);
    }


    private void criar() {
        System.out.println("\n--- Cadastro de Plano ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.print("Preço (ex: 129.90): ");
        BigDecimal preco = parseValor(sc.nextLine());

        System.out.print("Duração em meses (ex: 12): ");
        int meses = Integer.parseInt(sc.nextLine());

        Plano p = planoService.criar(nome, preco, meses);
        System.out.println("✅ Plano cadastrado! ID: " + p.getId());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Plano ---");
        System.out.print("ID do plano: ");
        long id = Long.parseLong(sc.nextLine());

        System.out.print("Novo nome: ");
        String nome = sc.nextLine();

        System.out.print("Novo preço (ex: 129.90): ");
        BigDecimal preco = parseValor(sc.nextLine());

        System.out.print("Nova duração em meses: ");
        int meses = Integer.parseInt(sc.nextLine());

        Plano p = planoService.atualizar(id, nome, preco, meses);
        System.out.println("✅ Plano atualizado: " + p.getNome()  + precoFmt(p.getPreco()) + " | " + mesesFmt(p.getDuracaoMeses()));
    }

    private void listarTodos() {
        System.out.println("\n--- Lista de Planos ---");
        List<Plano> lista = planoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum plano cadastrado.");
            return;
        }
        for (Plano p : lista) {
            System.out.printf("[%d] %s | %s | %s%n",
                    p.getId(), p.getNome(), precoFmt(p.getPreco()), mesesFmt(p.getDuracaoMeses()));
        }
    }


    private void buscarPorNome() {
        System.out.print("Parte do nome: ");
        String termo = sc.nextLine();
        List<Plano> lista = planoService.buscarPorNome(termo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum plano encontrado para esse termo.");
            return;
        }
        System.out.println("\n--- Resultados ---");
        lista.forEach(p -> System.out.printf("[%d] %s | %s | %s%n",
                p.getId(), p.getNome(), precoFmt(p.getPreco()), mesesFmt(p.getDuracaoMeses())));
    }

    private void buscarPorFaixaPreco() {
        System.out.print("Preço mínimo (ex: 50.00): ");
        BigDecimal min = parseValor(sc.nextLine());
        System.out.print("Preço máximo (ex: 200.00): ");
        BigDecimal max = parseValor(sc.nextLine());

        List<Plano> lista = planoService.buscarPorFaixaPreco(min, max);
        if (lista.isEmpty()) {
            System.out.println("Nenhum plano encontrado nessa faixa de preço.");
            return;
        }
        System.out.println("\n--- Planos na Faixa ---");
        lista.forEach(p -> System.out.printf("[%d] %s | %s | %s%n",
                p.getId(), p.getNome(), precoFmt(p.getPreco()), mesesFmt(p.getDuracaoMeses())));
    }

    private void excluir() {
        System.out.print("ID do plano: ");
        long id = Long.parseLong(sc.nextLine());
        planoService.excluir(id);
        System.out.println("🗑️ Plano excluído com sucesso!");
    }

    // ===== Helpers =====

    private BigDecimal parseValor(String s) {
        String norm = s.trim().replace(",", ".");
        return new BigDecimal(norm);
    }

    private String mesesFmt(int meses) {
        return (meses == 1) ? "1 mês" : meses + " meses";
    }

    private String precoFmt(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(valor);
    }
}
