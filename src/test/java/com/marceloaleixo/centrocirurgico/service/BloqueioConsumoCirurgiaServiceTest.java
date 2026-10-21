package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.FechamentoFinanceiroCirurgia;
import com.marceloaleixo.centrocirurgico.repository.FechamentoFinanceiroCirurgiaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloqueioConsumoCirurgiaServiceTest {
    @Mock private FechamentoFinanceiroCirurgiaRepository fechamentoRepository;
    @InjectMocks private BloqueioConsumoCirurgiaService service;

    @Test
    void rejeitaCirurgiaNula() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.validarConsumoPermitido(null));
        assertTrue(ex.getMessage().contains("Informe a cirurgia"));
        verifyNoInteractions(fechamentoRepository);
    }

    @Test
    void bloqueiaConsumoQuandoFechamentoEstaAprovado() {
        FechamentoFinanceiroCirurgia fechamento = new FechamentoFinanceiroCirurgia();
        fechamento.setStatus("APROVADO");
        when(fechamentoRepository.findByCirurgiaId(42L)).thenReturn(Optional.of(fechamento));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.validarConsumoPermitido(42L));

        assertTrue(ex.getMessage().contains("está APROVADO"));
        verify(fechamentoRepository).findByCirurgiaId(42L);
    }

    @Test
    void permiteConsumoQuandoFechamentoEstaAberto() {
        FechamentoFinanceiroCirurgia fechamento = new FechamentoFinanceiroCirurgia();
        fechamento.setStatus("ABERTO");
        when(fechamentoRepository.findByCirurgiaId(10L)).thenReturn(Optional.of(fechamento));

        assertDoesNotThrow(() -> service.validarConsumoPermitido(10L));
    }

    @Test
    void permiteConsumoQuandoNaoExisteFechamento() {
        when(fechamentoRepository.findByCirurgiaId(11L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.validarConsumoPermitido(11L));
    }
}
