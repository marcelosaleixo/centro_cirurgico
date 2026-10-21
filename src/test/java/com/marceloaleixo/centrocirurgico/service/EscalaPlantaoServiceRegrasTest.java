package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import com.marceloaleixo.centrocirurgico.repository.BancoHorasRepository;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.EscalaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.repository.FeriasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalaPlantaoServiceRegrasTest {
    @Mock private EscalaPlantaoRepository escalaRepository;
    @Mock private ColaboradorRepository colaboradorRepository;
    @Mock private FeriasRepository feriasRepository;
    @Mock private BancoHorasRepository bancoHorasRepository;
    @Mock private CalendarioFeriadosService calendarioFeriadosService;
    @InjectMocks private EscalaPlantaoService service;
    private Colaborador colaborador;

    @BeforeEach
    void preparar() {
        colaborador = new Colaborador();
        colaborador.setId(10L);
        colaborador.setAtivo(true);
        colaborador.setRegimeTrabalho(RegimeTrabalho.DOZE_TRINTA_SEIS);
        colaborador.setDataReferenciaPlantao(LocalDate.of(2026, 10, 20));
        colaborador.setPlantaoReferencia(TipoPlantao.PAR);
        lenient().when(colaboradorRepository.findById(10L)).thenReturn(Optional.of(colaborador));
        lenient().when(feriasRepository.existeSobreposicao(eq(10L), any(), any(), anyList(), isNull())).thenReturn(false);
        lenient().when(bancoHorasRepository.existsByColaboradorIdAndDataBancoAndStatus(eq(10L), any(), any())).thenReturn(false);
        lenient().when(escalaRepository.existsByColaboradorIdAndDataPlantao(eq(10L), any())).thenReturn(false);
    }

    @Test
    void aceitaEscala12x36NaDataAlternadaEComPlantaoCorrespondente() {
        EscalaPlantao escala = escala(LocalDate.of(2026, 10, 22), TipoPlantao.PAR);
        when(escalaRepository.save(any(EscalaPlantao.class))).thenAnswer(inv -> inv.getArgument(0));
        EscalaPlantao salva = service.salvar(escala);
        assertEquals(colaborador, salva.getColaborador());
        verify(escalaRepository).save(escala);
    }

    @Test
    void rejeitaEscala12x36EmDiaNaoAlternado() {
        EscalaPlantao escala = escala(LocalDate.of(2026, 10, 21), TipoPlantao.PAR);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(escala));
        verify(escalaRepository, never()).save(any());
    }

    @Test
    void rejeitaClassificacaoDePlantaoDiferenteDaReferencia() {
        EscalaPlantao escala = escala(LocalDate.of(2026, 10, 22), TipoPlantao.IMPAR);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(escala));
        verify(escalaRepository, never()).save(any());
    }

    @Test
    void rejeitaColaboradorInativo() {
        colaborador.setAtivo(false);
        assertThrows(IllegalStateException.class,
                () -> service.salvar(escala(LocalDate.of(2026, 10, 22), TipoPlantao.PAR)));
        verify(escalaRepository, never()).save(any());
    }

    private EscalaPlantao escala(LocalDate data, TipoPlantao tipo) {
        EscalaPlantao escala = new EscalaPlantao();
        escala.setColaborador(colaborador);
        escala.setDataPlantao(data);
        escala.setTipoPlantao(tipo);
        escala.setHoraInicio(LocalTime.of(7, 0));
        escala.setHoraFim(LocalTime.of(19, 0));
        return escala;
    }
}
