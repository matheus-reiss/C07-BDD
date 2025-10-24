package com.br.inatel.controller;

import com.br.inatel.model.TreinoExercicio;
import com.br.inatel.service.TreinoExercicioService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.util.List;
import java.util.Scanner;

public class TreinoExercicioController {

    private final TreinoExercicioService service;
    private final Scanner sc;

    public TreinoExercicioController(TreinoExercicioService service, Scanner sc) {
        this.service = service;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU TREINO x EXERCÍCIO =====");
            System.out.println("1. Adicionar exercício ao treino");
            System.out.println("2. Atualizar exercicio");
            System.out.println("3. Alterar ordem do exercicio");
            System.out.println("4. Trocar ordens entre exercícios");
            System.out.println("5. Listar exercícios do treino");
            System.out.println("6. Buscar exercício por ordem");
            System.out.println("7. Remover exercício do treino ");
            System.out.println("8. Remover todos os exercícios do treino");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> adicionar();
                    case 2 -> atualizar();
                    case 3 -> alterarOrdem();
                    case 4 -> trocarOrdem();
                    case 5 -> listarPorTreino();
                    case 6 -> buscarPorChave();
                    case 7 -> removerPorChave();
                    case 8 -> removerTodosDoTreino();
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

    private void adicionar() {
        System.out.println("\n--- Adicionar Exercício ao Treino ---");
        long idTreino = lerLong("ID do treino: ");
        short ordem = lerShort("Ordem (>=1): ");
        long idExercicio = lerLong("ID do exercício: ");
        short series = lerShort("Séries: ");
        short reps = lerShort("Reps: ");
        Integer carga = lerIntegerOuVazio("Carga (kg) [vazio = null]: ");
        short descanso = lerShort("Descanso (seg): ");

        TreinoExercicio te = service.adicionar(idTreino, ordem, idExercicio, series, reps, carga, descanso);
        System.out.println("✅ Item adicionado: treino=" + idTreino + " ordem=" + te.getOrdem());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Item (mesma ordem) ---");
        long idTreino = lerLong("ID do treino: ");
        short ordem = lerShort("Ordem atual: ");
        long idExercicio = lerLong("Novo ID do exercício: ");
        short series = lerShort("Novas séries: ");
        short reps = lerShort("Novas reps: ");
        Integer carga = lerIntegerOuVazio("Nova carga (kg) [vazio = null]: ");
        short descanso = lerShort("Novo descanso (seg): ");

        TreinoExercicio te = service.atualizar(idTreino, ordem, idExercicio, series, reps, carga, descanso);
        System.out.println("✅ Item atualizado: treino=" + idTreino + " ordem=" + te.getOrdem());
    }

    private void alterarOrdem() {
        System.out.println("\n--- Alterar Ordem ---");
        long idTreino = lerLong("ID do treino: ");
        short ordemAntiga = lerShort("Ordem antiga: ");
        short novaOrdem = lerShort("Nova ordem: ");

        service.alterarOrdem(idTreino, ordemAntiga, novaOrdem);
        System.out.println("✅ Ordem alterada de " + ordemAntiga + " para " + novaOrdem + " (treino=" + idTreino + ")");
    }

    private void trocarOrdem() {
        System.out.println("\n--- Trocar Ordens ---");
        long idTreino = lerLong("ID do treino: ");
        short ordemA = lerShort("Ordem A: ");
        short ordemB = lerShort("Ordem B: ");

        service.trocarOrdem(idTreino, ordemA, ordemB);
        System.out.println("✅ Ordens trocadas (" + ordemA + " ↔ " + ordemB + ") no treino " + idTreino);
    }

    private void listarPorTreino() {
        System.out.println("\n--- Itens do Treino ---");
        long idTreino = lerLong("ID do treino: ");
        List<TreinoExercicio> itens = service.listarPorTreino(idTreino);
        if (itens.isEmpty()) {
            System.out.println("Nenhum item para este treino.");
            return;
        }
        itens.forEach(this::imprimirLinha);
    }

    private void buscarPorChave() {
        System.out.println("\n--- Buscar Item ---");
        long idTreino = lerLong("ID do treino: ");
        short ordem = lerShort("Ordem: ");
        TreinoExercicio te = service.buscarPorChave(idTreino, ordem);
        imprimirDetalhe(te);
    }

    private void removerPorChave() {
        System.out.println("\n--- Remover Item ---");
        long idTreino = lerLong("ID do treino: ");
        short ordem = lerShort("Ordem: ");
        service.removerPorChave(idTreino, ordem);
        System.out.println("🗑️ Item removido (treino=" + idTreino + ", ordem=" + ordem + ").");
    }

    private void removerTodosDoTreino() {
        System.out.println("\n--- Remover TODOS os Itens ---");
        long idTreino = lerLong("ID do treino: ");
        int removidos = service.removerTodosDoTreino(idTreino);
        System.out.println("🗑️ " + removidos + " item(ns) removido(s) do treino " + idTreino + ".");
    }

    // ===== Helpers =====

    private long lerLong(String label) {
        System.out.print(label);
        return Long.parseLong(sc.nextLine());
    }

    private short lerShort(String label) {
        System.out.print(label);
        return Short.parseShort(sc.nextLine());
    }

    private Integer lerIntegerOuVazio(String label) {
        System.out.print(label);
        String s = sc.nextLine().trim();
        return s.isBlank() ? null : Integer.parseInt(s);
    }

    private void imprimirLinha(TreinoExercicio te) {
        String nomeEx = (te.getExercicio() != null && te.getExercicio().getNome() != null)
                ? te.getExercicio().getNome()
                : ("Exercício #" + (te.getExercicio() != null ? te.getExercicio().getId() : "-"));

        String carga = te.getCargaKg() != null ? te.getCargaKg() + "kg" : "-";

        System.out.printf("Ordem %d | %s | Séries: %d | Reps: %d | Carga: %s | Descanso: %ds%n",
                te.getOrdem(),
                nomeEx,
                te.getSeries(),
                te.getReps(),
                carga,
                te.getDescansoSeg());
    }

    private void imprimirDetalhe(TreinoExercicio te) {
        String nomeEx = (te.getExercicio() != null && te.getExercicio().getNome() != null)
                ? te.getExercicio().getNome()
                : ("Exercício #" + (te.getExercicio() != null ? te.getExercicio().getId() : "-"));

        System.out.println("\n--- Item ---");
        System.out.println("Treino:    " + (te.getTreino() != null ? te.getTreino().getId() : "-"));
        System.out.println("Ordem:     " + te.getOrdem());
        System.out.println("Exercício: " + nomeEx); // <<--
        System.out.println("Séries:    " + te.getSeries());
        System.out.println("Reps:      " + te.getReps());
        System.out.println("Carga:     " + (te.getCargaKg() != null ? te.getCargaKg() + " kg" : "-"));
        System.out.println("Descanso:  " + te.getDescansoSeg() + " s");
    }

}
