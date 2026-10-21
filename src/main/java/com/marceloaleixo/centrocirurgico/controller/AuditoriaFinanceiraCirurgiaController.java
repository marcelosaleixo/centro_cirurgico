package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.Cirurgia;
import com.marceloaleixo.centrocirurgico.entity.ConsumoCirurgiaLote;
import com.marceloaleixo.centrocirurgico.entity.OrcamentoMaterialProcedimento;
import com.marceloaleixo.centrocirurgico.repository.CirurgiaRepository;
import com.marceloaleixo.centrocirurgico.repository.ConsumoCirurgiaLoteRepository;
import com.marceloaleixo.centrocirurgico.repository.OrcamentoMaterialProcedimentoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/relatorios/auditoria-financeira-cirurgia")
public class AuditoriaFinanceiraCirurgiaController {
    private final CirurgiaRepository cirurgias;
    private final ConsumoCirurgiaLoteRepository consumos;
    private final OrcamentoMaterialProcedimentoRepository orcamentos;

    public AuditoriaFinanceiraCirurgiaController(CirurgiaRepository cirurgias, ConsumoCirurgiaLoteRepository consumos,
                                                  OrcamentoMaterialProcedimentoRepository orcamentos) {
        this.cirurgias = cirurgias; this.consumos = consumos; this.orcamentos = orcamentos;
    }

