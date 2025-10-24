package com.br.inatel.controller;

import com.br.inatel.model.Frequencia;
import com.br.inatel.service.FrequenciaService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class FrequenciaController {

    private final FrequenciaService frequenciaService;
    private final Scanner sc;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FrequenciaController(FrequenciaService frequenciaService, Scanner sc) {
        this.frequenciaService = frequenciaService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU FREQUÊNCIA =====");
            System.out.println("1. Registrar check-in");
            System.out.println("2. Atualizar check-in");
            System.out.println("3. Listar todas");
            System.out.println("4. Listar por aluno");
            System.out.println("5. Listar por período");
            System.out.println("6. Último check-in por aluno");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> registrarCheckin();
                    case 2 -> atualizarCheckin();
                    case 3 -> listarTodas();
                    case 4 -> listarPorAluno();
                    case 5 -> listarPorPeriodo();
                    case 6 -> ultimoCheckinPorAluno();
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

    private void registrarCheckin() {
        System.out.println("\n--- Registrar Check-in ---");
        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        System.out.print("Data do check-in (dd/MM/yyyy): ");
        LocalDate data = LocalDate.parse(sc.nextLine(), fmt);

        Frequencia f = frequenciaService.registrarCheckin(idAluno, data);
        System.out.println("✅ Check-in registrado! ID: " + f.getId());
    }

    private void atualizarCheckin() {
        System.out.println("\n--- Atualizar Check-in ---");
        System.out.print("ID da frequência: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        System.out.print("Nova data (dd/MM/yyyy): ");
        LocalDate data = LocalDate.parse(sc.nextLine(), fmt);

        Frequencia f = frequenciaService.atualizar(id, idAluno, data);
        System.out.println("✅ Check-in atualizado! ID: " + f.getId());
    }

    private void listarTodas() {
        System.out.println("\n--- Lista de Frequências ---");
        List<Frequencia> lista = frequenciaService.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("Nenhum check-in registrado.");
            return;
        }
        System.out.println("ID   | Aluno        | Data");
        System.out.println("---------------------------");
        for (Frequencia f : lista) {
            String nome = (f.getAluno() != null && f.getAluno().getNome() != null)
                    ? f.getAluno().getNome()
                    : ("Aluno #" + (f.getAluno() != null ? f.getAluno().getId() : "-"));
            String data = f.getDataCheckin() != null ? f.getDataCheckin().format(fmt) : "-";
            System.out.printf("%-4d | %-12s | %s%n", f.getId(), nome, data);
        }
    }


    private void listarPorAluno() {
        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        List<Frequencia> lista = frequenciaService.listarPorAluno(idAluno);
        if (lista.isEmpty()) {
            System.out.println("Nenhum check-in encontrado para esse aluno.");
            return;
        }
        System.out.println("\n--- Check-ins do Aluno ---");
        for (Frequencia f : lista) {
            String nome = (f.getAluno() != null && f.getAluno().getNome() != null)
                    ? f.getAluno().getNome()
                    : ("Aluno #" + idAluno);
            String data = f.getDataCheckin() != null ? f.getDataCheckin().format(fmt) : "-";
            System.out.printf("[%d] %s | %s%n", f.getId(), nome, data);
        }
    }


    private void listarPorPeriodo() {
        System.out.print("Data inicial (dd/MM/yyyy): ");
        LocalDate inicio = LocalDate.parse(sc.nextLine(), fmt);
        System.out.print("Data final (dd/MM/yyyy): ");
        LocalDate fim = LocalDate.parse(sc.nextLine(), fmt);

        List<Frequencia> lista = frequenciaService.listarPorPeriodo(inicio, fim);
        if (lista.isEmpty()) {
            System.out.println("Nenhum check-in no período informado.");
            return;
        }
        System.out.println("\n--- Check-ins no Período ---");
        for (Frequencia f : lista) {
            String nome = (f.getAluno() != null && f.getAluno().getNome() != null)
                    ? f.getAluno().getNome()
                    : ("Aluno #" + (f.getAluno() != null ? f.getAluno().getId() : "-"));
            String data = f.getDataCheckin() != null ? f.getDataCheckin().format(fmt) : "-";
            System.out.printf("%s | %s%n", nome, data);
        }
    }


    private void ultimoCheckinPorAluno() {
        System.out.print("ID do aluno: ");
        long idAluno = Long.parseLong(sc.nextLine());
        Frequencia f = frequenciaService.buscarUltimoCheckinPorAluno(idAluno);
        if (f == null) {
            System.out.println("Nenhum check-in encontrado para esse aluno.");
            return;
        }
        String nome = (f.getAluno() != null && f.getAluno().getNome() != null)
                ? f.getAluno().getNome()
                : ("Aluno #" + idAluno);
        String data = f.getDataCheckin() != null ? f.getDataCheckin().format(fmt) : "-";
        System.out.printf("Último check-in de %s: %s (Frequência ID=%d)%n", nome, data, f.getId());
    }


}
