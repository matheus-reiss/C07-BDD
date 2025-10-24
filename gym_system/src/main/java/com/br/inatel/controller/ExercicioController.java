package com.br.inatel.controller;

import com.br.inatel.model.Exercicio;
import com.br.inatel.service.ExercicioService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;
import java.util.Scanner;

public class ExercicioController {

    private final ExercicioService exercicioService;
    private final Scanner sc;

    public ExercicioController(ExercicioService exercicioService, Scanner sc) {
        this.exercicioService = exercicioService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU EXERCÍCIO =====");
            System.out.println("1. Cadastrar novo exercício");
            System.out.println("2. Atualizar exercício");
            System.out.println("3. Listar todos");
            System.out.println("4. Buscar por ID");
            System.out.println("5. Buscar por nome");
            System.out.println("6. Buscar por grupo muscular");
            System.out.println("7. Excluir exercício");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> atualizar();
                    case 3 -> listarTodos();
                    case 4 -> buscarPorId();
                    case 5 -> buscarPorNome();
                    case 6 -> buscarPorGrupo();
                    case 7 -> excluir();
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
        System.out.println("\n--- Cadastro de Exercício ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Grupo muscular: ");
        String grupo = sc.nextLine();

        Exercicio e = exercicioService.criar(nome, grupo);
        System.out.println("✅ Exercício cadastrado! ID: " + e.getId());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Exercício ---");
        System.out.print("ID: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Novo grupo muscular: ");
        String grupo = sc.nextLine();

        Exercicio e = exercicioService.atualizar(id, nome, grupo);
        System.out.println("✅ Exercício atualizado: " + e.getNome() + " | " + e.getGrupoMuscular());
    }

    private void listarTodos() {
        System.out.println("\n--- Lista de Exercícios ---");
        List<Exercicio> lista = exercicioService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum exercício cadastrado.");
            return;
        }
        for (Exercicio e : lista) {
            System.out.printf("[%d] %s | %s%n",
                    e.getId(), e.getNome(), e.getGrupoMuscular());
        }
    }

    private void buscarPorId() {
        System.out.print("ID do exercício: ");
        long id = Long.parseLong(sc.nextLine());
        Exercicio e = exercicioService.buscarPorId(id);
        System.out.printf("Exercício #%d - %s | %s%n", e.getId(), e.getNome(), e.getGrupoMuscular());
    }

    private void buscarPorNome() {
        System.out.print("Parte do nome: ");
        String termo = sc.nextLine();
        List<Exercicio> lista = exercicioService.buscarPorNome(termo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum exercício encontrado para esse termo.");
        } else {
            System.out.println("\n--- Resultados ---");
            lista.forEach(e -> System.out.printf("[%d] %s | %s%n",
                    e.getId(), e.getNome(), e.getGrupoMuscular()));
        }
    }

    private void buscarPorGrupo() {
        System.out.print("Grupo muscular: ");
        String grupo = sc.nextLine();
        List<Exercicio> lista = exercicioService.buscarPorGrupoMuscular(grupo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum exercício encontrado para esse grupo muscular.");
        } else {
            System.out.println("\n--- Resultados ---");
            lista.forEach(e -> System.out.printf("[%d] %s | %s%n",
                    e.getId(), e.getNome(), e.getGrupoMuscular()));
        }
    }

    private void excluir() {
        System.out.print("ID do exercício: ");
        long id = Long.parseLong(sc.nextLine());

        if (exercicioService.possuiVinculos(id)) {
            System.out.println("❌ Não é possível excluir: exercício vinculado a treinos.");
            return;
        }

        System.out.print("Confirmar exclusão? (s/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("s")) return;
        exercicioService.excluir(id);
        System.out.println("✅ Exercício removido.");
    }
}
