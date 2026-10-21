package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.web.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursosEstoqueServiceRegrasTest {
    @Mock private CarrinhoAnestesistaRepository carrinhos;
    @Mock private ArmarioSalaRepository armarios;
    @Mock private SalaCirurgicaRepository salas;
    @Mock private ItemEstoqueRepository itens;
    @Mock private CarrinhoItemRepository carrinhoItens;
    @Mock private ArmarioItemRepository armarioItens;
    private RecursosEstoqueService service;

    @BeforeEach void setUp() {
        service = new RecursosEstoqueService(carrinhos, armarios, salas, itens, carrinhoItens, armarioItens);
    }

    @Test void rejeitaCodigoDeCarrinhoDuplicado() {
        CarrinhoForm form = new CarrinhoForm();
        form.setCodigo(" C-01 "); form.setNome("Carrinho anestesia");
        when(carrinhos.existsByCodigoIgnoreCase("C-01")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvarCarrinho(form));
        assertEquals("Já existe um carrinho com esse código.", ex.getMessage());
        verify(carrinhos, never()).save(any());
    }

    @Test void rejeitaQuantidadeMinimaNegativaAntesDeConsultarEstoque() {
        MinimoItemForm form = new MinimoItemForm();
        form.setItemId(10L); form.setQuantidadeMinima(new BigDecimal("-1"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.configurarMinimoCarrinho(1L, form));
        assertEquals("A quantidade mínima não pode ser negativa.", ex.getMessage());
        verifyNoInteractions(carrinhos, itens, carrinhoItens);
    }

    @Test void rejeitaArmarioVinculadoASalaInativa() {
        SalaCirurgica sala = new SalaCirurgica(); sala.setId(3L); sala.setAtiva(false);
        when(salas.findById(3L)).thenReturn(Optional.of(sala));
        ArmarioForm form = new ArmarioForm();
        form.setSalaCirurgicaId(3L); form.setCodigo("A-01"); form.setNome("Armário sala 1");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvarArmario(form));
        assertEquals("Não é possível vincular armário a uma sala inativa.", ex.getMessage());
        verify(armarios, never()).save(any());
    }
}
