package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.service.FechamentoRhService;
import com.marceloaleixo.centrocirurgico.service.FechamentoRhExcelService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.marceloaleixo.centrocirurgico.entity.FechamentoRhPeriodo;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/relatorios/fechamento-rh")
public class FechamentoRhController {
    private final FechamentoRhService service;
    private final FechamentoRhExcelService excelService;

    public FechamentoRhController(FechamentoRhService service, FechamentoRhExcelService excelService) {
        this.service = service;
        this.excelService = excelService;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        if (fim.isBefore(inicio)) {
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
                    .body("A data final não pode ser anterior à data inicial.".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        byte[] arquivo = excelService.gerar(inicio, fim);
        String nomeArquivo = "fechamento-rh-" + inicio + "-a-" + fim + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(arquivo);
    }

    @PostMapping("/conferencia/{colaboradorId}")
    public String salvarConferencia(
            @PathVariable Long colaboradorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam String status,
            @RequestParam(required = false) String observacao,
            RedirectAttributes redirect) {
        try {
            service.registrarConferencia(inicio, fim, colaboradorId, status, observacao);
            redirect.addFlashAttribute("sucesso", "Conferência do colaborador salva.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        redirect.addAttribute("inicio", inicio);
        redirect.addAttribute("fim", fim);
        return "redirect:/relatorios/fechamento-rh";
    }

    @PostMapping("/fechar")
    public String fecharPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String observacao,
            RedirectAttributes redirect) {
        try {
            service.fecharPeriodo(inicio, fim, observacao);
            redirect.addFlashAttribute("sucesso", "Fechamento do RH concluído. O período foi bloqueado para alterações.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        redirect.addAttribute("inicio", inicio);
        redirect.addAttribute("fim", fim);
        return "redirect:/relatorios/fechamento-rh";
    }

    @PostMapping("/reabrir")
    public String reabrirPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam String justificativa,
            RedirectAttributes redirect) {
        try {
            service.reabrirPeriodo(inicio, fim, justificativa);
            redirect.addFlashAttribute("sucesso", "Período reaberto. A justificativa foi registrada na auditoria.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        redirect.addAttribute("inicio", inicio);
        redirect.addAttribute("fim", fim);
        return "redirect:/relatorios/fechamento-rh";
    }

    @GetMapping
    public String consultar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {
        YearMonth mesAtual = YearMonth.now();
        LocalDate dataInicio = inicio == null ? mesAtual.atDay(1) : inicio;
        LocalDate dataFim = fim == null ? mesAtual.atEndOfMonth() : fim;
        if (dataFim.isBefore(dataInicio)) {
            model.addAttribute("erro", "A data final não pode ser anterior à data inicial.");
            dataFim = dataInicio;
        }
        model.addAttribute("inicio", dataInicio);
        model.addAttribute("fim", dataFim);
        model.addAttribute("resumos", service.consultar(dataInicio, dataFim));
        model.addAttribute("fechamento", service.buscarFechamento(dataInicio, dataFim).orElse(null));
        model.addAttribute("historicoAuditoria", service.consultarAuditoria(dataInicio, dataFim));
        return "relatorios/fechamento-rh";
    }
}
