package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ConsumoCirurgiaLote;
import com.marceloaleixo.centrocirurgico.repository.CirurgiaRepository;
import com.marceloaleixo.centrocirurgico.repository.ConsumoCirurgiaLoteRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Controller
@RequestMapping("/relatorios/consumo-cirurgico")
public class RelatorioConsumoCirurgicoController {
    private final ConsumoCirurgiaLoteRepository consumoRepository;
    private final CirurgiaRepository cirurgiaRepository;
    public RelatorioConsumoCirurgicoController(ConsumoCirurgiaLoteRepository consumoRepository, CirurgiaRepository cirurgiaRepository) {
        this.consumoRepository = consumoRepository; this.cirurgiaRepository = cirurgiaRepository;
    }
    @GetMapping
    public String relatorio(@RequestParam(required=false) Long cirurgiaId,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim, Model model) {
        validarPeriodo(inicio, fim);
        List<ConsumoCirurgiaLote> registros = buscar(cirurgiaId, inicio, fim);
        BigDecimal total = registros.stream().map(ConsumoCirurgiaLote::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("cirurgias", cirurgiaRepository.findTop200ByOrderByDataCirurgiaDescHoraInicioDesc());
        model.addAttribute("registros", registros); model.addAttribute("cirurgiaId", cirurgiaId);
        model.addAttribute("inicio", inicio); model.addAttribute("fim", fim);
        model.addAttribute("totalQuantidade", total); model.addAttribute("totalRegistros", registros.size());
        boolean custoDisponivel = !registros.isEmpty() && registros.stream().allMatch(c -> c.getLote().getCustoUnitario() != null);
        BigDecimal custoTotal = custoDisponivel ? registros.stream()
                .map(c -> c.getQuantidade().multiply(c.getLote().getCustoUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        model.addAttribute("custoDisponivel", custoDisponivel);
        model.addAttribute("custoTotal", custoTotal);
        return "relatorios/consumo-cirurgico";
    }
    @GetMapping("/exportar.csv")
    public void exportar(@RequestParam(required=false) Long cirurgiaId,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim, HttpServletResponse response) throws IOException {
        validarPeriodo(inicio, fim); List<ConsumoCirurgiaLote> registros = buscar(cirurgiaId, inicio, fim);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-consumo-cirurgico.csv");
        var out = response.getWriter(); out.write("\uFEFFData registro;Cirurgia;Data cirurgia;Paciente;Procedimento;Material;Codigo material;Lote;Validade;Quantidade;Unidade;Custo unitario;Custo total;Observacao\r\n");
        for (ConsumoCirurgiaLote c : registros) {
            linha(out, c.getRegistradoEm()==null?"":c.getRegistradoEm().toString(), String.valueOf(c.getCirurgia().getId()), String.valueOf(c.getCirurgia().getDataCirurgia()),
                c.getCirurgia().getPaciente().getNome(), c.getCirurgia().getProcedimento().getNome(), c.getItem().getNome(), c.getItem().getCodigo(), c.getLote().getNumeroLote(),
                String.valueOf(c.getLote().getDataValidade()), c.getQuantidade().toPlainString(), c.getItem().getUnidadeMedida(),
                c.getLote().getCustoUnitario()==null?"":c.getLote().getCustoUnitario().toPlainString(),
                c.getLote().getCustoUnitario()==null?"":c.getQuantidade().multiply(c.getLote().getCustoUnitario()).toPlainString(), c.getObservacao());
        } out.flush();
    }
    private List<ConsumoCirurgiaLote> buscar(Long cirurgiaId, LocalDate inicio, LocalDate fim) {
        OffsetDateTime de = inicio == null ? null : inicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime ate = fim == null ? null : fim.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return consumoRepository.relatorio(cirurgiaId, de, ate);
    }
    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
    }
    private void linha(java.io.Writer out, String... campos) throws IOException {
        for (int i=0;i<campos.length;i++) { if(i>0) out.write(";"); String s=campos[i]==null?"":campos[i]; out.write("\""+s.replace("\"","\"\"")+"\""); } out.write("\r\n");
    }
}
