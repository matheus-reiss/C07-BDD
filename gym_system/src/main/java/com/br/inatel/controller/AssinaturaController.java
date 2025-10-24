package com.br.inatel.controller;

import com.br.inatel.model.Assinatura;
import com.br.inatel.model.enums.AssinaturaStatus;
import com.br.inatel.service.AssinaturaService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AssinaturaController {

    private final AssinaturaService assinaturaService;
    private final Scanner sc;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AssinaturaController(AssinaturaService assinaturaService, Scanner sc) {
        this.assinaturaService = assinaturaService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU ASSINATURAS =====");
            System.out.println("1. Cadastrar nova assinatura");
            System.out.println("2. Atualizar assinatura");
            System.out.println("3. Listar todas");
            System.out.println("4. Listar por aluno");
            System.out.println("5. Listar por plano");
            System.out.println("6. Alterar status da assinatura");
            System.out.println("7. Cancelar assinatura");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criarAssinatura();
                    case 2 -> atualizarAssinatura();
                    case 3 -> listarTodas();
                    case 4 -> buscarPorAluno();
                    case 5 -> buscarPorPlano();
                    case 6 -> alterarStatus();
                    case 7 -> excluirAssinatura();
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

    // ====== OPÇÕES ======

    private void criarAssinatura() {
        System.out.println("\n--- Nova Assinatura ---");
        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        System.out.print("ID do plano: ");
        long idPlano = Long.parseLong(sc.nextLine());
        System.out.print("Data de início (dd/MM/yyyy): ");
        String dataInicioStr = sc.nextLine();
        System.out.print("Data de fim (dd/MM/yyyy): ");
        String dataFimStr = sc.nextLine();

        LocalDate dataInicio = LocalDate.parse(dataInicioStr, fmt);
        LocalDate dataFim = LocalDate.parse(dataFimStr, fmt);

        Assinatura assinatura = assinaturaService.criar(idAluno, idPlano, dataInicio, dataFim);
        System.out.println("✅ Assinatura criada com sucesso! ID: " + assinatura.getId());
    }

    private void atualizarAssinatura() {
        System.out.println("\n--- Atualizar Assinatura ---");
        System.out.print("ID da assinatura: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Nova data de início (dd/MM/yyyy): ");
        String dataInicioStr = sc.nextLine();
        System.out.print("Nova data de fim (dd/MM/yyyy): ");
        String dataFimStr = sc.nextLine();
        System.out.print("Novo status (ATIVA / INATIVA / CANCELADA / ATRASADA): ");
        String statusStr = sc.nextLine().toUpperCase();

        LocalDate dataInicio = LocalDate.parse(dataInicioStr, fmt);
        LocalDate dataFim = LocalDate.parse(dataFimStr, fmt);
        AssinaturaStatus status = AssinaturaStatus.valueOf(statusStr);

        Assinatura atualizada = assinaturaService.atualizar(id, dataInicio, dataFim, status);
        System.out.println("✅ Assinatura atualizada com sucesso! Status: " + atualizada.getStatus());
    }

    private void listarTodas() {
        System.out.println("\n--- Lista de Assinaturas ---");
        List<Assinatura> lista = assinaturaService.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma assinatura cadastrada.");
            return;
        }
        for (Assinatura a : lista) {
            System.out.printf("[%d] Aluno: %s | Plano: %s | Início: %s | Fim: %s | Status: %s%n",
                    a.getId(),
                    a.getAluno() != null ? a.getAluno().getNome() : "N/A",
                    a.getPlano() != null ? a.getPlano().getNome() : "N/A",
                    a.getDataInicio() != null ? a.getDataInicio().format(fmt) : "-",
                    a.getDataFim() != null ? a.getDataFim().format(fmt) : "-",
                    a.getStatus());
        }
    }


    private void buscarPorAluno() {
        System.out.print("Digite o ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        List<Assinatura> lista = assinaturaService.listarPorAluno(idAluno);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma assinatura encontrada para esse aluno.");
        } else {
            System.out.println("\n--- Assinaturas do Aluno ---");
            lista.forEach(a -> System.out.printf("[%d] Plano: %s | Status: %s%n",
                    a.getId(),
                    a.getPlano() != null ? a.getPlano().getNome() : "N/A",
                    a.getStatus()));
        }
    }

    private void buscarPorPlano() {
        System.out.print("Digite o ID do plano: ");
        long idPlano = Long.parseLong(sc.nextLine());
        List<Assinatura> lista = assinaturaService.listarPorPlano(idPlano);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma assinatura encontrada para esse plano.");
        } else {
            System.out.println("\n--- Assinaturas do Plano ---");
            lista.forEach(a -> System.out.printf("[%d] Aluno: %s | Status: %s%n",
                    a.getId(),
                    a.getAluno() != null ? a.getAluno().getNome() : "N/A",
                    a.getStatus()));
        }
    }

    private void alterarStatus() {
        System.out.print("ID da assinatura: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo status (ATIVA / INATIVA / CANCELADA / ATRASADA): ");
        String statusStr = sc.nextLine().toUpperCase();
        AssinaturaStatus status = AssinaturaStatus.valueOf(statusStr);

        assinaturaService.alterarStatus(id, status);
        System.out.println("✅ Status atualizado para: " + status);
    }

    private void excluirAssinatura() {
        System.out.print("ID da assinatura: ");
        long id = Long.parseLong(sc.nextLine());

        try{
            assinaturaService.excluir(id);
            System.out.println("✅ Assinatura cancelada com sucesso!");
        } catch (BusinessException | NotFoundException e) {
            System.out.println("❌ Erro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
