package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.FuncaoProfissional;
import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusTrocaPlantao;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.TrocaPlantaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrocaPlantaoServiceRegrasTest {
    @Mock private TrocaPlantaoRepository trocaRepository;
    @Mock private ColaboradorRepository colaboradorRepository;
    @InjectMocks private TrocaPlantaoService service;

    @Test
    void rejeitaTrocaEntreFuncoesDiferentes() {
        Colaborador solicitante = colaborador(1L, 100L);
        Colaborador aceitante = colaborador(2L, 200L);
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(colaboradorRepository.findById(2L)).thenReturn(Optional.of(aceitante));
        TrocaPlantao troca = troca(1L, 2L);
        assertThrows(IllegalArgumentException.class, () -> service.solicitar(troca));
        verify(trocaRepository, never()).save(any());
    }

    @Test
    void naoPermiteSolicitanteAceitarPropriaTroca() {
        TrocaPlantao troca = troca(1L, 1L);
        assertThrows(IllegalArgumentException.class, () -> service.solicitar(troca));
        verifyNoInteractions(colaboradorRepository);
        verify(trocaRepository, never()).save(any());
    }

    @Test
    void iniciaTrocaValidaComoPendente() {
        Colaborador solicitante = colaborador(1L, 100L);
        Colaborador aceitante = colaborador(2L, 100L);
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(colaboradorRepository.findById(2L)).thenReturn(Optional.of(aceitante));
        when(trocaRepository.save(any(TrocaPlantao.class))).thenAnswer(inv -> inv.getArgument(0));
        TrocaPlantao salva = service.solicitar(troca(1L, 2L));
        assertEquals(StatusTrocaPlantao.PENDENTE, salva.getStatusAprovacao());
        assertNull(salva.getAprovadoPor());
        assertNull(salva.getDataDecisao());
        verify(trocaRepository).save(salva);
    }

    private Colaborador colaborador(Long id, Long funcaoId) {
        FuncaoProfissional funcao = new FuncaoProfissional();
        funcao.setId(funcaoId);
        Colaborador c = new Colaborador();
        c.setId(id);
        c.setNome("Colaborador " + id);
        c.setAtivo(true);
        c.setFuncao(funcao);
        return c;
    }

    private TrocaPlantao troca(Long solicitanteId, Long aceitanteId) {
        Colaborador solicitante = new Colaborador(); solicitante.setId(solicitanteId);
        Colaborador aceitante = new Colaborador(); aceitante.setId(aceitanteId);
        TrocaPlantao troca = new TrocaPlantao();
        troca.setSolicitante(solicitante);
        troca.setAceitante(aceitante);
        troca.setDataPlantaoOriginal(LocalDate.of(2026, 10, 22));
        troca.setMotivo("Cobertura de plantão");
        return troca;
    }
}
