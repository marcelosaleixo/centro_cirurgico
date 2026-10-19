package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.ItemConferenciaKitMutirao;
import com.marceloaleixo.centrocirurgico.entity.MontagemKitMutirao;
import com.marceloaleixo.centrocirurgico.repository.MontagemKitMutiraoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/mutiroes/montagens-kits")
public class ConferenciaKitMutiraoController {
    private final MontagemKitMutiraoRepository montagens;
    public ConferenciaKitMutiraoController(MontagemKitMutiraoRepository montagens) { this.montagens = montagens; }

    @GetMapping("/{montagemId}/conferir")
    public String conferir(@PathVariable Long montagemId, Model model, RedirectAttributes redirect) {
        MontagemKitMutirao montagem = montagens.findWithItensById(montagemId).orElse(null);
        if (montagem == null) { redirect.addFlashAttribute("erro", "Montagem não encontrada."); return "redirect:/mutiroes"; }
        model.addAttribute("montagem", montagem);
        long conferidos = montagem.getItens().stream().filter(ItemConferenciaKitMutirao::isConferido).count();
        model.addAttribute("conferidos", conferidos);
        model.addAttribute("totalItens", montagem.getItens().size());
        return "mutiroes/conferencia-kit";
    }

    @PostMapping("/{montagemId}/itens/{itemId}")
    public String salvarItem(@PathVariable Long montagemId, @PathVariable Long itemId,
            @RequestParam(required=false) BigDecimal quantidadeConferida,
            @RequestParam(defaultValue="false") boolean conferido,
            @RequestParam(required=false) String observacao, RedirectAttributes redirect) {
        MontagemKitMutirao montagem = montagens.findWithItensById(montagemId).orElse(null);
        if (montagem == null) { redirect.addFlashAttribute("erro", "Montagem não encontrada."); return "redirect:/mutiroes"; }
        ItemConferenciaKitMutirao item = montagem.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);
        if (item == null) { redirect.addFlashAttribute("erro", "Item não pertence a esta montagem."); return "redirect:/mutiroes/montagens-kits/"+montagemId+"/conferir"; }
        if ("CONCLUIDA".equals(montagem.getStatus())) { redirect.addFlashAttribute("erro", "A montagem já foi concluída. Reabra-a administrativamente antes de alterar itens."); return "redirect:/mutiroes/montagens-kits/"+montagemId+"/conferir"; }
        if (quantidadeConferida != null && quantidadeConferida.compareTo(BigDecimal.ZERO) < 0) { redirect.addFlashAttribute("erro", "A quantidade conferida não pode ser negativa."); return "redirect:/mutiroes/montagens-kits/"+montagemId+"/conferir"; }
        item.setQuantidadeConferida(quantidadeConferida);
        item.setConferido(conferido);
        item.setObservacao(observacao == null ? null : observacao.trim());
        montagem.setStatus("EM_CONFERENCIA");
        montagens.save(montagem);
        redirect.addFlashAttribute("sucesso", "Conferência do item salva. Nenhuma movimentação de estoque foi realizada.");
        return "redirect:/mutiroes/montagens-kits/"+montagemId+"/conferir";
    }

    @PostMapping("/{montagemId}/concluir")
    public String concluir(@PathVariable Long montagemId, RedirectAttributes redirect) {
        MontagemKitMutirao montagem = montagens.findWithItensById(montagemId).orElse(null);
        if (montagem == null) { redirect.addFlashAttribute("erro", "Montagem não encontrada."); return "redirect:/mutiroes"; }
        if (montagem.getItens().isEmpty() || montagem.getItens().stream().anyMatch(i -> !i.isConferido() || i.getQuantidadeConferida() == null)) {
            redirect.addFlashAttribute("erro", "Para concluir, confira todos os itens e informe a quantidade conferida.");
        } else if (montagem.getItens().stream().anyMatch(i -> i.getQuantidadeConferida().compareTo(i.getQuantidadePrevista()) != 0)) {
            montagem.setStatus("DIVERGENCIA"); montagens.save(montagem);
            redirect.addFlashAttribute("erro", "Há divergências entre quantidades previstas e conferidas. Revise os itens antes de concluir.");
        } else {
            montagem.setStatus("CONCLUIDA"); montagens.save(montagem);
            redirect.addFlashAttribute("sucesso", "Montagem concluída após conferência integral. Nenhuma baixa de estoque foi realizada.");
        }
        return "redirect:/mutiroes/montagens-kits/"+montagemId+"/conferir";
    }
}
