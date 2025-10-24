package com.br.inatel.controller;

import com.br.inatel.model.Treino;
import com.br.inatel.service.TreinoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TreinoController {

    private final TreinoService treinoService;
    private final Scanner sc;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TreinoController(TreinoService treinoService, Scanner sc) {
        this.treinoService = treinoService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU TREINO =====");
            System.out.println("1. Cadastrar novo treino");
            System.out.println("2. Atualizar treino");
            System.out.println("3. Listar todos");
            System.out.println("4. Listar por aluno");
            System.out.println("5. Listar por instrutor");
            System.out.println("6. Alterar status (ativo/inativo)");
            System.out.println("7. Excluir treino");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> atualizar();
                    case 3 -> listarTodos();
                    case 4 -> listarPorAluno();
                    case 5 -> listarPorInstrutor();
                    case 6 -> alterarAtivo();
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

    // ===== AÇÕES =====

    private void criar() {
        System.out.println("\n--- Cadastro de Treino ---");
        System.out.print("Título: ");
        String titulo = sc.nextLine();

        System.out.print("Data de criação (dd/MM/yyyy) [vazio = hoje]: ");
        String sData = sc.nextLine();
        LocalDate createdAt = sData.isBlank() ? LocalDate.now() : LocalDate.parse(sData, fmt);

        System.out.print("Ativo? (s/n): ");
        boolean ativo = sc.nextLine().trim().equalsIgnoreCase("s");

        System.out.print("ID do instrutor: ");
        long idInstrutor = Long.parseLong(sc.nextLine());

        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());

        Treino t = treinoService.criar(titulo, createdAt, ativo, idInstrutor, idAluno);
        System.out.println("✅ Treino criado! ID: " + t.getId());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Treino ---");
        System.out.print("ID do treino: ");
        long id = Long.parseLong(sc.nextLine());

        System.out.print("Novo título: ");
        String titulo = sc.nextLine();

        System.out.print("Nova data de criação (dd/MM/yyyy) [vazio = manter hoje]: ");
        String sData = sc.nextLine();
        LocalDate createdAt = sData.isBlank() ? LocalDate.now() : LocalDate.parse(sData, fmt);

        System.out.print("Ativo? (s/n): ");
        boolean ativo = sc.nextLine().trim().equalsIgnoreCase("s");

        System.out.print("ID do instrutor: ");
        long idInstrutor = Long.parseLong(sc.nextLine());

        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());

        Treino t = treinoService.atualizar(id, titulo, createdAt, ativo, idInstrutor, idAluno);
        System.out.println("✅ Treino atualizado: " + t.getTitulo() + " | " + (t.isAtivo() ? "Ativo" : "Inativo"));
    }

    private void listarTodos() {
        System.out.println("\n--- Lista de Treinos ---");
        List<Treino> lista = treinoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum treino cadastrado.");
            return;
        }
        lista.forEach(this::imprimirLinha);
    }

    private void listarPorAluno() {
        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        List<Treino> lista = treinoService.listarPorAluno(idAluno);
        if (lista.isEmpty()) {
            System.out.println("Nenhum treino encontrado para esse aluno.");
            return;
        }
        lista.forEach(this::imprimirLinha);
    }

    private void listarPorInstrutor() {
        System.out.print("ID do instrutor: ");
        long idInstrutor = Long.parseLong(sc.nextLine());
        List<Treino> lista = treinoService.listarPorInstrutor(idInstrutor);
        if (lista.isEmpty()) {
            System.out.println("Nenhum treino encontrado para esse instrutor.");
            return;
        }
        lista.forEach(this::imprimirLinha);
    }

    private void alterarAtivo() {
        System.out.print("ID do treino: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo status (s=Ativo / n=Inativo): ");
        boolean ativo = sc.nextLine().trim().equalsIgnoreCase("s");

        treinoService.alterarAtivo(id, ativo);
        System.out.println("✅ Status alterado para: " + (ativo ? "Ativo" : "Inativo"));
    }

    private void excluir() {
        System.out.print("ID do treino: ");
        long id = Long.parseLong(sc.nextLine());

        System.out.println("Tem certeza que deseja excluir? (s/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada");
            return;
        }

        treinoService.excluir(id);
        System.out.println("Treino excluido com sucesso!");
    }

    // ===== Helpers =====

    private void imprimirLinha(Treino t) {
        System.out.printf("[%d] %s | Criado: %s | %s | Instrutor#%s | Aluno#%s%n",
                t.getId(),
                t.getTitulo(),
                t.getCreatedAt() != null ? t.getCreatedAt().format(fmt) : "-",
                t.isAtivo() ? "Ativo" : "Inativo",
                t.getInstrutor() != null ? t.getInstrutor().getId() : "-",
                t.getAluno() != null ? t.getAluno().getId() : "-");
    }

    private void imprimirDetalhe(Treino t) {
        System.out.println("\n--- Treino ---");
        System.out.println("ID: " + t.getId());
        System.out.println("Título: " + t.getTitulo());
        System.out.println("Criado em: " + (t.getCreatedAt() != null ? t.getCreatedAt().format(fmt) : "-"));
        System.out.println("Status: " + (t.isAtivo() ? "Ativo" : "Inativo"));
        System.out.println("Instrutor: " + (t.getInstrutor() != null ? t.getInstrutor().getId() : "-"));
        System.out.println("Aluno: " + (t.getAluno() != null ? t.getAluno().getId() : "-"));
    }
}
