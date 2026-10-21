package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ItemSolicitacaoMutirao;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoMutirao;
import com.marceloaleixo.centrocirurgico.repository.SolicitacaoMutiraoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/mutiroes/relatorios-consumo")
public class RelatorioConsumoMutiraoController {
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final SolicitacaoMutiraoRepository solicitacoes;

    public RelatorioConsumoMutiraoController(SolicitacaoMutiraoRepository solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    @GetMapping
    public String relatorio(@RequestParam(required = false) String status,
                            @RequestParam(required = false) String categoria,
                            @RequestParam(required = false) String busca,
                            Model model) {
        List<LinhaRelatorio> linhas = filtrar(status, categoria, busca);
        BigDecimal totalSolicitado = linhas.stream().map(LinhaRelatorio::solicitada).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSeparado = linhas.stream().map(LinhaRelatorio::separada).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPendente = linhas.stream().map(LinhaRelatorio::pendente).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("linhas", linhas);
        model.addAttribute("totalItens", linhas.size());
        model.addAttribute("totalSolicitado", totalSolicitado);
        model.addAttribute("totalSeparado", totalSeparado);
        model.addAttribute("totalPendente", totalPendente);
        model.addAttribute("filtroStatus", status == null ? "" : status.trim().toUpperCase(Locale.ROOT));
        model.addAttribute("filtroCategoria", categoria == null ? "" : categoria.trim().toUpperCase(Locale.ROOT));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "mutiroes/relatorio-consumo";
    }

    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestParam(required = false) String status,
                                                @RequestParam(required = false) String categoria,
                                                @RequestParam(required = false) String busca) throws IOException {
        List<LinhaRelatorio> linhas = filtrar(status, categoria, busca);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Consumo e divergencias");
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(0, linhas.size()), 0, 9));

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            String[] headers = {"Solicitação", "Mutirão", "Status", "Item", "Categoria", "Unidade", "Quantidade solicitada", "Quantidade separada", "Quantidade pendente", "Conferência"};
            Row header = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }
            int rowIndex = 1;
            for (LinhaRelatorio linha : linhas) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(linha.solicitacaoId() == null ? "" : String.valueOf(linha.solicitacaoId()));
                row.createCell(1).setCellValue(valor(linha.evento()));
                row.createCell(2).setCellValue(valor(linha.status()));
                row.createCell(3).setCellValue(valor(linha.descricao()));
                row.createCell(4).setCellValue(valor(linha.categoria()));
                row.createCell(5).setCellValue(valor(linha.unidade()));
                row.createCell(6).setCellValue(linha.solicitada().doubleValue());
                row.createCell(7).setCellValue(linha.separada().doubleValue());
                row.createCell(8).setCellValue(linha.pendente().doubleValue());
                row.createCell(9).setCellValue(valor(linha.divergencia()));
            }
            for (int c = 0; c < headers.length; c++) sheet.autoSizeColumn(c);
            for (int c : new int[]{1, 3}) sheet.setColumnWidth(c, Math.min(sheet.getColumnWidth(c), 14000));
            workbook.write(output);
            String filename = "relatorio-consumo-mutiroes-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(output.toByteArray());
        }
    }

    private List<LinhaRelatorio> filtrar(String status, String categoria, String busca) {
        String filtroStatus = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        String filtroCategoria = categoria == null ? "" : categoria.trim().toUpperCase(Locale.ROOT);
        String termo = busca == null ? "" : busca.trim().toLowerCase(Locale.ROOT);
        List<LinhaRelatorio> linhas = new ArrayList<>();
        List<SolicitacaoMutirao> todas = solicitacoes.findAllByOrderByCriadoEmDesc();
        for (SolicitacaoMutirao s : todas) {
            if (!filtroStatus.isBlank() && !filtroStatus.equalsIgnoreCase(s.getStatus())) continue;
            String evento = s.getDia() != null && s.getDia().getEvento() != null ? s.getDia().getEvento().getNome() : "Mutirão não informado";
            for (ItemSolicitacaoMutirao item : s.getItens()) {
                if (!filtroCategoria.isBlank() && !filtroCategoria.equalsIgnoreCase(item.getCategoria())) continue;
                String descricao = item.getDescricao() == null ? "" : item.getDescricao();
                if (!termo.isBlank() && !descricao.toLowerCase(Locale.ROOT).contains(termo)
                        && !evento.toLowerCase(Locale.ROOT).contains(termo)
                        && !String.valueOf(s.getId()).contains(termo)) continue;
                BigDecimal solicitada = item.getQuantidadeSolicitada() == null ? BigDecimal.ZERO : item.getQuantidadeSolicitada();
                BigDecimal separada = item.getQuantidadeSeparada() == null ? BigDecimal.ZERO : item.getQuantidadeSeparada();
                BigDecimal pendente = solicitada.subtract(separada).max(BigDecimal.ZERO);
                String divergencia = separada.compareTo(solicitada) == 0 ? "COMPLETO" : separada.signum() == 0 ? "NÃO SEPARADO" : "PARCIAL";
                linhas.add(new LinhaRelatorio(s.getId(), evento, s.getStatus(), descricao, item.getCategoria(), item.getUnidade(), solicitada, separada, pendente, divergencia));
            }
        }
        return linhas;
    }

    private String valor(String texto) { return texto == null ? "" : texto; }

    public record LinhaRelatorio(Long solicitacaoId, String evento, String status, String descricao,
                                 String categoria, String unidade, BigDecimal solicitada,
                                 BigDecimal separada, BigDecimal pendente, String divergencia) {}
}
