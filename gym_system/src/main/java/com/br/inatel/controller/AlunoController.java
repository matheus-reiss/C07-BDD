package com.br.inatel.controller;

import com.br.inatel.model.Aluno;
import com.br.inatel.service.AlunoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AlunoController {

    private final AlunoService alunoService;
    private final Scanner sc;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AlunoController(AlunoService alunoService, Scanner sc) {
        this.alunoService = alunoService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU ALUNO =====");
            System.out.println("1. Cadastrar novo aluno");
            System.out.println("2. Atualizar aluno");
            System.out.println("3. Listar todos");
            System.out.println("4. Buscar por ID");
            System.out.println("5. Buscar por nome");
            System.out.println("6. Ativar aluno");
            System.out.println("7. Desativar aluno ");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criarAluno();
                    case 2 -> atualizarAluno();
                    case 3 -> listarTodos();
                    case 4 -> buscarPorId();
                    case 5 -> buscarPorNome();
                    case 6 -> alterarAtivo(true);
                    case 7 -> alterarAtivo(false);
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

    private void criarAluno() {
        System.out.println("\n--- Cadastro de Aluno ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Data de nascimento (dd/MM/yyyy): ");
        String data = sc.nextLine();
        System.out.print("Telefone (opcional): ");
        String telefone = sc.nextLine();

        LocalDate nascimento = data.isBlank() ? null : LocalDate.parse(data, fmt);
        Aluno aluno = alunoService.criar(nome, nascimento, telefone);
        System.out.println("✅ Aluno cadastrado com sucesso! ID: " + aluno.getId());
    }

    private void atualizarAluno() {
        System.out.print("ID do aluno: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Nova data de nascimento (dd/MM/yyyy): ");
        String data = sc.nextLine();
        System.out.print("Novo telefone: ");
        String telefone = sc.nextLine();

        LocalDate nascimento = data.isBlank() ? null : LocalDate.parse(data, fmt);
        Aluno atualizado = alunoService.atualizar(id, nome, nascimento, telefone);
        System.out.println("✅ Aluno atualizado com sucesso: " + atualizado.getNome());
    }

    private void listarTodos() {
        List<Aluno> alunos = alunoService.listarTodos();
        System.out.println("\n--- Lista de Alunos ---");
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        for (Aluno a : alunos) {
            System.out.printf("[%d] %s | %s | %s | %s%n",
                    a.getId(),
                    a.getNome(),
                    a.getDataNascimento() != null ? a.getDataNascimento().format(fmt) : "N/A",
                    a.getTelefone() != null ? a.getTelefone() : "Sem telefone",
                    a.isAtivo() ? "Ativo" : "Inativo");
        }
    }

    private void buscarPorId() {
        System.out.print("Digite o ID do aluno: ");
        long id = Long.parseLong(sc.nextLine());
        Aluno a = alunoService.buscarPorId(id);
        System.out.printf("Aluno #%d - %s (%s)%n", a.getId(), a.getNome(), a.isAtivo() ? "Ativo" : "Inativo");
    }

    private void buscarPorNome() {
        System.out.print("Digite parte do nome: ");
        String termo = sc.nextLine();
        List<Aluno> lista = alunoService.buscarPorNome(termo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum aluno encontrado com esse termo.");
        } else {
            System.out.println("\n--- Resultados ---");
            lista.forEach(a -> System.out.printf("[%d] %s%n", a.getId(), a.getNome()));
        }
    }

    private void alterarAtivo(boolean ativar) {
        System.out.print("ID do aluno: ");
        long id = Long.parseLong(sc.nextLine());
        if (ativar) alunoService.ativar(id); else alunoService.desativar(id);
        System.out.println(ativar ? "✅ Aluno ativado." : "🚫 Aluno desativado.");
    }

}
