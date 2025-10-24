package com.br.inatel;

import com.br.inatel.config.ConexaoBD;
import com.br.inatel.controller.*;
import com.br.inatel.dao.*;
import com.br.inatel.service.*;
import com.br.inatel.service.impl.*;

import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = ConexaoBD.obterConexao();

            // ===== INSTANCIA DAOs =====
            AlunoDao alunoDao = new AlunoDao(conn);
            InstrutorDao instrutorDao = new InstrutorDao(conn);
            PlanoDao planoDao = new PlanoDao(conn);
            AssinaturaDao assinaturaDao = new AssinaturaDao(conn);
            PagamentoDao pagamentoDao = new PagamentoDao(conn);
            ExercicioDao exercicioDao = new ExercicioDao(conn);
            TreinoDao treinoDao = new TreinoDao(conn);
            TreinoExercicioDao treinoExercicioDao = new TreinoExercicioDao(conn);
            FrequenciaDao frequenciaDao = new FrequenciaDao(conn);

            // ===== INSTANCIA Services =====
            AlunoService alunoService = new AlunoServiceImpl(alunoDao);
            InstrutorService instrutorService = new InstrutorServiceImpl(instrutorDao);
            PlanoService planoService = new PlanoServiceImpl(planoDao);
            AssinaturaService assinaturaService = new AssinaturaServiceImpl(assinaturaDao, alunoDao, planoDao);
            PagamentoService pagamentoService = new PagamentoServiceImpl(pagamentoDao, assinaturaDao);
            ExercicioService exercicioService = new ExercicioServiceImpl(exercicioDao);
            TreinoService treinoService = new TreinoServiceImpl(treinoDao, instrutorDao, alunoDao);
            TreinoExercicioService treinoExercicioService = new TreinoExercicioServiceImpl(treinoExercicioDao, treinoDao, exercicioDao);
            FrequenciaService frequenciaService = new FrequenciaServiceImpl(frequenciaDao, alunoDao);

            // ===== INSTANCIA Controllers =====
            AlunoController alunoController = new AlunoController(alunoService, sc);
            InstrutorController instrutorController = new InstrutorController(instrutorService, sc);
            PlanoController planoController = new PlanoController(planoService, sc);
            AssinaturaController assinaturaController = new AssinaturaController(assinaturaService, sc);
            PagamentoController pagamentoController = new PagamentoController(pagamentoService, sc);
            ExercicioController exercicioController = new ExercicioController(exercicioService, sc);
            TreinoController treinoController = new TreinoController(treinoService, sc);
            TreinoExercicioController treinoExercicioController = new TreinoExercicioController(treinoExercicioService, sc);
            FrequenciaController frequenciaController = new FrequenciaController(frequenciaService, sc);

            // =====  MENU PRINCIPAL =====
            int opcao = -1;
            while (opcao != 0) {
                System.out.println("\n================= GYM MANAGER =================");
                System.out.println("1. Alunos");
                System.out.println("2. Instrutores");
                System.out.println("3. Planos");
                System.out.println("4. Assinaturas");
                System.out.println("5. Pagamentos");
                System.out.println("6. Exercícios");
                System.out.println("7. Treinos");
                System.out.println("8. Treino x Exercício");
                System.out.println("9. Frequências");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                try {
                    opcao = Integer.parseInt(sc.nextLine());

                    switch (opcao) {
                        case 1 -> alunoController.exibirMenu();
                        case 2 -> instrutorController.exibirMenu();
                        case 3 -> planoController.exibirMenu();
                        case 4 -> assinaturaController.exibirMenu();
                        case 5 -> pagamentoController.exibirMenu();
                        case 6 -> exercicioController.exibirMenu();
                        case 7 -> treinoController.exibirMenu();
                        case 8 -> treinoExercicioController.exibirMenu();
                        case 9 -> frequenciaController.exibirMenu();
                        case 0 -> System.out.println("Encerrando o sistema... 👋");
                        default -> System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite apenas números.");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ===== 6. FECHA CONEXÃO =====
            ConexaoBD.fecharConexao();
            sc.close();
        }
    }
}
