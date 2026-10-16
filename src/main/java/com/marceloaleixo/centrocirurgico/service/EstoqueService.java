package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.enums.LocalizacaoEstoque;
import com.marceloaleixo.centrocirurgico.enums.TipoMovimentacao;
import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.web.ItemEstoqueForm;
import com.marceloaleixo.centrocirurgico.web.MovimentacaoEstoqueForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EstoqueService {
    private final ItemEstoqueRepository itemRepository;
    private final EstoqueGeralRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final CarrinhoAnestesistaRepository carrinhoRepository;
    private final ArmarioSalaRepository armarioRepository;
    private final CarrinhoItemRepository carrinhoItemRepository;
    private final ArmarioItemRepository armarioItemRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AuditoriaAlteracaoService auditoriaAlteracaoService;

    public EstoqueService(ItemEstoqueRepository itemRepository, EstoqueGeralRepository estoqueRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository,
                          CarrinhoAnestesistaRepository carrinhoRepository, ArmarioSalaRepository armarioRepository,
                          CarrinhoItemRepository carrinhoItemRepository, ArmarioItemRepository armarioItemRepository,
                          ColaboradorRepository colaboradorRepository,
                          AuditoriaAlteracaoService auditoriaAlteracaoService) {
        this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.armarioRepository = armarioRepository;
        this.carrinhoItemRepository = carrinhoItemRepository;
        this.armarioItemRepository = armarioItemRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.auditoriaAlteracaoService = auditoriaAlteracaoService;
    }

    @Transactional(readOnly = true)
    public List<ItemEstoque> listarItens() { return itemRepository.findAllByOrderByNomeAsc(); }

    @Transactional(readOnly = true)
    public ItemEstoque buscarItem(Long id) {
        return itemRepository.findWithEstoqueGeralById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de estoque não encontrado."));
    }

    @Transactional
    public ItemEstoque salvarItem(ItemEstoqueForm form) {
        if (form.getCodigo() == null || form.getCodigo().isBlank())
            throw new IllegalArgumentException("Informe o código do item.");
        if (form.getNome() == null || form.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do item.");
        if (form.getUnidadeMedida() == null || form.getUnidadeMedida().isBlank())
            throw new IllegalArgumentException("Informe a unidade de medida.");
        if (form.getEstoqueMinimo() == null || form.getEstoqueMinimo().signum() < 0)
            throw new IllegalArgumentException("O estoque mínimo não pode ser negativo.");
        if (form.getQuantidadeInicial() == null || form.getQuantidadeInicial().signum() < 0)
            throw new IllegalArgumentException("A quantidade inicial não pode ser negativa.");

        boolean codigoDuplicado = form.getId() == null
                ? itemRepository.existsByCodigoIgnoreCase(form.getCodigo().trim())
                : itemRepository.existsByCodigoIgnoreCaseAndIdNot(form.getCodigo().trim(), form.getId());
        if (codigoDuplicado) throw new IllegalArgumentException("Já existe um item com esse código.");

        ItemEstoque item;
        boolean novoCadastro = form.getId() == null;
        Map<String, String> antes = novoCadastro ? Map.of() : snapshot(itemRepository.findById(form.getId()).orElseThrow(() -> new IllegalArgumentException("Item não encontrado.")));
        if (form.getId() == null) {
            item = new ItemEstoque();
            item.setCodigo(form.getCodigo().trim());
            item.setNome(form.getNome().trim());
            item.setUnidadeMedida(form.getUnidadeMedida().trim());
            item.setEstoqueMinimo(form.getEstoqueMinimo());
            item = itemRepository.save(item);

            EstoqueGeral estoque = new EstoqueGeral();
            estoque.setItem(item);
            estoque.setQuantidadeAtual(BigDecimal.ZERO);
            estoqueRepository.save(estoque);

            auditoriaAlteracaoService.registrarDiferencas("ItemEstoque", item.getId(), "CRIACAO", antes, snapshot(item));
            if (form.getQuantidadeInicial().signum() > 0) {
                MovimentacaoEstoqueForm mf = new MovimentacaoEstoqueForm();
                mf.setItemId(item.getId());
                mf.setTipo(TipoMovimentacao.ENTRADA);
                mf.setQuantidade(form.getQuantidadeInicial());
                mf.setDestinoTipo(LocalizacaoEstoque.ESTOQUE_GERAL);
                mf.setMotivo("Saldo inicial do cadastro");
                registrarMovimentacao(mf);
            }
            return item;
        }

        item = itemRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        item.setCodigo(form.getCodigo().trim());
        item.setNome(form.getNome().trim());
        item.setUnidadeMedida(form.getUnidadeMedida().trim());
        item.setEstoqueMinimo(form.getEstoqueMinimo());
        ItemEstoque salvo = itemRepository.save(item);
        auditoriaAlteracaoService.registrarDiferencas("ItemEstoque", salvo.getId(), "EDICAO", antes, snapshot(salvo));
        return salvo;
    }

    @Transactional
    public void desativarItem(Long id) {
        ItemEstoque item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        Map<String, String> antes = snapshot(item);
        item.setAtivo(false);
        ItemEstoque salvo = itemRepository.save(item);
        auditoriaAlteracaoService.registrarDiferencas("ItemEstoque", salvo.getId(), "DESATIVACAO", antes, snapshot(salvo));
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> listarMovimentacoes() {
        return movimentacaoRepository.findTop100ByOrderByDataMovimentacaoDesc();
    }

    @Transactional
    public MovimentacaoEstoque registrarMovimentacao(MovimentacaoEstoqueForm form) {
        if (form.getItemId() == null) throw new IllegalArgumentException("Selecione o item.");
        if (form.getQuantidade() == null || form.getQuantidade().signum() <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (form.getTipo() == null) throw new IllegalArgumentException("Selecione o tipo de movimentação.");

        ItemEstoque item = itemRepository.findById(form.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        if (!item.isAtivo()) throw new IllegalArgumentException("Não é possível movimentar um item inativo.");

        LocalizacaoEstoque origem = form.getOrigemTipo();
        LocalizacaoEstoque destino = form.getDestinoTipo();
        if (form.getTipo() == TipoMovimentacao.ENTRADA) {
            if (destino == null) throw new IllegalArgumentException("Selecione o destino da entrada.");
            if (destino != LocalizacaoEstoque.ESTOQUE_GERAL)
                throw new IllegalArgumentException("Entrada de fornecedor deve entrar primeiro no estoque geral.");
            adicionar(LocalizacaoEstoque.ESTOQUE_GERAL, null, item, form.getQuantidade());
        } else if (form.getTipo() == TipoMovimentacao.SAIDA) {
            if (origem == null) throw new IllegalArgumentException("Selecione a origem da saída.");
            if (origem != LocalizacaoEstoque.ESTOQUE_GERAL && origem != LocalizacaoEstoque.CARRINHO && origem != LocalizacaoEstoque.ARMARIO)
                throw new IllegalArgumentException("Origem de saída inválida.");
            retirar(origem, idObrigatorio(origem, form.getOrigemId()), item, form.getQuantidade());
        } else {
            if (origem == null || destino == null) throw new IllegalArgumentException("Selecione origem e destino.");
            if (origem == destino && java.util.Objects.equals(form.getOrigemId(), form.getDestinoId()))
                throw new IllegalArgumentException("A origem e o destino devem ser diferentes.");
            if (destino == LocalizacaoEstoque.ESTOQUE_GERAL && form.getDestinoId() != null)
                throw new IllegalArgumentException("O estoque geral não exige identificador.");
            retirar(origem, idObrigatorio(origem, form.getOrigemId()), item, form.getQuantidade());
            adicionar(destino, idObrigatorio(destino, form.getDestinoId()), item, form.getQuantidade());
        }

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setItem(item);
        mov.setTipo(form.getTipo());
        mov.setQuantidade(form.getQuantidade());
        mov.setOrigemTipo(form.getTipo() == TipoMovimentacao.ENTRADA ? null : origem);
        mov.setOrigemId(form.getTipo() == TipoMovimentacao.ENTRADA ? null : form.getOrigemId());
        mov.setDestinoTipo(destino);
        mov.setDestinoId(destino == LocalizacaoEstoque.ESTOQUE_GERAL ? null : form.getDestinoId());
        if (form.getColaboradorId() != null) {
            Colaborador c = colaboradorRepository.findById(form.getColaboradorId())
                    .orElseThrow(() -> new IllegalArgumentException("Colaborador responsável não encontrado."));
            mov.setColaborador(c);
        }
        mov.setMotivo(form.getMotivo() == null ? null : form.getMotivo().trim());
        MovimentacaoEstoque salva = movimentacaoRepository.save(mov);
        Map<String, String> dadosMovimentacao = new LinkedHashMap<>();
        dadosMovimentacao.put("itemId", item.getId().toString());
        dadosMovimentacao.put("tipo", form.getTipo().name());
        dadosMovimentacao.put("quantidade", form.getQuantidade().toPlainString());
        dadosMovimentacao.put("origem", form.getTipo() == TipoMovimentacao.ENTRADA ? "FORNECEDOR" : String.valueOf(origem));
        dadosMovimentacao.put("destino", String.valueOf(destino));
        dadosMovimentacao.put("motivo", form.getMotivo());
        auditoriaAlteracaoService.registrarDiferencas("MovimentacaoEstoque", salva.getId(), form.getTipo().name(), Map.of(), dadosMovimentacao);
        return salva;
    }

    private Map<String, String> snapshot(ItemEstoque item) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("codigo", item.getCodigo());
        m.put("nome", item.getNome());
        m.put("unidadeMedida", item.getUnidadeMedida());
        m.put("estoqueMinimo", item.getEstoqueMinimo() == null ? null : item.getEstoqueMinimo().toPlainString());
        m.put("ativo", Boolean.toString(item.isAtivo()));
        return m;
    }

    private Long idObrigatorio(LocalizacaoEstoque local, Long id) {
        if (local != LocalizacaoEstoque.ESTOQUE_GERAL && id == null)
            throw new IllegalArgumentException("Selecione o carrinho ou armário para a localização escolhida.");
        return id;
    }

    private void retirar(LocalizacaoEstoque local, Long localId, ItemEstoque item, BigDecimal qtd) {
        if (local == LocalizacaoEstoque.ESTOQUE_GERAL) {
            EstoqueGeral e = estoqueRepository.bloquearPorItemId(item.getId())
                    .orElseThrow(() -> new IllegalStateException("Saldo do estoque geral não encontrado."));
            garantirSaldo(e.getQuantidadeAtual(), qtd);
            e.setQuantidadeAtual(e.getQuantidadeAtual().subtract(qtd));
            estoqueRepository.save(e);
        } else if (local == LocalizacaoEstoque.CARRINHO) {
            CarrinhoItem ci = carrinhoItemRepository.findByCarrinhoIdAndItemId(localId, item.getId())
                    .orElseThrow(() -> new IllegalStateException("O item não está cadastrado nesse carrinho."));
            garantirSaldo(ci.getQuantidadeAtual(), qtd);
            ci.setQuantidadeAtual(ci.getQuantidadeAtual().subtract(qtd));
            carrinhoItemRepository.save(ci);
        } else {
            ArmarioItem ai = armarioItemRepository.findByArmarioIdAndItemId(localId, item.getId())
                    .orElseThrow(() -> new IllegalStateException("O item não está cadastrado nesse armário."));
            garantirSaldo(ai.getQuantidadeAtual(), qtd);
            ai.setQuantidadeAtual(ai.getQuantidadeAtual().subtract(qtd));
            armarioItemRepository.save(ai);
        }
    }

    private void adicionar(LocalizacaoEstoque local, Long localId, ItemEstoque item, BigDecimal qtd) {
        if (local == LocalizacaoEstoque.ESTOQUE_GERAL) {
            EstoqueGeral e = estoqueRepository.bloquearPorItemId(item.getId())
                    .orElseThrow(() -> new IllegalStateException("Saldo do estoque geral não encontrado."));
            e.setQuantidadeAtual(e.getQuantidadeAtual().add(qtd));
            estoqueRepository.save(e);
        } else if (local == LocalizacaoEstoque.CARRINHO) {
            CarrinhoAnestesista carrinho = carrinhoRepository.findById(localId)
                    .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));
            if (!carrinho.isAtivo()) throw new IllegalArgumentException("O carrinho está inativo.");
            CarrinhoItem ci = carrinhoItemRepository.findByCarrinhoIdAndItemId(localId, item.getId()).orElseGet(() -> {
                CarrinhoItem novo = new CarrinhoItem();
                novo.setCarrinho(carrinho); novo.setItem(item);
                novo.setQuantidadeAtual(BigDecimal.ZERO); novo.setQuantidadeMinima(BigDecimal.ZERO);
                return novo;
            });
            ci.setQuantidadeAtual(ci.getQuantidadeAtual().add(qtd));
            carrinhoItemRepository.save(ci);
        } else {
            ArmarioSala armario = armarioRepository.findById(localId)
                    .orElseThrow(() -> new IllegalArgumentException("Armário não encontrado."));
            if (!armario.isAtivo()) throw new IllegalArgumentException("O armário está inativo.");
            ArmarioItem ai = armarioItemRepository.findByArmarioIdAndItemId(localId, item.getId()).orElseGet(() -> {
                ArmarioItem novo = new ArmarioItem();
                novo.setArmario(armario); novo.setItem(item);
                novo.setQuantidadeAtual(BigDecimal.ZERO); novo.setQuantidadeMinima(BigDecimal.ZERO);
                return novo;
            });
            ai.setQuantidadeAtual(ai.getQuantidadeAtual().add(qtd));
            armarioItemRepository.save(ai);
        }
    }

    private void garantirSaldo(BigDecimal saldo, BigDecimal qtd) {
        if (saldo.compareTo(qtd) < 0) throw new IllegalStateException("Saldo insuficiente para concluir a movimentação.");
    }
}
