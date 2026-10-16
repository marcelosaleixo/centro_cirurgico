package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.entity.AuditoriaFechamentoRh;
import com.marceloaleixo.centrocirurgico.repository.AuditoriaFechamentoRhRepository;
import com.marceloaleixo.centrocirurgico.entity.ConferenciaFechamentoRh;
import com.marceloaleixo.centrocirurgico.repository.ConferenciaFechamentoRhRepository;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.FechamentoRhPeriodo;
import com.marceloaleixo.centrocirurgico.repository.FechamentoRhPeriodoRepository;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.repository.EscalaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.web.ResumoFechamentoRh;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class FechamentoRhService {
    private final EscalaPlantaoRepository escalaRepository;
    private final ConferenciaFechamentoRhRepository conferenciaRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final FechamentoRhPeriodoRepository periodoRepository;
    private final AuditoriaFechamentoRhRepository auditoriaRepository;

    public FechamentoRhService(EscalaPlantaoRepository escalaRepository,
                               ConferenciaFechamentoRhRepository conferenciaRepository,
                               ColaboradorRepository colaboradorRepository,
                               FechamentoRhPeriodoRepository periodoRepository,
                               AuditoriaFechamentoRhRepository auditoriaRepository) {
        this.escalaRepository = escalaRepository;
        this.conferenciaRepository = conferenciaRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.periodoRepository = periodoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ResumoFechamentoRh> consultar(LocalDate inicio, LocalDate fim) {
        Map<Long, ResumoFechamentoRh> resumos = new LinkedHashMap<>();
        List<EscalaPlantao> escalas = escalaRepository.findByDataPlantaoBetweenOrderByDataPlantaoAsc(inicio, fim);
        for (EscalaPlantao escala : escalas) {
            if (escala.getColaborador() == null || escala.getColaborador().getId() == null) continue;
            Long id = escala.getColaborador().getId();
            ResumoFechamentoRh resumo = resumos.computeIfAbsent(id, k -> new ResumoFechamentoRh(escala.getColaborador()));
            StatusEscala status = escala.getStatus();
            if (status == StatusEscala.FERIAS) resumo.registrarFerias();
            else if (status == StatusEscala.BANCO_HORAS) resumo.registrarBancoHoras();
            else if (status == StatusEscala.CANCELADO) resumo.registrarCancelado();
            else if (status == StatusEscala.PROGRAMADO || status == StatusEscala.SUBSTITUIDO) {
                resumo.registrarProgramado(minutosTrabalhados(escala));
            } else if (status == StatusEscala.REALIZADO) {
                resumo.registrarRealizado(minutosTrabalhados(escala));
            }
            LocalDate data = escala.getDataPlantao();
            if (data != null && (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY)) resumo.registrarFimDeSemana();
            String observacao = escala.getObservacao();
            if (observacao != null && (observacao.toLowerCase(Locale.ROOT).contains("feriado") || observacao.toLowerCase(Locale.ROOT).contains("nossa senhora") || observacao.toLowerCase(Locale.ROOT).contains("independência"))) resumo.registrarFeriado();
        }
        Map<Long, ConferenciaFechamentoRh> conferencias = new HashMap<>();
        for (ConferenciaFechamentoRh c : conferenciaRepository.findByPeriodoInicioAndPeriodoFim(inicio, fim)) {
            if (c.getColaborador() != null) conferencias.put(c.getColaborador().getId(), c);
        }
        for (ResumoFechamentoRh resumo : resumos.values()) {
            Long id = resumo.getColaborador().getId();
            ConferenciaFechamentoRh c = conferencias.get(id);
            if (c != null) {
                resumo.setStatusConferencia(c.getStatus());
                resumo.setObservacaoConferencia(c.getObservacao());
            }
        }
        return new ArrayList<>(resumos.values());
    }

    @Transactional
    public void registrarConferencia(LocalDate inicio, LocalDate fim, Long colaboradorId, String status, String observacao) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Informe um período válido para a conferência.");
        }
        if (periodoRepository.findByPeriodoInicioAndPeriodoFim(inicio, fim).map(FechamentoRhPeriodo::isFechado).orElse(false)) {
            throw new IllegalArgumentException("Este período está fechado. Reabra o fechamento antes de alterar conferências.");
        }
        if (!Set.of("PENDENTE", "CONFERIDO", "DIVERGENCIA").contains(status)) {
            throw new IllegalArgumentException("Status de conferência inválido.");
        }
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));
        ConferenciaFechamentoRh conferencia = conferenciaRepository
                .findByPeriodoInicioAndPeriodoFimAndColaboradorId(inicio, fim, colaboradorId)
                .orElseGet(ConferenciaFechamentoRh::new);
        conferencia.setPeriodoInicio(inicio);
        conferencia.setPeriodoFim(fim);
        conferencia.setColaborador(colaborador);
        String statusAnterior = conferencia.getId() == null ? null : conferencia.getStatus();
        String nomeColaborador = colaborador.getNome();
        conferencia.setStatus(status);
        String nota = observacao == null ? "" : observacao.trim();
        if (nota.length() > 1000) nota = nota.substring(0, 1000);
        conferencia.setObservacao(nota.isBlank() ? null : nota);
        conferenciaRepository.save(conferencia);
        registrarAuditoria(inicio, fim, nomeColaborador, "CONFERENCIA_ATUALIZADA", statusAnterior, status,
                nota.isBlank() ? null : nota);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaFechamentoRh> consultarAuditoria(LocalDate inicio, LocalDate fim) {
        return auditoriaRepository.findByPeriodoInicioAndPeriodoFimOrderByCriadoEmDesc(inicio, fim);
    }

    private void registrarAuditoria(LocalDate inicio, LocalDate fim, String colaborador, String acao,
                                    String statusAnterior, String statusNovo, String detalhe) {
        AuditoriaFechamentoRh evento = new AuditoriaFechamentoRh();
        evento.setPeriodoInicio(inicio);
        evento.setPeriodoFim(fim);
        evento.setColaboradorNome(colaborador);
        evento.setAcao(acao);
        evento.setStatusAnterior(statusAnterior);
        evento.setStatusNovo(statusNovo);
        if (detalhe != null && detalhe.length() > 1000) detalhe = detalhe.substring(0, 1000);
        evento.setDetalhe(detalhe);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String responsavel = authentication != null && authentication.isAuthenticated()
                && authentication.getName() != null ? authentication.getName() : "sistema";
        evento.setResponsavel(responsavel.length() > 120 ? responsavel.substring(0, 120) : responsavel);
        auditoriaRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Optional<FechamentoRhPeriodo> buscarFechamento(LocalDate inicio, LocalDate fim) {
        return periodoRepository.findByPeriodoInicioAndPeriodoFim(inicio, fim);
    }

    @Transactional
    public void fecharPeriodo(LocalDate inicio, LocalDate fim, String observacao) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Informe um período válido para fechar.");
        }
        FechamentoRhPeriodo existente = periodoRepository.findByPeriodoInicioAndPeriodoFim(inicio, fim)
                .orElseGet(FechamentoRhPeriodo::new);
        if (existente.isFechado()) throw new IllegalArgumentException("Este período já está fechado.");

        List<ResumoFechamentoRh> resumos = consultar(inicio, fim);
        if (resumos.isEmpty()) throw new IllegalArgumentException("Não é possível fechar um período sem escalas no intervalo.");
        List<String> pendencias = new ArrayList<>();
        for (ResumoFechamentoRh resumo : resumos) {
            if (!"CONFERIDO".equals(resumo.getStatusConferencia())) {
                String nome = resumo.getColaborador() != null ? resumo.getColaborador().getNome() : "Colaborador";
                pendencias.add(nome);
            }
        }
        if (!pendencias.isEmpty()) {
            String nomes = String.join(", ", pendencias.subList(0, Math.min(5, pendencias.size())));
            String sufixo = pendencias.size() > 5 ? " e mais " + (pendencias.size() - 5) + " colaborador(es)" : "";
            throw new IllegalArgumentException("Fechamento bloqueado: todos os colaboradores precisam estar como CONFERIDO. Pendências: " + nomes + sufixo + ".");
        }
        existente.setPeriodoInicio(inicio);
        existente.setPeriodoFim(fim);
        existente.setStatus("FECHADO");
        existente.setFechadoEm(java.time.OffsetDateTime.now());
        String nota = observacao == null ? "" : observacao.trim();
        if (nota.length() > 1000) nota = nota.substring(0, 1000);
        existente.setObservacaoFechamento(nota.isBlank() ? null : nota);
        periodoRepository.save(existente);
        registrarAuditoria(inicio, fim, null, "PERIODO_FECHADO", "ABERTO", "FECHADO",
                nota.isBlank() ? "Fechamento mensal concluído." : nota);
    }

    @Transactional
    public void reabrirPeriodo(LocalDate inicio, LocalDate fim, String justificativa) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Informe um período válido para reabrir.");
        }
        String motivo = justificativa == null ? "" : justificativa.trim();
        if (motivo.length() < 10) {
            throw new IllegalArgumentException("Informe uma justificativa com pelo menos 10 caracteres.");
        }
        if (motivo.length() > 1000) motivo = motivo.substring(0, 1000);

        FechamentoRhPeriodo fechamento = periodoRepository.findByPeriodoInicioAndPeriodoFim(inicio, fim)
                .orElseThrow(() -> new IllegalArgumentException("Não existe fechamento cadastrado para este período."));
        if (!fechamento.isFechado()) {
            throw new IllegalArgumentException("O período já está aberto.");
        }

        fechamento.setStatus("ABERTO");
        fechamento.setFechadoEm(null);
        periodoRepository.save(fechamento);
        registrarAuditoria(inicio, fim, null, "PERIODO_REABERTO", "FECHADO", "ABERTO", motivo);
    }

    private long minutosTrabalhados(EscalaPlantao escala) {
        LocalTime inicio = escala.getHoraInicio();
        LocalTime fim = escala.getHoraFim();
        if (inicio == null || fim == null) return 0;
        long minutos = Duration.between(inicio, fim).toMinutes();
        if (minutos < 0) minutos += 24 * 60;
        LocalTime inicioIntervalo = escala.getHoraInicioIntervalo();
        LocalTime fimIntervalo = escala.getHoraFimIntervalo();
        if (inicioIntervalo != null && fimIntervalo != null) {
            long intervalo = Duration.between(inicioIntervalo, fimIntervalo).toMinutes();
            if (intervalo < 0) intervalo += 24 * 60;
            minutos -= Math.max(0, intervalo);
        }
        return Math.max(0, minutos);
    }
}