    @GetMapping
    public String consultar(@RequestParam(required=false) Long cirurgiaId, Model model) {
        model.addAttribute("cirurgias", cirurgias.findTop200ByOrderByDataCirurgiaDescHoraInicioDesc());
        model.addAttribute("cirurgiaId", cirurgiaId);
        if (cirurgiaId == null) {
            model.addAttribute("linhas", List.of());
            model.addAttribute("selecionada", null);
            model.addAttribute("custoTotal", BigDecimal.ZERO);
            model.addAttribute("orcamentoTotal", BigDecimal.ZERO);
            model.addAttribute("desvioTotal", BigDecimal.ZERO);
            model.addAttribute("custosCompletos", false);
            model.addAttribute("temOrcamento", false);
            return "relatorios/auditoria-financeira-cirurgia";
        }
        Cirurgia cirurgia = cirurgias.findById(cirurgiaId).orElseThrow(() -> new IllegalArgumentException("Cirurgia não encontrada."));
        List<ConsumoCirurgiaLote> registros = consumos.findByCirurgiaIdOrderByRegistradoEmDesc(cirurgiaId);
        Map<Long, BigDecimal> quantidades = registros.stream().collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.mapping(ConsumoCirurgiaLote::getQuantidade, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<Long, BigDecimal> custos = new HashMap<>();
        boolean custosCompletos = !registros.isEmpty() && registros.stream().allMatch(c -> c.getLote().getCustoUnitario() != null);
        for (ConsumoCirurgiaLote c : registros) {
            if (c.getLote().getCustoUnitario() != null) custos.merge(c.getItem().getId(), c.getQuantidade().multiply(c.getLote().getCustoUnitario()), BigDecimal::add);
        }
        List<OrcamentoMaterialProcedimento> orcamento = orcamentos.findAllByOrderByProcedimentoNomeAscItemNomeAsc().stream()
                .filter(o -> o.getProcedimento().getId().equals(cirurgia.getProcedimento().getId())).toList();
        Map<Long, OrcamentoMaterialProcedimento> porItem = orcamento.stream().collect(Collectors.toMap(o -> o.getItem().getId(), o -> o, (a,b) -> a));
        Set<Long> ids = new LinkedHashSet<>();
        orcamento.forEach(o -> ids.add(o.getItem().getId()));
        registros.forEach(c -> ids.add(c.getItem().getId()));
        List<LinhaAuditoria> linhas = new ArrayList<>();
        BigDecimal custoTotal = BigDecimal.ZERO, orcamentoTotal = BigDecimal.ZERO;
        for (Long itemId : ids) {
            OrcamentoMaterialProcedimento o = porItem.get(itemId);
            ConsumoCirurgiaLote c = registros.stream().filter(x -> x.getItem().getId().equals(itemId)).findFirst().orElse(null);
            var item = o != null ? o.getItem() : c.getItem();
            BigDecimal previsto = o == null ? null : o.getQuantidadePrevista().multiply(o.getCustoUnitarioPrevisto());
            BigDecimal quantidadePrevista = o == null ? null : o.getQuantidadePrevista();
            BigDecimal quantidadeReal = quantidades.getOrDefault(itemId, BigDecimal.ZERO);
            BigDecimal custoReal = custos.getOrDefault(itemId, BigDecimal.ZERO);
            BigDecimal desvio = previsto == null || !custosCompletos ? null : custoReal.subtract(previsto);
            if (previsto != null) orcamentoTotal = orcamentoTotal.add(previsto);
            custoTotal = custoTotal.add(custoReal);
            linhas.add(new LinhaAuditoria(item.getNome(), item.getCodigo(), item.getUnidadeMedida(), quantidadePrevista,
                    quantidadeReal, previsto, custosCompletos ? custoReal : null, desvio,
                    o == null ? "Sem orçamento cadastrado" : "Orçado"));
        }
        model.addAttribute("selecionada", cirurgia); model.addAttribute("registros", registros); model.addAttribute("linhas", linhas);
        model.addAttribute("custoTotal", custoTotal); model.addAttribute("orcamentoTotal", orcamentoTotal);
        model.addAttribute("desvioTotal", custoTotal.subtract(orcamentoTotal)); model.addAttribute("custosCompletos", custosCompletos);
        model.addAttribute("temOrcamento", !orcamento.isEmpty()); model.addAttribute("totalRegistros", registros.size());
        return "relatorios/auditoria-financeira-cirurgia";
    }

    @GetMapping("/exportar.csv")
    public void exportar(@RequestParam Long cirurgiaId, HttpServletResponse response) throws IOException {
        Cirurgia cirurgia = cirurgias.findById(cirurgiaId).orElseThrow(() -> new IllegalArgumentException("Cirurgia não encontrada."));
        List<ConsumoCirurgiaLote> registros = consumos.findByCirurgiaIdOrderByRegistradoEmDesc(cirurgiaId);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=auditoria-financeira-cirurgia-" + cirurgiaId + ".csv");
        var out = response.getWriter(); out.write("\uFEFFCirurgia;Data;Paciente;Procedimento;Material;Lote;Validade;Quantidade;Custo unitario;Custo total;Observacao\r\n");
        for (ConsumoCirurgiaLote c : registros) {
            linha(out, String.valueOf(cirurgia.getId()), String.valueOf(cirurgia.getDataCirurgia()), cirurgia.getPaciente().getNome(), cirurgia.getProcedimento().getNome(), c.getItem().getNome(), c.getLote().getNumeroLote(), String.valueOf(c.getLote().getDataValidade()), c.getQuantidade().toPlainString(), c.getLote().getCustoUnitario()==null?"":c.getLote().getCustoUnitario().toPlainString(), c.getLote().getCustoUnitario()==null?"":c.getQuantidade().multiply(c.getLote().getCustoUnitario()).toPlainString(), c.getObservacao());
        }
        out.flush();
    }
    private void linha(java.io.Writer out, String... campos) throws IOException { for(int i=0;i<campos.length;i++){if(i>0)out.write(";");String s=campos[i]==null?"":campos[i];out.write("\""+s.replace("\"","\"\"")+"\"");}out.write("\r\n"); }
    public record LinhaAuditoria(String material, String codigo, String unidade, BigDecimal quantidadePrevista, BigDecimal quantidadeReal,
                                 BigDecimal custoPrevisto, BigDecimal custoReal, BigDecimal desvio, String status) {}
}
