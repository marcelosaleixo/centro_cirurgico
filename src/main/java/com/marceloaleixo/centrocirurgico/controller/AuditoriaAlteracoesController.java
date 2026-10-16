package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaAlteracao;
import com.marceloaleixo.centrocirurgico.service.AuditoriaAlteracaoService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/auditoria-alteracoes")
public class AuditoriaAlteracoesController {
    private final AuditoriaAlteracaoService service;
    public AuditoriaAlteracoesController(AuditoriaAlteracaoService service) { this.service = service; }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(required = false) String usuario,
                         @RequestParam(required = false) String entidade,
                         @RequestParam(required = false) String operacao,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         Model model) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            model.addAttribute("erroFiltro", "A data inicial não pode ser posterior à data final.");
            dataInicio = null;
            dataFim = null;
        }
        var resultado = service.pesquisar(pagina, usuario, entidade, operacao, dataInicio, dataFim);
        model.addAttribute("registros", resultado.getContent());
        model.addAttribute("paginaAtual", resultado.getNumber());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("temAnterior", resultado.hasPrevious());
        model.addAttribute("temProxima", resultado.hasNext());
        model.addAttribute("filtroUsuario", usuario);
        model.addAttribute("filtroEntidade", entidade);
        model.addAttribute("filtroOperacao", operacao);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        return "admin/auditoria-alteracoes";
    }

    @GetMapping("/exportar")
    public void exportar(@RequestParam(required = false) String usuario,
                         @RequestParam(required = false) String entidade,
                         @RequestParam(required = false) String operacao,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         HttpServletResponse response) throws IOException {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A data inicial não pode ser posterior à data final.");
            return;
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=auditoria-alteracoes.xlsx");
        var registros = service.exportar(usuario, entidade, operacao, dataInicio, dataFim);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Auditoria de alterações");
            String[] colunas = {"Data e hora", "Usuário", "Entidade", "ID entidade", "Operação", "Campo", "Valor anterior", "Valor novo"};
            Row header = sheet.createRow(0);
            CellStyle estiloCabecalho = workbook.createCellStyle();
            Font fonte = workbook.createFont(); fonte.setBold(true); estiloCabecalho.setFont(fonte);
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i); cell.setCellValue(colunas[i]); cell.setCellStyle(estiloCabecalho);
            }
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            int linha = 1;
            for (AuditoriaAlteracao registro : registros) {
                Row row = sheet.createRow(linha++);
                row.createCell(0).setCellValue(registro.getOcorridoEm() == null ? "" : registro.getOcorridoEm().format(formato));
                row.createCell(1).setCellValue(texto(registro.getUsuario()));
                row.createCell(2).setCellValue(texto(registro.getEntidade()));
                row.createCell(3).setCellValue(texto(registro.getEntidadeId()));
                row.createCell(4).setCellValue(texto(registro.getOperacao()));
                row.createCell(5).setCellValue(texto(registro.getCampo()));
                row.createCell(6).setCellValue(texto(registro.getValorAnterior()));
                row.createCell(7).setCellValue(texto(registro.getValorNovo()));
            }
            for (int i = 0; i < colunas.length; i++) sheet.setColumnWidth(i, i == 6 || i == 7 ? 9000 : 5500);
            workbook.write(response.getOutputStream());
        }
    }

    private String texto(String valor) { return valor == null ? "" : valor; }
}
