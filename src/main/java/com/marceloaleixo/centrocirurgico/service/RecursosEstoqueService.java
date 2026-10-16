package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.web.ArmarioForm;
import com.marceloaleixo.centrocirurgico.web.CarrinhoForm;
import com.marceloaleixo.centrocirurgico.web.MinimoItemForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RecursosEstoqueService {
    private final CarrinhoAnestesistaRepository carrinhos;
    private final ArmarioSalaRepository armarios;
    private final SalaCirurgicaRepository salas;
    private final ItemEstoqueRepository itens;
    private final CarrinhoItemRepository carrinhoItens;
    private final ArmarioItemRepository armarioItens;

    public RecursosEstoqueService(CarrinhoAnestesistaRepository carrinhos, ArmarioSalaRepository armarios,
            SalaCirurgicaRepository salas, ItemEstoqueRepository itens,
            CarrinhoItemRepository carrinhoItens, ArmarioItemRepository armarioItens) {
        this.carrinhos=carrinhos; this.armarios=armarios; this.salas=salas;
        this.itens=itens; this.carrinhoItens=carrinhoItens; this.armarioItens=armarioItens;
    }

    @Transactional(readOnly=true)
    public List<CarrinhoAnestesista> listarCarrinhos(){ return carrinhos.findAllByOrderByNomeAsc(); }
    @Transactional(readOnly=true)
    public List<ArmarioSala> listarArmarios(){ return armarios.findAllByOrderBySalaCirurgicaNomeAscCodigoAsc(); }
    @Transactional(readOnly=true)
    public CarrinhoAnestesista buscarCarrinho(Long id){return carrinhos.findById(id).orElseThrow(()->new IllegalArgumentException("Carrinho não encontrado."));}
    @Transactional(readOnly=true)
    public ArmarioSala buscarArmario(Long id){return armarios.findById(id).orElseThrow(()->new IllegalArgumentException("Armário não encontrado."));}

    @Transactional
    public CarrinhoAnestesista salvarCarrinho(CarrinhoForm f) {
        validarTexto(f.getCodigo(),"Informe o código do carrinho.");
        validarTexto(f.getNome(),"Informe o nome do carrinho.");
        boolean duplicado = f.getId()==null ? carrinhos.existsByCodigoIgnoreCase(f.getCodigo().trim())
                : carrinhos.existsByCodigoIgnoreCaseAndIdNot(f.getCodigo().trim(), f.getId());
        if(duplicado) throw new IllegalArgumentException("Já existe um carrinho com esse código.");
        CarrinhoAnestesista c = f.getId()==null ? new CarrinhoAnestesista() : buscarCarrinho(f.getId());
        c.setCodigo(f.getCodigo().trim()); c.setNome(f.getNome().trim());
        c.setLocalizacao(f.getLocalizacao()==null?null:f.getLocalizacao().trim());
        return carrinhos.save(c);
    }
    @Transactional
    public void alternarCarrinho(Long id) {
        CarrinhoAnestesista c=buscarCarrinho(id); c.setAtivo(!c.isAtivo()); carrinhos.save(c);
    }
    @Transactional
    public ArmarioSala salvarArmario(ArmarioForm f) {
        validarTexto(f.getCodigo(),"Informe o código do armário.");
        validarTexto(f.getNome(),"Informe o nome do armário.");
        if(f.getSalaCirurgicaId()==null) throw new IllegalArgumentException("Selecione a sala cirúrgica.");
        SalaCirurgica sala=salas.findById(f.getSalaCirurgicaId()).orElseThrow(()->new IllegalArgumentException("Sala cirúrgica não encontrada."));
        if(!sala.isAtiva()) throw new IllegalArgumentException("Não é possível vincular armário a uma sala inativa.");
        boolean duplicado=f.getId()==null?armarios.existsBySalaCirurgicaIdAndCodigoIgnoreCase(sala.getId(),f.getCodigo().trim())
                :armarios.existsBySalaCirurgicaIdAndCodigoIgnoreCaseAndIdNot(sala.getId(),f.getCodigo().trim(),f.getId());
        if(duplicado) throw new IllegalArgumentException("Já existe um armário com esse código nesta sala.");
        ArmarioSala a=f.getId()==null?new ArmarioSala():buscarArmario(f.getId());
        a.setSalaCirurgica(sala); a.setCodigo(f.getCodigo().trim()); a.setNome(f.getNome().trim());
        return armarios.save(a);
    }
    @Transactional
    public void alternarArmario(Long id) {
        ArmarioSala a=buscarArmario(id); a.setAtivo(!a.isAtivo()); armarios.save(a);
    }

    @Transactional
    public void configurarMinimoCarrinho(Long carrinhoId, MinimoItemForm f) {
        if(f.getItemId()==null) throw new IllegalArgumentException("Selecione o insumo.");
        validarMinimo(f.getQuantidadeMinima());
        CarrinhoAnestesista c=buscarCarrinho(carrinhoId);
        if(!c.isAtivo()) throw new IllegalArgumentException("O carrinho está inativo.");
        ItemEstoque item=itens.findById(f.getItemId()).orElseThrow(()->new IllegalArgumentException("Item não encontrado."));
        if(!item.isAtivo()) throw new IllegalArgumentException("O item está inativo.");
        CarrinhoItem ci=carrinhoItens.findByCarrinhoIdAndItemId(carrinhoId,item.getId()).orElseGet(()->{
            CarrinhoItem n=new CarrinhoItem(); n.setCarrinho(c); n.setItem(item);
            n.setQuantidadeAtual(BigDecimal.ZERO); n.setQuantidadeMinima(BigDecimal.ZERO); return n;
        });
        ci.setQuantidadeMinima(f.getQuantidadeMinima()); carrinhoItens.save(ci);
    }
    @Transactional
    public void configurarMinimoArmario(Long armarioId, MinimoItemForm f) {
        if(f.getItemId()==null) throw new IllegalArgumentException("Selecione o insumo.");
        validarMinimo(f.getQuantidadeMinima());
        ArmarioSala a=buscarArmario(armarioId);
        if(!a.isAtivo()) throw new IllegalArgumentException("O armário está inativo.");
        ItemEstoque item=itens.findById(f.getItemId()).orElseThrow(()->new IllegalArgumentException("Item não encontrado."));
        if(!item.isAtivo()) throw new IllegalArgumentException("O item está inativo.");
        ArmarioItem ai=armarioItens.findByArmarioIdAndItemId(armarioId,item.getId()).orElseGet(()->{
            ArmarioItem n=new ArmarioItem(); n.setArmario(a); n.setItem(item);
            n.setQuantidadeAtual(BigDecimal.ZERO); n.setQuantidadeMinima(BigDecimal.ZERO); return n;
        });
        ai.setQuantidadeMinima(f.getQuantidadeMinima()); armarioItens.save(ai);
    }
    @Transactional(readOnly=true)
    public List<CarrinhoItem> itensCarrinho(Long id){buscarCarrinho(id);return carrinhoItens.findByCarrinhoIdOrderByItemNomeAsc(id);}
    @Transactional(readOnly=true)
    public List<ArmarioItem> itensArmario(Long id){buscarArmario(id);return armarioItens.findByArmarioIdOrderByItemNomeAsc(id);}
    private void validarTexto(String s,String msg){if(s==null||s.isBlank())throw new IllegalArgumentException(msg);}
    private void validarMinimo(BigDecimal q){if(q==null||q.signum()<0)throw new IllegalArgumentException("A quantidade mínima não pode ser negativa.");}
}
