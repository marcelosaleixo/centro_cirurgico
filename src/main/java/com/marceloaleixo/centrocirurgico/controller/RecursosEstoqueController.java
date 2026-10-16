package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.repository.SalaCirurgicaRepository;
import com.marceloaleixo.centrocirurgico.service.RecursosEstoqueService;
import com.marceloaleixo.centrocirurgico.web.ArmarioForm;
import com.marceloaleixo.centrocirurgico.web.CarrinhoForm;
import com.marceloaleixo.centrocirurgico.web.MinimoItemForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque/recursos")
public class RecursosEstoqueController {
    private final RecursosEstoqueService service;
    private final ItemEstoqueRepository itens;
    private final SalaCirurgicaRepository salas;
    public RecursosEstoqueController(RecursosEstoqueService service, ItemEstoqueRepository itens, SalaCirurgicaRepository salas) {
        this.service=service; this.itens=itens; this.salas=salas;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("carrinhos",service.listarCarrinhos());
        model.addAttribute("armarios",service.listarArmarios());
        return "estoque/recursos-lista";
    }
    @GetMapping("/carrinhos/novo")
    public String novoCarrinho(Model model) { model.addAttribute("form",new CarrinhoForm()); model.addAttribute("titulo","Novo carrinho de anestesia"); return "estoque/carrinho-formulario"; }
    @GetMapping("/carrinhos/{id}/editar")
    public String editarCarrinho(@PathVariable Long id,Model model) {
        CarrinhoAnestesista c=service.buscarCarrinho(id); CarrinhoForm f=new CarrinhoForm();
        f.setId(c.getId());f.setCodigo(c.getCodigo());f.setNome(c.getNome());f.setLocalizacao(c.getLocalizacao());
        model.addAttribute("form",f);model.addAttribute("titulo","Editar carrinho de anestesia");return "estoque/carrinho-formulario";
    }
    @PostMapping("/carrinhos/salvar")
    public String salvarCarrinho(@ModelAttribute("form") CarrinhoForm f,Model model,RedirectAttributes ra) {
        try {service.salvarCarrinho(f);ra.addFlashAttribute("sucesso","Carrinho salvo com sucesso.");return "redirect:/estoque/recursos";}
        catch(IllegalArgumentException ex){model.addAttribute("erro",ex.getMessage());model.addAttribute("titulo",f.getId()==null?"Novo carrinho de anestesia":"Editar carrinho de anestesia");return "estoque/carrinho-formulario";}
    }
    @PostMapping("/carrinhos/{id}/alternar")
    public String alternarCarrinho(@PathVariable Long id,RedirectAttributes ra){try{service.alternarCarrinho(id);ra.addFlashAttribute("sucesso","Status do carrinho atualizado.");}catch(RuntimeException e){ra.addFlashAttribute("erro",e.getMessage());}return "redirect:/estoque/recursos";}

    @GetMapping("/armarios/novo")
    public String novoArmario(Model model){model.addAttribute("form",new ArmarioForm());model.addAttribute("titulo","Novo armário cirúrgico");prepararSalas(model);return "estoque/armario-formulario";}
    @GetMapping("/armarios/{id}/editar")
    public String editarArmario(@PathVariable Long id,Model model){
        ArmarioSala a=service.buscarArmario(id);ArmarioForm f=new ArmarioForm();f.setId(a.getId());f.setSalaCirurgicaId(a.getSalaCirurgica().getId());f.setCodigo(a.getCodigo());f.setNome(a.getNome());
        model.addAttribute("form",f);model.addAttribute("titulo","Editar armário cirúrgico");prepararSalas(model);return "estoque/armario-formulario";
    }
    @PostMapping("/armarios/salvar")
    public String salvarArmario(@ModelAttribute("form") ArmarioForm f,Model model,RedirectAttributes ra){
        try{service.salvarArmario(f);ra.addFlashAttribute("sucesso","Armário salvo com sucesso.");return "redirect:/estoque/recursos";}
        catch(IllegalArgumentException ex){model.addAttribute("erro",ex.getMessage());model.addAttribute("titulo",f.getId()==null?"Novo armário cirúrgico":"Editar armário cirúrgico");prepararSalas(model);return "estoque/armario-formulario";}
    }
    @PostMapping("/armarios/{id}/alternar")
    public String alternarArmario(@PathVariable Long id,RedirectAttributes ra){try{service.alternarArmario(id);ra.addFlashAttribute("sucesso","Status do armário atualizado.");}catch(RuntimeException e){ra.addFlashAttribute("erro",e.getMessage());}return "redirect:/estoque/recursos";}

    @GetMapping("/carrinhos/{id}/itens")
    public String itensCarrinho(@PathVariable Long id,Model model){
        CarrinhoAnestesista c=service.buscarCarrinho(id);model.addAttribute("recurso",c);model.addAttribute("itensRecurso",service.itensCarrinho(id));
        model.addAttribute("form",new MinimoItemForm());model.addAttribute("itensDisponiveis",itens.findByAtivoTrueOrderByNomeAsc());model.addAttribute("tipoRecurso","carrinho");return "estoque/recursos-itens";
    }
    @PostMapping("/carrinhos/{id}/itens/minimo")
    public String minimoCarrinho(@PathVariable Long id,@ModelAttribute("form") MinimoItemForm f,RedirectAttributes ra){
        try{service.configurarMinimoCarrinho(id,f);ra.addFlashAttribute("sucesso","Item e quantidade mínima configurados. Use movimentação para abastecer.");}
        catch(RuntimeException e){ra.addFlashAttribute("erro",e.getMessage());}
        return "redirect:/estoque/recursos/carrinhos/"+id+"/itens";
    }
    @GetMapping("/armarios/{id}/itens")
    public String itensArmario(@PathVariable Long id,Model model){
        ArmarioSala a=service.buscarArmario(id);model.addAttribute("recurso",a);model.addAttribute("itensRecurso",service.itensArmario(id));
        model.addAttribute("form",new MinimoItemForm());model.addAttribute("itensDisponiveis",itens.findByAtivoTrueOrderByNomeAsc());model.addAttribute("tipoRecurso","armario");return "estoque/recursos-itens";
    }
    @PostMapping("/armarios/{id}/itens/minimo")
    public String minimoArmario(@PathVariable Long id,@ModelAttribute("form") MinimoItemForm f,RedirectAttributes ra){
        try{service.configurarMinimoArmario(id,f);ra.addFlashAttribute("sucesso","Item e quantidade mínima configurados. Use movimentação para abastecer.");}
        catch(RuntimeException e){ra.addFlashAttribute("erro",e.getMessage());}
        return "redirect:/estoque/recursos/armarios/"+id+"/itens";
    }
    private void prepararSalas(Model model){model.addAttribute("salas",salas.findByAtivaTrueOrderByNomeAsc());}
}
