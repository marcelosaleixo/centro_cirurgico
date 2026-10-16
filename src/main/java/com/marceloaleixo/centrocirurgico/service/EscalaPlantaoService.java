package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.Ferias;
import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.enums.StatusFerias;
import com.marceloaleixo.centrocirurgico.enums.StatusBancoHoras;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import com.marceloaleixo.centrocirurgico.repository.BancoHorasRepository;
import com.marceloaleixo.centrocirurgico.web.ResultadoGeracaoEscala;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.EscalaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.repository.FeriasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class EscalaPlantaoService {

    private final EscalaPlantaoRepository escalaRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final FeriasRepository feriasRepository;
    private final BancoHorasRepository bancoHorasRepository;
    private final CalendarioFeriadosService calendarioFeriadosService;

    public EscalaPlantaoService(EscalaPlantaoRepository escalaRepository,
                                ColaboradorRepository colaboradorRepository,
                                FeriasRepository feriasRepository,
                                BancoHorasRepository bancoHorasRepository,
                                CalendarioFeriadosService calendarioFeriadosService) {
        this.escalaRepository = escalaRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.feriasRepository = feriasRepository;
        this.bancoHorasRepository = bancoHorasRepository;
        this.calendarioFeriadosService = calendarioFeriadosService;
    }

    @Transactional(readOnly = true)
    public List<EscalaPlantao> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return escalaRepository.findByDataPlantaoBetweenOrderByDataPlantaoAsc(inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<EscalaPlantao> listarPorColaborador(Long colaboradorId, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return escalaRepository.findByColaboradorIdAndDataPlantaoBetweenOrderByDataPlantaoAsc(
                colaboradorId, inicio, fim);
    }

    @Transactional(readOnly = true)
    public EscalaPlantao buscarPorId(Long id) {
        return escalaRepository.findDetalhadaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Escala não encontrada: " + id));
    }

    public EscalaPlantao salvar(EscalaPlantao escala) {
        if (escala == null || escala.getColaborador() == null || escala.getColaborador().getId() == null) {
            throw new IllegalArgumentException("Informe o colaborador da escala.");
        }
        if (escala.getDataPlantao() == null || escala.getTipoPlantao() == null) {
            throw new IllegalArgumentException("Informe data e classificação do plantão.");
        }
        if (escala.getHoraInicio() == null || escala.getHoraFim() == null
                || escala.getHoraInicio().equals(escala.getHoraFim())) {
            throw new IllegalArgumentException("Informe horários de início e fim diferentes.");
        }
        if (escala.getStatus() == null) {
            escala.setStatus(StatusEscala.PROGRAMADO);
        }

        Colaborador colaborador = colaboradorRepository.findById(escala.getColaborador().getId())
                .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));

        if (!colaborador.isAtivo()) {
            throw new IllegalStateException("Não é possível escalar colaborador inativo.");
        }
        if (colaborador.getDataReferenciaPlantao() == null) {
            throw new IllegalStateException("O colaborador precisa ter uma data de referência cadastrada.");
        }
        if (escala.getDataPlantao().isBefore(colaborador.getDataReferenciaPlantao())) {
            throw new IllegalArgumentException("A data da escala não pode anteceder a data de referência do colaborador.");
        }
        if (colaborador.getRegimeTrabalho() == RegimeTrabalho.DOZE_TRINTA_SEIS) {
            if (colaborador.getPlantaoReferencia() == null) {
                throw new IllegalStateException("O colaborador precisa ter plantão de referência cadastrado.");
            }
            long diasDesdeReferencia = ChronoUnit.DAYS.between(
                    colaborador.getDataReferenciaPlantao(), escala.getDataPlantao());
            if (diasDesdeReferencia % 2 != 0) {
                throw new IllegalArgumentException("No regime 12x36, o colaborador deve ser escalado em dias alternados a partir da data de referência.");
            }
            if (escala.getTipoPlantao() != colaborador.getPlantaoReferencia()) {
                throw new IllegalArgumentException("O tipo de plantão deve corresponder à referência do colaborador: " + colaborador.getPlantaoReferencia() + ".");
            }
            if (escala.getHoraInicioIntervalo() != null || escala.getHoraFimIntervalo() != null) {
                throw new IllegalArgumentException("O intervalo configurado nesta etapa é aplicável ao regime 5x2.");
            }
        } else {
            if (escala.getDataPlantao().getDayOfWeek().getValue() > 5) {
                throw new IllegalArgumentException("No regime 5x2, os dias de trabalho são de segunda a sexta-feira.");
            }
            if (escala.getHoraInicioIntervalo() == null || escala.getHoraFimIntervalo() == null
                    || !escala.getHoraInicio().isBefore(escala.getHoraInicioIntervalo())
                    || !escala.getHoraInicioIntervalo().isBefore(escala.getHoraFimIntervalo())
                    || !escala.getHoraFimIntervalo().isBefore(escala.getHoraFim())) {
                throw new IllegalArgumentException("No regime 5x2, informe o intervalo dentro do horário de trabalho (padrão 11:00 às 13:00).");
            }
        }

        boolean emFerias = feriasRepository.existeSobreposicao(
                colaborador.getId(), escala.getDataPlantao(), escala.getDataPlantao(),
                List.of(StatusFerias.SOLICITADA, StatusFerias.APROVADA), null);
        boolean emBancoHoras = bancoHorasRepository.existsByColaboradorIdAndDataBancoAndStatus(
                colaborador.getId(), escala.getDataPlantao(), StatusBancoHoras.REGISTRADO);
        if (emFerias && escala.getStatus() != StatusEscala.FERIAS && escala.getStatus() != StatusEscala.CANCELADO) {
            throw new IllegalStateException("Essa data está coberta por férias. A escala deve estar marcada como FÉRIAS.");
        }
        if (escala.getStatus() == StatusEscala.FERIAS && !emFerias) {
            throw new IllegalStateException("Só é possível marcar FÉRIAS quando existe um registro de férias ativo nessa data.");
        }
        if (emBancoHoras && escala.getStatus() != StatusEscala.BANCO_HORAS && escala.getStatus() != StatusEscala.CANCELADO) {
            throw new IllegalStateException("Existe lançamento de banco de horas nessa data. A escala deve estar marcada como BANCO_HORAS.");
        }
        if (escala.getStatus() == StatusEscala.BANCO_HORAS && !emBancoHoras) {
            throw new IllegalStateException("Não existe lançamento ativo de banco de horas para essa data.");
        }
        if (emFerias && emBancoHoras) {
            throw new IllegalStateException("A data está simultaneamente em férias e banco de horas. Corrija os lançamentos antes de salvar a escala.");
        }

        boolean dataOcupada = escala.getId() == null
                ? escalaRepository.existsByColaboradorIdAndDataPlantao(colaborador.getId(), escala.getDataPlantao())
                : escalaRepository.existsByColaboradorIdAndDataPlantaoAndStatusNot(
                        colaborador.getId(), escala.getDataPlantao(), StatusEscala.CANCELADO)
                    && !escalaRepository.findById(escala.getId())
                        .map(atual -> atual.getDataPlantao().equals(escala.getDataPlantao())
                                && atual.getColaborador().getId().equals(colaborador.getId()))
                        .orElse(false);
        if (dataOcupada) {
            throw new IllegalStateException("Já existe uma escala cadastrada para esse colaborador nessa data.");
        }

        escala.setColaborador(colaborador);
        return escalaRepository.save(escala);
    }

    /**
     * Compatibilidade com chamadas existentes: retorna o total de registros criados.
     */
    public int gerarPeriodo(Long colaboradorId, LocalDate inicio, LocalDate fim) {
        return gerarPeriodoDetalhado(colaboradorId, inicio, fim).totalCriados();
    }

    /**
     * Gera as datas de plantão 12x36 e marca os dias cobertos por férias ou banco de horas.
     * Datas já cadastradas são preservadas para evitar duplicidade.
     */
    public ResultadoGeracaoEscala gerarPeriodoDetalhado(Long colaboradorId, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        if (ChronoUnit.DAYS.between(inicio, fim) > 365) {
            throw new IllegalArgumentException("A geração automática aceita períodos de até 366 dias.");
        }
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));
        if (!colaborador.isAtivo()) {
            throw new IllegalStateException("Não é possível gerar escala para colaborador inativo.");
        }
        if (colaborador.getDataReferenciaPlantao() == null || colaborador.getRegimeTrabalho() == null) {
            throw new IllegalStateException("Cadastre o regime de trabalho e a data de referência antes de gerar a escala.");
        }
        boolean regime12x36 = colaborador.getRegimeTrabalho() == RegimeTrabalho.DOZE_TRINTA_SEIS;
        if (regime12x36 && colaborador.getPlantaoReferencia() == null) {
            throw new IllegalStateException("Cadastre o plantão de referência antes de gerar a escala 12x36.");
        }

        int programados = 0;
        int ferias = 0;
        int bancoHoras = 0;
        for (LocalDate data = inicio; !data.isAfter(fim); data = data.plusDays(1)) {
            if (data.isBefore(colaborador.getDataReferenciaPlantao())) continue;
            if (regime12x36) {
                long dias = ChronoUnit.DAYS.between(colaborador.getDataReferenciaPlantao(), data);
                if (dias % 2 != 0) continue;
            } else if (data.getDayOfWeek().getValue() > 5) {
                // Sábado e domingo são descanso no regime 5x2.
                continue;
            }

            EscalaPlantao escalaExistente = escalaRepository.findByColaboradorIdAndDataPlantao(colaboradorId, data).orElse(null);
            if (escalaExistente != null && escalaExistente.getStatus() == StatusEscala.CANCELADO) continue;

            Ferias registroFerias = feriasRepository
                    .findFirstByColaboradorIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndStatusInOrderByDataInicioAsc(
                            colaboradorId, data, data, List.of(StatusFerias.SOLICITADA, StatusFerias.APROVADA))
                    .orElse(null);
            boolean emBancoHoras = bancoHorasRepository.existsByColaboradorIdAndDataBancoAndStatus(
                    colaboradorId, data, StatusBancoHoras.REGISTRADO);
            if (registroFerias != null && emBancoHoras) {
                throw new IllegalStateException("Conflito em " + data + ": o colaborador possui férias e banco de horas registrados. Resolva o conflito antes de gerar a escala.");
            }

            EscalaPlantao escala = escalaExistente == null ? new EscalaPlantao() : escalaExistente;
            escala.setColaborador(colaborador);
            escala.setDataPlantao(data);
            escala.setTipoPlantao(colaborador.getPlantaoReferencia());
            if (regime12x36) {
                escala.setHoraInicio(LocalTime.of(7, 0));
                escala.setHoraFim(LocalTime.of(19, 0));
                escala.setHoraInicioIntervalo(null);
                escala.setHoraFimIntervalo(null);
            } else {
                escala.setHoraInicio(LocalTime.of(7, 0));
                escala.setHoraFim(LocalTime.of(17, 0));
                escala.setHoraInicioIntervalo(LocalTime.of(11, 0));
                escala.setHoraFimIntervalo(LocalTime.of(13, 0));
            }

            if (registroFerias != null) {
                escala.setStatus(StatusEscala.FERIAS);
                escala.setObservacao("FÉRIAS " + registroFerias.getStatus() + " (" + registroFerias.getDataInicio() + " a " + registroFerias.getDataFim() + "). Ausência no regime " + (regime12x36 ? "12x36" : "5x2") + ".");
                ferias++;
            } else if (emBancoHoras) {
                escala.setStatus(StatusEscala.BANCO_HORAS);
                escala.setObservacao("Dia de banco de horas lançado para esta data.");
                bancoHoras++;
            } else {
                if (escalaExistente == null || escala.getStatus() == StatusEscala.FERIAS || escala.getStatus() == StatusEscala.BANCO_HORAS) {
                    escala.setStatus(StatusEscala.PROGRAMADO);
                    escala.setObservacao(regime12x36
                            ? "Gerado automaticamente para escala 12x36."
                            : "Gerado automaticamente para escala 5x2: 07:00-11:00, intervalo 11:00-13:00, trabalho 13:00-17:00.");
                }
                if (escala.getStatus() == StatusEscala.PROGRAMADO) programados++;
            }
            String nomeFeriado = calendarioFeriadosService.nomeFeriado(data);
            if (nomeFeriado != null) {
                String observacaoAtual = escala.getObservacao() == null ? "" : escala.getObservacao().trim();
                String anotacao = "FERIADO: " + nomeFeriado + ".";
                if (!observacaoAtual.contains(anotacao)) {
                    escala.setObservacao(observacaoAtual.isBlank() ? anotacao : observacaoAtual + " " + anotacao);
                }
            }
            String observacaoAtual = escala.getObservacao() == null ? "" : escala.getObservacao().trim();
            int inicioRegraAnterior = observacaoAtual.indexOf(" REGRA DO FERIADO: ");
            if (inicioRegraAnterior >= 0) observacaoAtual = observacaoAtual.substring(0, inicioRegraAnterior).trim();
            else if (observacaoAtual.startsWith("REGRA DO FERIADO: ")) observacaoAtual = "";
            String regraFeriado = calendarioFeriadosService.descricaoRegraOperacional(data);
            if (regraFeriado != null) {
                String anotacaoRegra = "REGRA DO FERIADO: " + regraFeriado + ".";
                observacaoAtual = observacaoAtual.isBlank() ? anotacaoRegra : observacaoAtual + " " + anotacaoRegra;
            }
            escala.setObservacao(observacaoAtual.isBlank() ? null : observacaoAtual);
            escalaRepository.save(escala);
        }
        return new ResultadoGeracaoEscala(programados, ferias, bancoHoras);
    }

    public void excluir(Long id) {
        escalaRepository.delete(buscarPorId(id));
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Informe um período válido: a data final deve ser igual ou posterior à inicial.");
        }
    }
}
