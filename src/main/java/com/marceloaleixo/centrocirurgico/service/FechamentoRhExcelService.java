package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.web.ResumoFechamentoRh;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FechamentoRhExcelService {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final FechamentoRhService fechamentoRhService;

    public FechamentoRhExcelService(FechamentoRhService fechamentoRhService) {
        this.fechamentoRhService = fechamentoRhService;
    }

    public byte[] gerar(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe a data inicial e a data final.");
        }
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }

        List<ResumoFechamentoRh> resumos = fechamentoRhService.consultar(inicio, fim);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Fechamento RH");
            sheet.createFreezePane(0, 4);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(3, Math.max(3, resumos.size() + 3), 0, 11));

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setWrapText(true);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("FECHAMENTO DE ESCALAS — RECURSOS HUMANOS");
            title.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 11));
            Row period = sheet.createRow(1);
            period.createCell(0).setCellValue("Período: " + inicio.format(DATA) + " a " + fim.format(DATA));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 11));
            Row note = sheet.createRow(2);
            note.createCell(0).setCellValue("Relatório para conferência operacional. Validar regras do RH antes do uso na folha de pagamento.");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, 11));

            String[] headers = {"Colaborador", "Função", "Equipe", "Plantões programados", "Plantões realizados", "Horas previstas", "Horas realizadas", "Dias de férias", "Dias de banco de horas", "Cancelados", "Finais de semana", "Feriados identificados"};
            Row header = sheet.createRow(3);
            header.setHeightInPoints(32);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 4;
            for (ResumoFechamentoRh resumo : resumos) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(resumo.getColaborador() != null ? safe(resumo.getColaborador().getNome()) : "-");
                row.createCell(1).setCellValue(resumo.getColaborador() != null && resumo.getColaborador().getFuncao() != null ? safe(resumo.getColaborador().getFuncao().getNome()) : "-");
                row.createCell(2).setCellValue(resumo.getColaborador() != null && resumo.getColaborador().getEquipe() != null ? safe(resumo.getColaborador().getEquipe().getNome()) : "-");
                row.createCell(3).setCellValue(resumo.getPlantoesProgramados());
                row.createCell(4).setCellValue(resumo.getPlantoesRealizados());
                row.createCell(5).setCellValue(resumo.getHorasPrevistasFormatadas());
                row.createCell(6).setCellValue(resumo.getHorasRealizadasFormatadas());
                row.createCell(7).setCellValue(resumo.getFerias());
                row.createCell(8).setCellValue(resumo.getBancoHoras());
                row.createCell(9).setCellValue(resumo.getCancelados());
                row.createCell(10).setCellValue(resumo.getFinaisDeSemana());
                row.createCell(11).setCellValue(resumo.getFeriadosIdentificados());
            }

            int[] widths = {30, 22, 20, 20, 20, 16, 16, 14, 20, 12, 18, 22};
            for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gerar o arquivo Excel do fechamento do RH.", e);
        }
    }

    private String safe(String value) { return value == null || value.isBlank() ? "-" : value; }
}
