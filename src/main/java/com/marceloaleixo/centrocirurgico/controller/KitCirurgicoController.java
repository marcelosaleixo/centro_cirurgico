package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.KitCirurgico;
import com.marceloaleixo.centrocirurgico.entity.ItemKitCirurgico;
import com.marceloaleixo.centrocirurgico.repository.KitCirurgicoRepository;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.Set;

@Controller @RequestMapping("/mutiroes/kits")
public class KitCirurgicoController {
 private final KitCirurgicoRepository kits; private final ItemEstoqueRepository estoque;
 public KitCirurgicoController(KitCirurgicoRepository kits,ItemEstoqueRepository estoque){this.kits=kits;this.estoque=estoque;}
 @GetMapping public String listar(Model model){model.addAttribute("kits",kits.findAllByOrderByNomeAsc());return "mutiroes/kits-lista";}
 @GetMapping("/novo") public String novo(Model model){model.addAttribute("kit",new KitCirurgico());model.addAttribute("edicao",false);preparar(model);return "mutiroes/kits-formulario";}
 @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model model,RedirectAttributes r){KitCirurgico k=kits.findWithItensById(id).orElse(null);if(k==null){r.addFlashAttribute("erro","Kit não encontrado.");return "redirect:/mutiroes/kits";}model.addAttribute("kit",k);model.addAttribute("edicao",true);preparar(model);return "mutiroes/kits-formulario";}
 private void preparar(Model m){m.addAttribute("itensEstoque",estoque.findByAtivoTrueOrderByNomeAsc());m.addAttribute("categorias",Set.of("MATERIAL","FIO","MEDICAMENTO","OUTRO"));}
 @PostMapping("/salvar") public String salvar(@ModelAttribute KitCirurgico kit,@RequestParam(name="itemDescricao",required=false) java.util.List<String> descricoes,@RequestParam(name="itemCategoria",required=false) java.util.List<String> categorias,@RequestParam(name="itemQuantidade",required=false) java.util.List<String> quantidades,@RequestParam(name="itemUnidade",required=false) java.util.List<String> unidades,@RequestParam(name="itemEstoqueId",required=false) java.util.List<String> estoqueIds,RedirectAttributes r){
  if(kit.getNome()==null||kit.getNome().isBlank()||kit.getEspecialidade()==null||kit.getEspecialidade().isBlank()){r.addFlashAttribute("erro","Informe o nome e a especialidade do kit.");return "redirect:/mutiroes/kits/novo";}
  kit.setNome(kit.getNome().trim());kit.setEspecialidade(kit.getEspecialidade().trim());kit.getItens().clear();
  if(descricoes!=null)for(int i=0;i<descricoes.size();i++){String d=descricoes.get(i)==null?"":descricoes.get(i).trim();if(d.isBlank())continue;String cat=at(categorias,i,"MATERIAL");if(!Set.of("MATERIAL","FIO","MEDICAMENTO","OUTRO").contains(cat))cat="MATERIAL";BigDecimal qtd;try{qtd=new BigDecimal(at(quantidades,i,"1").replace(',','.'));}catch(Exception ex){qtd=BigDecimal.ZERO;}if(qtd.signum()<=0){r.addFlashAttribute("erro","As quantidades dos itens precisam ser maiores que zero.");return kit.getId()==null?"redirect:/mutiroes/kits/novo":"redirect:/mutiroes/kits/"+kit.getId()+"/editar";}ItemKitCirurgico item=new ItemKitCirurgico();item.setKit(kit);item.setDescricao(d);item.setCategoria(cat);item.setQuantidade(qtd);item.setUnidade(at(unidades,i,"UNIDADE"));String eid=at(estoqueIds,i,"");if(!eid.isBlank())try{item.setItemEstoque(estoque.findById(Long.valueOf(eid)).orElse(null));}catch(NumberFormatException ignored){}kit.getItens().add(item);}
  kits.save(kit);r.addFlashAttribute("sucesso","Modelo de kit salvo. Nenhuma baixa de estoque foi realizada.");return "redirect:/mutiroes/kits";
 }
 private String at(java.util.List<String> l,int i,String d){return l!=null&&i<l.size()&&l.get(i)!=null?l.get(i):d;}
 @PostMapping("/{id}/ativar") public String alternar(@PathVariable Long id,RedirectAttributes r){KitCirurgico k=kits.findById(id).orElse(null);if(k==null){r.addFlashAttribute("erro","Kit não encontrado.");}else{k.setAtivo(!k.isAtivo());kits.save(k);r.addFlashAttribute("sucesso",k.isAtivo()?"Kit ativado.":"Kit desativado.");}return "redirect:/mutiroes/kits";}
}
