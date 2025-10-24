package com.br.inatel.controller;

import com.br.inatel.model.Instrutor;
import com.br.inatel.service.InstrutorService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;
import java.util.Scanner;

public class InstrutorController {

    private final InstrutorService instrutorService;
    private final Scanner sc;

    public InstrutorController(InstrutorService instrutorService, Scanner sc) {
        this.instrutorService = instrutorService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU INSTRUTOR =====");
            System.out.println("1. Cadastrar novo instrutor");
            System.out.println("2. Atualizar instrutor");
            System.out.println("3. Listar todos");
            System.out.println("4. Buscar por nome");
            System.out.println("5. Excluir instrutor");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> atualizar();
                    case 3 -> listarTodos();
                    case 4 -> buscarPorNome();
                    case 5 -> excluir();
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

    // ===== AÇÕES =====

    private void criar() {
        System.out.println("\n--- Cadastro de Instrutor ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("CREF: ");
        String cref = sc.nextLine();

        Instrutor i = instrutorService.criar(nome, cref);
        System.out.println("✅ Instrutor cadastrado! ID: " + i.getId());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Instrutor ---");
        System.out.print("ID: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Novo CREF: ");
        String cref = sc.nextLine();

        Instrutor i = instrutorService.atualizar(id, nome, cref);
        System.out.println("✅ Instrutor atualizado: " + i.getNome() + " | CREF: " + i.getCref());
    }

    private void listarTodos() {
        System.out.println("\n--- Lista de Instrutores ---");
        List<Instrutor> lista = instrutorService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum instrutor cadastrado.");
            return;
        }
        for (Instrutor i : lista) {
            System.out.printf("[%d] %s | CREF: %s%n",
                    i.getId(), i.getNome(), i.getCref());
        }
    }
    private void buscarPorNome() {
        System.out.print("Parte do nome: ");
        String termo = sc.nextLine();
        List<Instrutor> lista = instrutorService.buscarPorNome(termo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum instrutor encontrado para esse termo.");
        } else {
            System.out.println("\n--- Resultados ---");
            lista.forEach(i ->
                    System.out.printf("[%d] %s | CREF: %s%n", i.getId(), i.getNome(), i.getCref()));
        }
    }

    private void excluir() {
        System.out.print("ID do instrutor: ");
        long id = Long.parseLong(sc.nextLine());
        instrutorService.excluir(id);
        System.out.println("🗑️ Instrutor excluído com sucesso!");
    }
}
