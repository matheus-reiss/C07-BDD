package com.br.inatel.controller;

import com.br.inatel.model.Pagamento;
import com.br.inatel.model.enums.PagamentoStatus;
import com.br.inatel.service.PagamentoService;
import com.br.inatel.service.exception.BusinessException;
import com.br.inatel.service.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final Scanner sc;

    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtCompetencia = DateTimeFormatter.ofPattern("MM/yyyy");

    public PagamentoController(PagamentoService pagamentoService, Scanner sc) {
        this.pagamentoService = pagamentoService;
        this.sc = sc;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MENU PAGAMENTOS =====");
            System.out.println("1. Criar pagamento");
            System.out.println("2. Atualizar pagamento");
            System.out.println("3. Listar todos");
            System.out.println("4. Listar por assinatura");
            System.out.println("5. Listar por status");
            System.out.println("6. Listar vencidos");
            System.out.println("7. Alterar status");
            System.out.println("8. Registrar pagamento ");
            System.out.println("9. Estornar pagamento");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> atualizar();
                    case 3 -> listarTodos();
                    case 4 -> listarPorAssinatura();
                    case 5 -> listarPorStatus();
                    case 6 -> listarVencidos();
                    case 7 -> alterarStatus();
                    case 8 -> registrarPagamento();
                    case 9 -> excluir();
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
        System.out.println("\n--- Criar Pagamento ---");
        System.out.print("ID da assinatura: ");
        long idAssinatura = Long.parseLong(sc.nextLine());

        System.out.print("Competência (MM/yyyy): ");
        YearMonth ym = YearMonth.parse(sc.nextLine(), fmtCompetencia);
        LocalDate competencia = ym.atDay(1);

        System.out.print("Valor (ex: 99.90): ");
        BigDecimal valor = parseValor(sc.nextLine());

        System.out.print("Data de vencimento (dd/MM/yyyy): ");
        LocalDate vencimento = LocalDate.parse(sc.nextLine(), fmtData);

        Pagamento p = pagamentoService.criar(idAssinatura, competencia, valor, vencimento, null);
        System.out.println("✅ Pagamento criado! ID: " + p.getId());
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Pagamento ---");
        System.out.print("ID do pagamento: ");
        long id = Long.parseLong(sc.nextLine());

        System.out.print("ID da assinatura: ");
        long idAssinatura = Long.parseLong(sc.nextLine());

        System.out.print("Competência (MM/yyyy): ");
        YearMonth ym = YearMonth.parse(sc.nextLine(), fmtCompetencia);
        LocalDate competencia = ym.atDay(1);

        System.out.print("Valor (ex: 99.90): ");
        BigDecimal valor = parseValor(sc.nextLine());

        System.out.print("Data de vencimento (dd/MM/yyyy): ");
        LocalDate vencimento = LocalDate.parse(sc.nextLine(), fmtData);

        PagamentoStatus status = lerStatus("Novo status");

        System.out.print("Data do pagamento (dd/MM/yyyy) [deixe vazio para manter/auto]: ");
        String dataPgStr = sc.nextLine();
        LocalDate dataPagamento = dataPgStr.isBlank() ? null : LocalDate.parse(dataPgStr, fmtData);

        Pagamento p = pagamentoService.atualizar(id, idAssinatura, competencia, valor, vencimento, status, dataPagamento);
        System.out.println("✅ Pagamento atualizado! Status atual: " + p.getStatus());
    }

    private void listarTodos() {
        System.out.println("\n--- Lista de Pagamentos ---");
        List<Pagamento> lista = pagamentoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum pagamento cadastrado.");
            return;
        }
        lista.forEach(this::imprimirPagamentoLinha);
    }

    private void listarPorAssinatura() {
        System.out.print("ID da assinatura: ");
        long idAssinatura = Long.parseLong(sc.nextLine());
        List<Pagamento> lista = pagamentoService.listarPorAssinatura(idAssinatura);
        if (lista.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado para essa assinatura.");
            return;
        }
        lista.forEach(this::imprimirPagamentoLinha);
    }

    private void listarPorStatus() {
        PagamentoStatus status = lerStatus("Status para filtrar");
        List<Pagamento> lista = pagamentoService.listarPorStatus(status);
        if (lista.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado com status " + status + ".");
            return;
        }
        lista.forEach(this::imprimirPagamentoLinha);
    }

    private void listarVencidos() {
        System.out.println("\n--- Pagamentos Vencidos ---");
        List<Pagamento> lista = pagamentoService.listarVencidos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum pagamento vencido no momento.");
            return;
        }
        lista.forEach(this::imprimirPagamentoLinha);
    }

    private void alterarStatus() {
        System.out.print("ID do pagamento: ");
        long id = Long.parseLong(sc.nextLine());
        PagamentoStatus status = lerStatus("Novo status");
        pagamentoService.alterarStatus(id, status);
        System.out.println("✅ Status atualizado para: " + status);
    }

    private void registrarPagamento() {
        System.out.print("ID do pagamento: ");
        long id = Long.parseLong(sc.nextLine());
        System.out.print("Data do pagamento (dd/MM/yyyy) [vazio = hoje]: ");
        String s = sc.nextLine();
        LocalDate dataPg = s.isBlank() ? null : LocalDate.parse(s, fmtData);
        pagamentoService.registrarPagamento(id, dataPg);
        System.out.println("💸 Pagamento registrado!");
    }

    private void excluir() {
        System.out.print("ID do pagamento: ");
        long id = Long.parseLong(sc.nextLine());
        pagamentoService.excluir(id);
        System.out.println("🗑 Pagamento estornado com sucesso!");
    }

    // ===== Helpers =====

    private BigDecimal parseValor(String s) {
        String norm = s.trim().replace(",", ".");
        return new BigDecimal(norm);
    }

    private PagamentoStatus lerStatus(String label) {
        System.out.println(label + " (opções: " + Arrays.toString(PagamentoStatus.values()) + "): ");
        String in = sc.nextLine().trim().toUpperCase();
        return PagamentoStatus.valueOf(in);
    }

    private void imprimirPagamentoLinha(Pagamento p) {
        System.out.printf("[%d] Assinatura #%d | Comp.: %s | Valor: %s | Venc.: %s | Status: %s%n",
                p.getId(),
                p.getAssinatura() != null ? p.getAssinatura().getId() : -1,
                p.getCompetencia() != null ? fmtCompetencia.format(YearMonth.from(p.getCompetencia())) : "-",
                p.getValor() != null ? p.getValor().toPlainString() : "-",
                p.getDataVencimento() != null ? p.getDataVencimento().format(fmtData) : "-",
                p.getStatus());
    }

    private void imprimirPagamentoDetalhado(Pagamento p) {
        System.out.println("\n--- Pagamento ---");
        System.out.println("ID: " + p.getId());
        System.out.println("Assinatura: " + (p.getAssinatura() != null ? p.getAssinatura().getId() : "-"));
        System.out.println("Competência: " + (p.getCompetencia() != null ? fmtCompetencia.format(YearMonth.from(p.getCompetencia())) : "-"));
        System.out.println("Valor: " + (p.getValor() != null ? p.getValor().toPlainString() : "-"));
        System.out.println("Vencimento: " + (p.getDataVencimento() != null ? p.getDataVencimento().format(fmtData) : "-"));
        System.out.println("Status: " + p.getStatus());
        System.out.println("Data pagamento: " + (p.getDataPagamento() != null ? p.getDataPagamento().format(fmtData) : "-"));
    }
}
