package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.Ferias;
import com.marceloaleixo.centrocirurgico.enums.StatusFerias;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.FeriasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FeriasService {
    private final FeriasRepository feriasRepository;
    private final ColaboradorRepository colaboradorRepository;
    private static final List<StatusFerias> STATUS_QUE_BLOQUEIAM = List.of(StatusFerias.SOLICITADA, StatusFerias.APROVADA);

    public FeriasService(FeriasRepository feriasRepository, ColaboradorRepository colaboradorRepository) {
        this.feriasRepository = feriasRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Transactional(readOnly = true)
    public List<Ferias> listarTodas() {
        return feriasRepository.findAllByOrderByDataInicioDesc();
    }

    @Transactional(readOnly = true)
    public List<Ferias> listarPorColaborador(Long colaboradorId) {
        return feriasRepository.findByColaboradorIdOrderByDataInicioDesc(colaboradorId);
    }

    @Transactional(readOnly = true)
    public Ferias buscarPorId(Long id) {
        return feriasRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Registro de férias não encontrado: " + id));
    }

    public Ferias solicitar(Ferias ferias) {
        if (ferias == null || ferias.getColaborador() == null || ferias.getColaborador().getId() == null)
            throw new IllegalArgumentException("Informe o colaborador das férias.");
        validarPeriodo(ferias.getDataInicio(), ferias.getDataFim());
        Colaborador colaborador = colaboradorRepository.findById(ferias.getColaborador().getId())
                .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));
        if (!colaborador.isAtivo()) throw new IllegalStateException("Não é possível solicitar férias para colaborador inativo.");
        if (feriasRepository.existeSobreposicao(colaborador.getId(), ferias.getDataInicio(), ferias.getDataFim(),
                STATUS_QUE_BLOQUEIAM, ferias.getId()))
            throw new IllegalStateException("Já existe uma solicitação de férias sobreposta para esse colaborador.");
        ferias.setColaborador(colaborador);
        ferias.setStatus(StatusFerias.SOLICITADA);
        ferias.setAprovadoPor(null);
        return feriasRepository.save(ferias);
    }

    public Ferias aprovar(Long id, Long aprovadorId) {
        Ferias ferias = buscarPorId(id);
        if (ferias.getStatus() != StatusFerias.SOLICITADA)
            throw new IllegalStateException("Somente férias solicitadas podem ser aprovadas.");
        Colaborador aprovador = colaboradorRepository.findById(aprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Aprovador não encontrado."));
        if (!aprovador.isAtivo()) throw new IllegalStateException("O aprovador está inativo.");
        if (feriasRepository.existeSobreposicao(ferias.getColaborador().getId(), ferias.getDataInicio(), ferias.getDataFim(),
                List.of(StatusFerias.APROVADA), ferias.getId()))
            throw new IllegalStateException("O período conflita com férias já aprovadas.");
        ferias.setStatus(StatusFerias.APROVADA);
        ferias.setAprovadoPor(aprovador);
        return feriasRepository.save(ferias);
    }

    public Ferias rejeitar(Long id, Long aprovadorId) {
        Ferias ferias = buscarPorId(id);
        if (ferias.getStatus() != StatusFerias.SOLICITADA)
            throw new IllegalStateException("Somente férias solicitadas podem ser rejeitadas.");
        Colaborador aprovador = colaboradorRepository.findById(aprovadorId)
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado."));
        ferias.setStatus(StatusFerias.REJEITADA);
        ferias.setAprovadoPor(aprovador);
        return feriasRepository.save(ferias);
    }

    public Ferias cancelar(Long id) {
        Ferias f = buscarPorId(id);
        if (f.getStatus() != StatusFerias.SOLICITADA && f.getStatus() != StatusFerias.APROVADA)
            throw new IllegalStateException("Somente férias solicitadas ou aprovadas podem ser canceladas.");
        f.setStatus(StatusFerias.CANCELADA);
        return feriasRepository.save(f);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio))
            throw new IllegalArgumentException("O período de férias é inválido.");
    }
}
