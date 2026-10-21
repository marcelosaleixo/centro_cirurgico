package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirurgiaMutiraoServiceRegrasTest {
    @Mock private CirurgiaMutiraoRepository cirurgias;
    @Mock private DiaMutiraoRepository dias;
    @Mock private PacienteMutiraoLookupRepository pacientes;
    @Mock private ProcedimentoMutiraoLookupRepository procedimentos;
    private CirurgiaMutiraoService service;

    @BeforeEach void setUp() { service = new CirurgiaMutiraoService(cirurgias, dias, pacientes, procedimentos); }

    @Test void rejeitaHorarioFinalIgualAoInicial() {
        DiaMutirao dia = diaValido();
        when(dias.findById(1L)).thenReturn(Optional.of(dia));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvar(
                null, 1L, 2L, 3L, "Dr. Exemplo", LocalTime.of(8, 0), LocalTime.of(8, 0), "PROGRAMADA", null));
        assertEquals("O horário final deve ser posterior ao horário inicial.", ex.getMessage());
        verify(cirurgias, never()).save(any());
        verify(cirurgias, never()).existeConflito(anyLong(), anyLong(), any(), any());
    }

    @Test void rejeitaDataForaDoPeriodoDoMutirao() {
        EventoMutirao evento = new EventoMutirao();
        evento.setDataInicio(LocalDate.of(2026, 10, 10)); evento.setDataFim(LocalDate.of(2026, 10, 20));
        DiaMutirao dia = new DiaMutirao(); dia.setEvento(evento); dia.setData(LocalDate.of(2026, 10, 21));
        when(dias.findById(1L)).thenReturn(Optional.of(dia));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvar(
                null, 1L, 2L, 3L, "Dr. Exemplo", LocalTime.of(8, 0), LocalTime.of(9, 0), "PROGRAMADA", null));
        assertEquals("A data do dia não pertence ao período do evento.", ex.getMessage());
        verify(cirurgias, never()).save(any());
    }

    @Test void rejeitaSobreposicaoDeCirurgiasNoMesmoDia() {
        when(dias.findById(1L)).thenReturn(Optional.of(diaValido()));
        when(cirurgias.existeConflito(1L, 0L, LocalTime.of(8, 30), LocalTime.of(9, 30))).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvar(
                null, 1L, 2L, 3L, "Dr. Exemplo", LocalTime.of(8, 30), LocalTime.of(9, 30), "PROGRAMADA", null));
        assertEquals("Já existe uma cirurgia com horário sobreposto neste dia.", ex.getMessage());
        verifyNoInteractions(pacientes, procedimentos);
        verify(cirurgias, never()).save(any());
    }

    private DiaMutirao diaValido() {
        EventoMutirao evento = new EventoMutirao();
        evento.setDataInicio(LocalDate.of(2026, 10, 10)); evento.setDataFim(LocalDate.of(2026, 10, 20));
        DiaMutirao dia = new DiaMutirao(); dia.setId(1L); dia.setEvento(evento); dia.setData(LocalDate.of(2026, 10, 15));
        return dia;
    }
}
