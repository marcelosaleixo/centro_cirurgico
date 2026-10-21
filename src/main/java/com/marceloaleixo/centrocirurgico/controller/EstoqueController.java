package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;
import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.service.EstoqueService;
import com.marceloaleixo.centrocirurgico.web.ItemEstoqueForm;
import com.marceloaleixo.centrocirurgico.web.MovimentacaoEstoqueForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {
    private final EstoqueService service;
    private final CarrinhoAnestesistaRepository carrinhoRepository;
    private final ArmarioSalaRepository armarioRepository;
    private final SalaCirurgicaRepository salaRepository;
    private final ColaboradorRepository colaboradorRepository;

    public EstoqueController(EstoqueService service, CarrinhoAnestesistaRepository carrinhoRepository,
                             ArmarioSalaRepository armarioRepository, SalaCirurgicaRepository salaRepository,
                             ColaboradorRepository colaboradorRepository) {
        this.service = service; this.carrinhoRepository = carrinhoRepository;
        this.armarioRepository = armarioRepository; this.salaRepository = salaRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("itens", service.listarItens());
        model.addAttribute("movimentacoes", service.listarMovimentacoes());
        model.addAttribute("carrinhos", carrinhoRepository.findAllByOrderByNomeAsc());
        model.addAttribute("armarios", armarioRepository.findAllByOrderBySalaCirurgicaNomeAscCodigoAsc());
        return "estoque/painel";
    }

    @GetMapping("/baixa-fefo")
    public String formularioBaixaFefo(Model model) {
        model.addAttribute("itens", service.listarItens().stream().filter(ItemEstoque::isAtivo).toList());
        model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
        return "estoque/baixa-fefo";
    }

    @PostMapping("/baixa-fefo")
    public String salvarBaixaFefo(@RequestParam Long itemId, @RequestParam java.math.BigDecimal quantidade,
                                  @RequestParam(required=false) Long colaboradorId, @RequestParam String motivo,
                                  Model model, RedirectAttributes ra) {
        try {
            service.registrarBaixaFefo(itemId, quantidade, colaboradorId, motivo);
            ra.addFlashAttribute("sucesso", "Baixa registrada por FEFO; lotes válidos e saldo geral atualizados.");
            return "redirect:/estoque";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("itens", service.listarItens().stream().filter(ItemEstoque::isAtivo).toList());
            model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
            return "estoque/baixa-fefo";
        }
    }

    @GetMapping("/itens/novo")
    public String novoItem(Model model) {
        model.addAttribute("form", new ItemEstoqueForm());
        model.addAttribute("titulo", "Novo item de estoque");
        return "estoque/item-formulario";
    }

    @GetMapping("/itens/{id}/editar")
    public String editarItem(@PathVariable Long id, Model model) {
        ItemEstoque item = service.buscarItem(id);
        ItemEstoqueForm form = new ItemEstoqueForm();
        form.setId(item.getId()); form.setCodigo(item.getCodigo()); form.setNome(item.getNome());
        form.setUnidadeMedida(item.getUnidadeMedida()); form.setEstoqueMinimo(item.getEstoqueMinimo());
        form.setQuantidadeInicial(item.getEstoqueGeral() == null ? java.math.BigDecimal.ZERO : item.getEstoqueGeral().getQuantidadeAtual());
        model.addAttribute("form", form);
        model.addAttribute("titulo", "Editar item de estoque");
        return "estoque/item-formulario";
    }

    @PostMapping("/itens/salvar")
    public String salvarItem(@ModelAttribute("form") ItemEstoqueForm form, Model model, RedirectAttributes ra) {
        try {
            service.salvarItem(form);
            ra.addFlashAttribute("sucesso", "Item de estoque salvo com sucesso.");
            return "redirect:/estoque";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("titulo", form.getId() == null ? "Novo item de estoque" : "Editar item de estoque");
            return "estoque/item-formulario";
        }
    }

    @PostMapping("/itens/{id}/desativar")
    public String desativarItem(@PathVariable Long id, RedirectAttributes ra) {
        try { service.desativarItem(id); ra.addFlashAttribute("sucesso", "Item desativado."); }
        catch (RuntimeException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
        return "redirect:/estoque";
    }

    @GetMapping("/movimentacoes/nova")
    public String novaMovimentacao(Model model) {
        MovimentacaoEstoqueForm form = new MovimentacaoEstoqueForm();
        model.addAttribute("form", form);
        prepararMovimentacao(model);
        return "estoque/movimentacao-formulario";
    }

    @PostMapping("/movimentacoes/salvar")
    public String salvarMovimentacao(@ModelAttribute("form") MovimentacaoEstoqueForm form, Model model, RedirectAttributes ra) {
        try {
            service.registrarMovimentacao(form);
            ra.addFlashAttribute("sucesso", "Movimentação registrada e saldos atualizados.");
            return "redirect:/estoque";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            prepararMovimentacao(model);
            return "estoque/movimentacao-formulario";
        }
    }

    private void prepararMovimentacao(Model model) {
        model.addAttribute("itens", service.listarItens().stream().filter(ItemEstoque::isAtivo).toList());
        model.addAttribute("carrinhos", carrinhoRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("armarios", armarioRepository.findByAtivoTrueOrderByCodigoAsc());
        model.addAttribute("salas", salaRepository.findByAtivaTrueOrderByNomeAsc());
        model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("tipos", TipoMovimentacao.values());
        model.addAttribute("localizacoes", LocalizacaoEstoque.values());
    }
}
