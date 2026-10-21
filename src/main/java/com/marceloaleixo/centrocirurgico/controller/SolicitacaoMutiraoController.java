package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.*;

@Controller @RequestMapping("/mutiroes/solicitacoes")
public class SolicitacaoMutiraoController {
 private final SolicitacaoMutiraoRepository solicitacoes; private final DiaMutiraoRepository dias; private final ItemEstoqueRepository estoque;
 private final HistoricoSolicitacaoMutiraoRepository historico; private final AuditoriaItemSolicitacaoMutiraoRepository auditoriaItens;
 public SolicitacaoMutiraoController(SolicitacaoMutiraoRepository s, DiaMutiraoRepository d, ItemEstoqueRepository e, HistoricoSolicitacaoMutiraoRepository h, AuditoriaItemSolicitacaoMutiraoRepository ai){solicitacoes=s;dias=d;estoque=e;historico=h;auditoriaItens=ai;}
 private void registrarHistorico(Long id, String acao, String descricao, String responsavel, String anterior, String novo) {
  HistoricoSolicitacaoMutirao h = new HistoricoSolicitacaoMutirao();
  h.setSolicitacaoId(id); h.setAcao(acao); h.setDescricao(descricao);
  h.setResponsavel(responsavel == null || responsavel.isBlank() ? null : responsavel.trim());
  h.setStatusAnterior(anterior); h.setStatusNovo(novo); h.setOcorridoEm(LocalDateTime.now());
  historico.save(h);
 }
 @GetMapping public String listar(@RequestParam(required=false) String status, @RequestParam(required=false) String busca, Model model){
  List<SolicitacaoMutirao> todas=solicitacoes.findAllByOrderByCriadoEmDesc();
  String filtroStatus=status==null?"":status.trim().toUpperCase(Locale.ROOT);
  String termo=busca==null?"":busca.trim().toLowerCase(Locale.ROOT);
  List<SolicitacaoMutirao> filtradas=todas.stream()
   .filter(s->filtroStatus.isBlank()||s.getStatus().equals(filtroStatus))
   .filter(s->termo.isBlank()||String.valueOf(s.getId()).contains(termo)||
      (s.getSolicitante()!=null&&s.getSolicitante().toLowerCase(Locale.ROOT).contains(termo))||
      (s.getDia()!=null&&s.getDia().getEvento()!=null&&s.getDia().getEvento().getNome()!=null&&s.getDia().getEvento().getNome().toLowerCase(Locale.ROOT).contains(termo)))
   .toList();
  model.addAttribute("solicitacoes",filtradas); model.addAttribute("totalSolicitacoes",todas.size());
  model.addAttribute("rascunhos",todas.stream().filter(x->"RASCUNHO".equals(x.getStatus())).count());
  model.addAttribute("emSeparacao",todas.stream().filter(x->"EM_SEPARACAO".equals(x.getStatus())).count());
  model.addAttribute("pendentes",todas.stream().filter(x->!"ENTREGUE".equals(x.getStatus())).count());
  model.addAttribute("filtroStatus",filtroStatus); model.addAttribute("busca",busca==null?"":busca);
  return "mutiroes/solicitacoes-lista";
 }
 @GetMapping("/nova") public String nova(@RequestParam(required=false) Long diaId,Model model,RedirectAttributes r){SolicitacaoMutirao s=new SolicitacaoMutirao(); if(diaId!=null){DiaMutirao d=dias.findById(diaId).orElse(null);if(d==null){r.addFlashAttribute("erro","Dia do mutirão não encontrado.");return "redirect:/mutiroes";}s.setDia(d);}model.addAttribute("solicitacao",s);model.addAttribute("dias",dias.findAllWithEventoByOrderByDataDesc());model.addAttribute("itensEstoque",estoque.findByAtivoTrueOrderByNomeAsc());return "mutiroes/solicitacao-formulario";}
 @PostMapping("/salvar") public String salvar(@RequestParam(required=false) Long id,@RequestParam Long diaId,@RequestParam String solicitante,@RequestParam(required=false) String observacoes,@RequestParam(required=false) List<Long> itemEstoqueId,@RequestParam(required=false) List<String> itemDescricao,@RequestParam(required=false) List<String> itemCategoria,@RequestParam(required=false) List<String> itemQuantidade,@RequestParam(required=false) List<String> itemUnidade,RedirectAttributes r){
  DiaMutirao dia=dias.findById(diaId).orElse(null); if(dia==null||solicitante==null||solicitante.isBlank()){r.addFlashAttribute("erro","Informe o dia do mutirão e o nome do solicitante.");return "redirect:/mutiroes/solicitacoes/nova";}
  SolicitacaoMutirao s=id==null?new SolicitacaoMutirao():solicitacoes.findWithItensById(id).orElse(null); if(s==null){r.addFlashAttribute("erro","Solicitação não encontrada.");return "redirect:/mutiroes/solicitacoes";} if(!s.getStatus().equals("RASCUNHO")){r.addFlashAttribute("erro","Somente solicitações em rascunho podem ser editadas.");return "redirect:/mutiroes/solicitacoes";}
  s.setDia(dia);s.setSolicitante(solicitante.trim());s.setObservacoes(observacoes);s.getItens().clear();
  int n=itemDescricao==null?0:itemDescricao.size(); for(int i=0;i<n;i++){String desc=itemDescricao.get(i)==null?"":itemDescricao.get(i).trim(); if(desc.isEmpty())continue; BigDecimal q;try{q=new BigDecimal(itemQuantidade.get(i));}catch(Exception ex){continue;}if(q.compareTo(BigDecimal.ZERO)<=0)continue;ItemSolicitacaoMutirao it=new ItemSolicitacaoMutirao();it.setSolicitacao(s);Long eid=itemEstoqueId!=null&&i<itemEstoqueId.size()?itemEstoqueId.get(i):null;if(eid!=null){ItemEstoque ie=estoque.findById(eid).orElse(null);if(ie!=null){it.setItemEstoque(ie);it.setDescricao(ie.getNome());it.setUnidade(ie.getUnidadeMedida());}else{it.setDescricao(desc);}}else{it.setDescricao(desc);it.setUnidade(itemUnidade!=null&&i<itemUnidade.size()&&itemUnidade.get(i)!=null&&!itemUnidade.get(i).isBlank()?itemUnidade.get(i):"UNIDADE");}String cat=itemCategoria!=null&&i<itemCategoria.size()?itemCategoria.get(i):"MATERIAL";it.setCategoria(Set.of("MATERIAL","MEDICAMENTO","FIO","OUTRO").contains(cat)?cat:"MATERIAL");it.setQuantidadeSolicitada(q);s.getItens().add(it);}
  if(s.getItens().isEmpty()){r.addFlashAttribute("erro","Adicione pelo menos um item com quantidade válida.");return "redirect:/mutiroes/solicitacoes/nova?diaId="+diaId;}boolean novaSolicitacao = id == null;
  solicitacoes.save(s);
  registrarHistorico(s.getId(), novaSolicitacao ? "CRIACAO" : "EDICAO", novaSolicitacao ? "Solicitação criada como rascunho." : "Solicitação em rascunho editada.", s.getSolicitante(), null, s.getStatus());
  r.addFlashAttribute("sucesso","Solicitação salva como rascunho. Nenhuma movimentação de estoque foi realizada.");return "redirect:/mutiroes/solicitacoes";
 }
 @PostMapping("/{id}/separacao") public String salvarSeparacao(@PathVariable Long id, @RequestParam(required=false) List<Long> itemId, @RequestParam(required=false) List<String> quantidadeSeparada, @RequestParam String responsavel, RedirectAttributes r) {
  SolicitacaoMutirao s=solicitacoes.findWithItensById(id).orElse(null);
  if(s==null){r.addFlashAttribute("erro","Solicitação não encontrada.");return "redirect:/mutiroes/solicitacoes";}
  if(!"EM_SEPARACAO".equals(s.getStatus())){r.addFlashAttribute("erro","A conferência só pode ser atualizada durante a separação.");return "redirect:/mutiroes/solicitacoes/"+id;}
  if(responsavel==null||responsavel.isBlank()){r.addFlashAttribute("erro","Informe o responsável pela separação.");return "redirect:/mutiroes/solicitacoes/"+id;}
  Map<Long,String> valores=new HashMap<>(); if(itemId!=null&&quantidadeSeparada!=null){for(int i=0;i<Math.min(itemId.size(),quantidadeSeparada.size());i++)valores.put(itemId.get(i),quantidadeSeparada.get(i));}
  for(ItemSolicitacaoMutirao it:s.getItens()){
   String raw=valores.get(it.getId()); if(raw==null)continue;
   try{BigDecimal q=new BigDecimal(raw.replace(',', '.')); if(q.compareTo(BigDecimal.ZERO)<0||q.compareTo(it.getQuantidadeSolicitada())>0){r.addFlashAttribute("erro","A quantidade separada deve estar entre zero e a quantidade solicitada para: "+it.getDescricao());return "redirect:/mutiroes/solicitacoes/"+id;} BigDecimal anterior=it.getQuantidadeSeparada(); if(anterior==null || anterior.compareTo(q)!=0){AuditoriaItemSolicitacaoMutirao a=new AuditoriaItemSolicitacaoMutirao(); a.setSolicitacaoId(s.getId()); a.setItemSolicitacaoId(it.getId()); a.setItemDescricao(it.getDescricao()); a.setCampoAlterado("QUANTIDADE_SEPARADA"); a.setValorAnterior(anterior); a.setValorNovo(q); a.setResponsavel(responsavel.trim()); a.setOcorridoEm(LocalDateTime.now()); auditoriaItens.save(a);} it.setQuantidadeSeparada(q);}
   catch(Exception ex){r.addFlashAttribute("erro","Informe uma quantidade válida para: "+it.getDescricao());return "redirect:/mutiroes/solicitacoes/"+id;}
  }
  s.setResponsavelSeparacao(responsavel.trim()); s.setDataSeparacao(LocalDateTime.now()); solicitacoes.save(s);
  registrarHistorico(s.getId(), "CONFERENCIA", "Quantidades separadas atualizadas.", responsavel, s.getStatus(), s.getStatus());
  boolean completo=s.getItens().stream().allMatch(i->i.getQuantidadeSeparada()!=null&&i.getQuantidadeSeparada().compareTo(i.getQuantidadeSolicitada())==0);
  r.addFlashAttribute("sucesso",completo?"Separação registrada. Todos os itens estão completos.":"Separação registrada com itens pendentes ou quantidades parciais.");
  return "redirect:/mutiroes/solicitacoes/"+id;
 }
 @PostMapping("/{id}/status") public String status(@PathVariable Long id,@RequestParam String status,@RequestParam(required=false) String responsavel,RedirectAttributes r){SolicitacaoMutirao s=solicitacoes.findWithItensById(id).orElse(null);if(s==null){r.addFlashAttribute("erro","Solicitação não encontrada.");return "redirect:/mutiroes/solicitacoes";}Map<String,List<String>> fluxo=Map.of("RASCUNHO",List.of("ENVIADA"),"ENVIADA",List.of("APROVADA","RASCUNHO"),"APROVADA",List.of("EM_SEPARACAO"),"EM_SEPARACAO",List.of("ENTREGUE"));String statusAnterior = s.getStatus(); if(!fluxo.getOrDefault(s.getStatus(),List.of()).contains(status)){r.addFlashAttribute("erro","Transição de status não permitida.");return "redirect:/mutiroes/solicitacoes";}if(status.equals("EM_SEPARACAO")){s.setResponsavelSeparacao(responsavel==null||responsavel.isBlank()?null:responsavel.trim());s.setDataSeparacao(LocalDateTime.now());}if(status.equals("APROVADA")){s.setAprovador(responsavel==null||responsavel.isBlank()?"Responsável não informado":responsavel.trim());s.setDataAprovacao(LocalDateTime.now());}if(status.equals("ENTREGUE")){boolean completo=s.getItens().stream().allMatch(it->it.getQuantidadeSeparada()!=null&&it.getQuantidadeSeparada().compareTo(it.getQuantidadeSolicitada())==0);if(!completo){r.addFlashAttribute("erro","Não é possível registrar a entrega: existem itens pendentes ou quantidades divergentes.");return "redirect:/mutiroes/solicitacoes/"+id;}if(responsavel==null||responsavel.isBlank()){r.addFlashAttribute("erro","Informe o responsável pelo registro da entrega.");return "redirect:/mutiroes/solicitacoes/"+id;}s.setResponsavelEntrega(responsavel.trim());s.setDataEntrega(LocalDateTime.now());}s.setStatus(status);solicitacoes.save(s);
  String descricao = switch (status) { case "ENVIADA" -> "Solicitação enviada para aprovação."; case "APROVADA" -> "Solicitação aprovada."; case "EM_SEPARACAO" -> "Separação iniciada."; case "ENTREGUE" -> "Entrega confirmada."; case "RASCUNHO" -> "Solicitação retornou para rascunho."; default -> "Status atualizado."; };
  registrarHistorico(s.getId(), "STATUS", descricao, responsavel, statusAnterior, status);
  r.addFlashAttribute("sucesso","Status atualizado para "+status+". O estoque não foi movimentado automaticamente.");return "redirect:/mutiroes/solicitacoes";}
 @GetMapping("/{id}") public String detalhe(@PathVariable Long id,Model model,RedirectAttributes r){SolicitacaoMutirao s=solicitacoes.findWithItensById(id).orElse(null);if(s==null){r.addFlashAttribute("erro","Solicitação não encontrada.");return "redirect:/mutiroes/solicitacoes";}model.addAttribute("s",s);model.addAttribute("historico",historico.findBySolicitacaoIdOrderByOcorridoEmDescIdDesc(id));model.addAttribute("auditoriaItens",auditoriaItens.findBySolicitacaoIdOrderByOcorridoEmDescIdDesc(id));return "mutiroes/solicitacao-detalhe";}
}
