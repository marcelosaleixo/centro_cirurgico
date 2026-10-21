package com.marceloaleixo.centrocirurgico.controller;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoCompra;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.service.SolicitacaoCompraService;
import com.marceloaleixo.centrocirurgico.service.RecebimentoCompraService;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
@Controller @RequestMapping("/compras/solicitacoes")
public class SolicitacaoCompraController {
 private final SolicitacaoCompraService service; private final ItemEstoqueRepository itemRepository; private final RecebimentoCompraService recebimentoService;
 public SolicitacaoCompraController(SolicitacaoCompraService s, ItemEstoqueRepository i, RecebimentoCompraService r){service=s;itemRepository=i;recebimentoService=r;}
 @GetMapping public String listar(Model model){model.addAttribute("solicitacoes",service.listar());return "compras/solicitacoes-lista";}
 @GetMapping("/nova") public String nova(Model model){model.addAttribute("itens",itemRepository.findByAtivoTrueOrderByNomeAsc());return "compras/solicitacao-formulario";}
 @PostMapping public String criar(@RequestParam Long itemId,@RequestParam BigDecimal quantidadeSolicitada,@RequestParam String justificativa,@RequestParam(required=false) String fornecedorSugerido,@RequestParam String responsavelSolicitacao,@RequestParam(required=false) String observacao,RedirectAttributes ra){try{service.criar(itemId,quantidadeSolicitada,justificativa,fornecedorSugerido,responsavelSolicitacao,observacao);ra.addFlashAttribute("sucesso","Solicitação de compra registrada para aprovação.");return "redirect:/compras/solicitacoes";}catch(IllegalArgumentException e){ra.addFlashAttribute("erro",e.getMessage());return "redirect:/compras/solicitacoes/nova";}}
 @PostMapping("/{id}/status") public String status(@PathVariable Long id,@RequestParam SolicitacaoCompra.StatusCompra status,RedirectAttributes ra){try{service.atualizarStatus(id,status);ra.addFlashAttribute("sucesso","Status da solicitação atualizado.");}catch(RuntimeException e){ra.addFlashAttribute("erro",e.getMessage());}return "redirect:/compras/solicitacoes";}

 @PostMapping("/{id}/receber")
 public String receber(@PathVariable Long id, @RequestParam BigDecimal quantidadeRecebida,
                       @RequestParam String responsavelRecebimento, @RequestParam String numeroLote, @RequestParam LocalDate dataValidade, @RequestParam(required=false) BigDecimal custoUnitario, RedirectAttributes ra) {
  try {
   recebimentoService.receber(id, quantidadeRecebida, responsavelRecebimento, numeroLote, dataValidade, custoUnitario == null ? BigDecimal.ZERO : custoUnitario);
   ra.addFlashAttribute("sucesso", "Recebimento registrado e saldo do estoque atualizado.");
  } catch (RuntimeException e) {
   ra.addFlashAttribute("erro", e.getMessage());
  }
  return "redirect:/compras/solicitacoes";
 }
}
