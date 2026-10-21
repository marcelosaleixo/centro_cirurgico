package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.OrcamentoMaterialProcedimento;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.repository.ProcedimentoMutiraoLookupRepository;
import com.marceloaleixo.centrocirurgico.repository.OrcamentoMaterialProcedimentoRepository;
import com.marceloaleixo.centrocirurgico.repository.ConsumoCirurgiaLoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/orcamentos/cirurgicos")
public class OrcamentoCirurgicoController {
 private final OrcamentoMaterialProcedimentoRepository repo; private final ProcedimentoMutiraoLookupRepository procedimentos; private final ItemEstoqueRepository itens; private final ConsumoCirurgiaLoteRepository consumos;
 public OrcamentoCirurgicoController(OrcamentoMaterialProcedimentoRepository repo, ProcedimentoMutiraoLookupRepository procedimentos, ItemEstoqueRepository itens, ConsumoCirurgiaLoteRepository consumos){this.repo=repo;this.procedimentos=procedimentos;this.itens=itens;this.consumos=consumos;}
 @GetMapping public String listar(Model model){model.addAttribute("orcamentos",repo.findAllByOrderByProcedimentoNomeAscItemNomeAsc());model.addAttribute("procedimentos",procedimentos.findByAtivoTrueOrderByNomeAsc());model.addAttribute("itens",itens.findAll());model.addAttribute("novo",new OrcamentoMaterialProcedimento());return "orcamentos/cirurgicos";}
 @PostMapping public String salvar(@RequestParam Long procedimentoId,@RequestParam Long itemId,@RequestParam BigDecimal quantidadePrevista,@RequestParam BigDecimal custoUnitarioPrevisto,@RequestParam(required=false) String observacao, RedirectAttributes ra){
  if(quantidadePrevista==null||quantidadePrevista.signum()<=0||custoUnitarioPrevisto==null||custoUnitarioPrevisto.signum()<0){ra.addFlashAttribute("erro","Informe quantidade prevista maior que zero e custo unitário não negativo.");return "redirect:/orcamentos/cirurgicos";}
  if(repo.existsByProcedimentoIdAndItemId(procedimentoId,itemId)){ra.addFlashAttribute("erro","Já existe orçamento para este material neste procedimento. Exclua o anterior antes de cadastrar novamente.");return "redirect:/orcamentos/cirurgicos";}
  OrcamentoMaterialProcedimento o=new OrcamentoMaterialProcedimento();o.setProcedimento(procedimentos.findById(procedimentoId).orElseThrow());o.setItem(itens.findById(itemId).orElseThrow());o.setQuantidadePrevista(quantidadePrevista);o.setCustoUnitarioPrevisto(custoUnitarioPrevisto);o.setObservacao(observacao);repo.save(o);ra.addFlashAttribute("sucesso","Orçamento de material salvo.");return "redirect:/orcamentos/cirurgicos";
 }
 @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id, RedirectAttributes ra){repo.deleteById(id);ra.addFlashAttribute("sucesso","Orçamento removido.");return "redirect:/orcamentos/cirurgicos";}
 @GetMapping("/comparativo") public String comparativo(Model model){
  var orcamentos=repo.findAllByOrderByProcedimentoNomeAscItemNomeAsc(); var registros=consumos.findTop300ByOrderByRegistradoEmDesc();
  var linhas=orcamentos.stream().map(o->{BigDecimal qtd=registros.stream().filter(c->c.getCirurgia().getProcedimento().getId().equals(o.getProcedimento().getId())&&c.getItem().getId().equals(o.getItem().getId())).map(c->c.getQuantidade()).reduce(BigDecimal.ZERO,BigDecimal::add);BigDecimal previsto=o.getQuantidadePrevista().multiply(o.getCustoUnitarioPrevisto());BigDecimal real=registros.stream().filter(c->c.getCirurgia().getProcedimento().getId().equals(o.getProcedimento().getId())&&c.getItem().getId().equals(o.getItem().getId())).filter(c->c.getLote().getCustoUnitario()!=null).map(c->c.getQuantidade().multiply(c.getLote().getCustoUnitario())).reduce(BigDecimal.ZERO,BigDecimal::add);return new LinhaComparativo(o,qtd,previsto,real,real.subtract(previsto));}).toList();
  model.addAttribute("linhas",linhas);return "orcamentos/comparativo";
 }
 public record LinhaComparativo(OrcamentoMaterialProcedimento orcamento, BigDecimal quantidadeConsumida, BigDecimal custoPrevisto, BigDecimal custoReal, BigDecimal desvio){}
}
