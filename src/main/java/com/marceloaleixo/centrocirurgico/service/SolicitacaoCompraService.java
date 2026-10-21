package com.marceloaleixo.centrocirurgico.service;
import com.marceloaleixo.centrocirurgico.entity.ItemEstoque;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoCompra;
import com.marceloaleixo.centrocirurgico.repository.ItemEstoqueRepository;
import com.marceloaleixo.centrocirurgico.repository.SolicitacaoCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
public class SolicitacaoCompraService {
 private final SolicitacaoCompraRepository repository; private final ItemEstoqueRepository itemRepository;
 public SolicitacaoCompraService(SolicitacaoCompraRepository r, ItemEstoqueRepository i){repository=r;itemRepository=i;}
 public List<SolicitacaoCompra> listar(){return repository.findAllByOrderByCriadoEmDesc();}
 @Transactional public SolicitacaoCompra criar(Long itemId, BigDecimal quantidade, String justificativa, String fornecedor, String responsavel, String observacao){
  if(itemId==null) throw new IllegalArgumentException("Selecione um item do estoque.");
  if(quantidade==null || quantidade.compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
  if(justificativa==null || justificativa.isBlank()) throw new IllegalArgumentException("Informe a justificativa da compra.");
  if(responsavel==null || responsavel.isBlank()) throw new IllegalArgumentException("Informe o responsável pela solicitação.");
  ItemEstoque item=itemRepository.findById(itemId).orElseThrow(()->new IllegalArgumentException("Item de estoque não encontrado."));
  if(!item.isAtivo()) throw new IllegalArgumentException("Não é possível solicitar compra de item inativo.");
  SolicitacaoCompra s=new SolicitacaoCompra(); s.setItem(item);s.setQuantidadeSolicitada(quantidade);s.setJustificativa(justificativa.trim());s.setFornecedorSugerido(fornecedor);s.setResponsavelSolicitacao(responsavel.trim());s.setObservacao(observacao);s.setStatus(SolicitacaoCompra.StatusCompra.PENDENTE_APROVACAO);return repository.save(s);
 }
 @Transactional public void atualizarStatus(Long id, SolicitacaoCompra.StatusCompra status){
  SolicitacaoCompra s=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Solicitação de compra não encontrada."));
  if(status == SolicitacaoCompra.StatusCompra.RECEBIDA) throw new IllegalStateException("Use o formulário de recebimento para conferir quantidades e atualizar o estoque.");
  if(s.getStatus()==SolicitacaoCompra.StatusCompra.RECEBIDA || s.getStatus()==SolicitacaoCompra.StatusCompra.CANCELADA) throw new IllegalStateException("Solicitação recebida ou cancelada não pode ser alterada.");
  s.setStatus(status); repository.save(s);
 }
}
