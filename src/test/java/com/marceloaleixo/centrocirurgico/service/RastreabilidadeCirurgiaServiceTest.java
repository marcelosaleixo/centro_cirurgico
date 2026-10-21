package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.entity.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class RastreabilidadeCirurgiaServiceTest {
    @Mock private CirurgiaRepository cirurgiaRepository;
    @Mock private ItemEstoqueRepository itemRepository;
    @Mock private EstoqueGeralRepository estoqueRepository;
    @Mock private LoteEstoqueRepository loteRepository;
    @Mock private ConsumoCirurgiaLoteRepository consumoRepository;
    @Mock private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Mock private FechamentoFinanceiroCirurgiaRepository fechamentoRepository;
    @Mock private BloqueioConsumoCirurgiaService bloqueioConsumoService;
    private RastreabilidadeCirurgiaService service;

    @BeforeEach
    void setUp() {
        service = new RastreabilidadeCirurgiaService(cirurgiaRepository, itemRepository, estoqueRepository,
                loteRepository, consumoRepository, movimentacaoRepository, fechamentoRepository, bloqueioConsumoService);
    }

    @Test
    void validaCamposObrigatoriosAntesDeConsultarBanco() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(null, 1L, BigDecimal.ONE, "Uso cirúrgico"));
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1L, null, BigDecimal.ONE, "Uso cirúrgico"));
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1L, 1L, BigDecimal.ZERO, "Uso cirúrgico"));
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(1L, 1L, BigDecimal.ONE, " "));
        verifyNoInteractions(cirurgiaRepository, itemRepository, estoqueRepository, loteRepository,
                consumoRepository, movimentacaoRepository, fechamentoRepository, bloqueioConsumoService);
    }
    @Test
    void distribuiBaixaEntreLotesValidosNaOrdemFefo() {
        Cirurgia cirurgia = new Cirurgia(); cirurgia.setId(7L);
        ItemEstoque item = new ItemEstoque(); item.setId(3L); item.setAtivo(true);
        EstoqueGeral saldo = new EstoqueGeral(); saldo.setQuantidadeAtual(new BigDecimal("10.000"));
        saldo.setItem(item);
        LoteEstoque primeiro = lote(item, "LOTE-A", LocalDate.now().plusDays(2), "2.000");
        LoteEstoque segundo = lote(item, "LOTE-B", LocalDate.now().plusDays(10), "5.000");
        when(cirurgiaRepository.findById(7L)).thenReturn(Optional.of(cirurgia));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(estoqueRepository.bloquearPorItemId(3L)).thenReturn(Optional.of(saldo));
        when(loteRepository.bloquearLotesValidosFefo(eq(3L), any(LocalDate.class))).thenReturn(List.of(primeiro, segundo));

        service.registrar(7L, 3L, new BigDecimal("4.000"), "Uso na cirurgia");

        assertEquals(0, new BigDecimal("0.000").compareTo(primeiro.getQuantidadeAtual()));
        assertEquals(0, new BigDecimal("3.000").compareTo(segundo.getQuantidadeAtual()));
        assertEquals(0, new BigDecimal("6.000").compareTo(saldo.getQuantidadeAtual()));
        ArgumentCaptor<ConsumoCirurgiaLote> captor = ArgumentCaptor.forClass(ConsumoCirurgiaLote.class);
        verify(consumoRepository, times(2)).save(captor.capture());
        assertEquals(List.of(new BigDecimal("2.000"), new BigDecimal("2.000")),
                captor.getAllValues().stream().map(ConsumoCirurgiaLote::getQuantidade).toList());
        verify(movimentacaoRepository, times(2)).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void naoAlteraSaldosQuandoLotesValidosSaoInsuficientes() {
        Cirurgia cirurgia = new Cirurgia(); cirurgia.setId(7L);
        ItemEstoque item = new ItemEstoque(); item.setId(3L); item.setAtivo(true);
        EstoqueGeral saldo = new EstoqueGeral(); saldo.setQuantidadeAtual(new BigDecimal("10.000"));
        LoteEstoque lote = lote(item, "LOTE-A", LocalDate.now().plusDays(2), "1.000");
        when(cirurgiaRepository.findById(7L)).thenReturn(Optional.of(cirurgia));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(estoqueRepository.bloquearPorItemId(3L)).thenReturn(Optional.of(saldo));
        when(loteRepository.bloquearLotesValidosFefo(eq(3L), any(LocalDate.class))).thenReturn(List.of(lote));

        assertThrows(IllegalStateException.class, () -> service.registrar(7L, 3L,
                new BigDecimal("2.000"), "Uso na cirurgia"));
        assertEquals(0, new BigDecimal("10.000").compareTo(saldo.getQuantidadeAtual()));
        assertEquals(0, new BigDecimal("1.000").compareTo(lote.getQuantidadeAtual()));
        verify(consumoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void bloqueioFinanceiroInterrompeAntesDeAlterarEstoque() {
        Cirurgia cirurgia = new Cirurgia(); cirurgia.setId(7L);
        when(cirurgiaRepository.findById(7L)).thenReturn(Optional.of(cirurgia));
        doThrow(new IllegalStateException("Fechamento aprovado")).when(bloqueioConsumoService).validarConsumoPermitido(7L);

        assertThrows(IllegalStateException.class, () -> service.registrar(7L, 3L,
                BigDecimal.ONE, "Uso na cirurgia"));
        verifyNoInteractions(itemRepository, estoqueRepository, loteRepository, consumoRepository, movimentacaoRepository);
    }

    private LoteEstoque lote(ItemEstoque item, String numero, LocalDate validade, String quantidade) {
        LoteEstoque lote = new LoteEstoque();
        lote.setItem(item); lote.setNumeroLote(numero); lote.setDataValidade(validade);
        lote.setQuantidadeInicial(new BigDecimal(quantidade)); lote.setQuantidadeAtual(new BigDecimal(quantidade));
        return lote;
    }

}
