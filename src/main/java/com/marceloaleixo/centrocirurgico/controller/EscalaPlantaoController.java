package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.TrocaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.service.EscalaPlantaoService;
import com.marceloaleixo.centrocirurgico.service.CalendarioFeriadosService;
import com.marceloaleixo.centrocirurgico.web.EscalaPlantaoForm;
import com.marceloaleixo.centrocirurgico.web.DiaCalendarioEscala;
import com.marceloaleixo.centrocirurgico.web.ResultadoGeracaoEscala;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/escalas")
public class EscalaPlantaoController {

    private final EscalaPlantaoService escalaService;
    private final ColaboradorRepository colaboradorRepository;
    private final TrocaPlantaoRepository trocaPlantaoRepository;
    private final CalendarioFeriadosService calendarioFeriadosService;

    public EscalaPlantaoController(EscalaPlantaoService escalaService,
                                   ColaboradorRepository colaboradorRepository,
                                   TrocaPlantaoRepository trocaPlantaoRepository,
                                   CalendarioFeriadosService calendarioFeriadosService) {
        this.escalaService = escalaService;
        this.colaboradorRepository = colaboradorRepository;
        this.trocaPlantaoRepository = trocaPlantaoRepository;
        this.calendarioFeriadosService = calendarioFeriadosService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(required = false) Long colaboradorId,
            Model model) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio;
        LocalDate dataFim;
        try {
            dataInicio = parseDataOpcional(inicio);
            dataFim = parseDataOpcional(fim);
        } catch (IllegalArgumentException ex) {
            dataInicio = hoje.withDayOfMonth(1);
            dataFim = hoje.withDayOfMonth(hoje.lengthOfMonth());
            model.addAttribute("erro", ex.getMessage() + " Exibindo o mês atual.");
        }
        if (dataInicio == null) dataInicio = hoje.withDayOfMonth(1);
        if (dataFim == null) dataFim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        model.addAttribute("escalas", colaboradorId == null
                ? escalaService.listarPorPeriodo(dataInicio, dataFim)
                : escalaService.listarPorColaborador(colaboradorId, dataInicio, dataFim));
        model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("inicio", dataInicio);
        model.addAttribute("fim", dataFim);
        model.addAttribute("colaboradorId", colaboradorId);
        model.addAttribute("feriadosPeriodo", calendarioFeriadosService.feriadosDoPeriodo(dataInicio, dataFim));
        return "escalas/lista";
    }

    @GetMapping("/calendario")
    public String calendario(@RequestParam(required = false) String mes,
                             @RequestParam(required = false) Long colaboradorId,
                             Model model) {
        YearMonth referencia;
        try {
            referencia = (mes == null || mes.isBlank())
                    ? YearMonth.now()
                    : YearMonth.parse(mes);
        } catch (java.time.format.DateTimeParseException ex) {
            referencia = YearMonth.now();
            model.addAttribute("erro", "Mês inválido. Exibindo o mês atual.");
        }

        LocalDate primeiroDia = referencia.atDay(1);
        LocalDate ultimoDia = referencia.atEndOfMonth();
        List<EscalaPlantao> escalas = colaboradorId == null
                ? escalaService.listarPorPeriodo(primeiroDia, ultimoDia)
                : escalaService.listarPorColaborador(colaboradorId, primeiroDia, ultimoDia);

        Map<LocalDate, List<EscalaPlantao>> escalasPorData = new LinkedHashMap<>();
        for (EscalaPlantao escala : escalas) {
            escalasPorData.computeIfAbsent(escala.getDataPlantao(), k -> new ArrayList<>()).add(escala);
        }

        // Carrega as trocas que tenham a data original ou a data de destino dentro do mês.
        // A troca é exibida para os dois participantes; com filtro, somente para o colaborador selecionado.
        List<TrocaPlantao> trocasPeriodo = trocaPlantaoRepository.buscarTrocasDoPeriodo(primeiroDia, ultimoDia);
        if (colaboradorId != null) {
            trocasPeriodo = trocasPeriodo.stream()
                    .filter(t -> Objects.equals(t.getSolicitante().getId(), colaboradorId)
                            || Objects.equals(t.getAceitante().getId(), colaboradorId))
                    .toList();
        }
        Map<LocalDate, List<TrocaPlantao>> trocasPorData = new LinkedHashMap<>();
        for (TrocaPlantao troca : trocasPeriodo) {
            LocalDate dataOriginal = troca.getDataPlantaoOriginal();
            LocalDate dataDestino = troca.getDataPlantaoDestino();
            if (dataOriginal != null && !dataOriginal.isBefore(primeiroDia) && !dataOriginal.isAfter(ultimoDia)) {
                trocasPorData.computeIfAbsent(dataOriginal, k -> new ArrayList<>()).add(troca);
            }
            if (dataDestino != null && !dataDestino.equals(dataOriginal)
                    && !dataDestino.isBefore(primeiroDia) && !dataDestino.isAfter(ultimoDia)) {
                trocasPorData.computeIfAbsent(dataDestino, k -> new ArrayList<>()).add(troca);
            }
        }

        // Monta os dias já associados às escalas e às trocas no Java, sem lookups de Map no Thymeleaf.
        LocalDate inicioGrade = primeiroDia.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fimGrade = ultimoDia.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<List<DiaCalendarioEscala>> semanas = new ArrayList<>();
        for (LocalDate cursor = inicioGrade; !cursor.isAfter(fimGrade); cursor = cursor.plusWeeks(1)) {
            List<DiaCalendarioEscala> semana = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate data = cursor.plusDays(i);
                semana.add(new DiaCalendarioEscala(
                        data,
                        escalasPorData.getOrDefault(data, List.of()),
                        trocasPorData.getOrDefault(data, List.of()),
                        data.getMonthValue() == referencia.getMonthValue()
                                && data.getYear() == referencia.getYear(),
                        calendarioFeriadosService.nomeFeriado(data)));
            }
            semanas.add(semana);
        }

        model.addAttribute("mesReferencia", referencia);
        model.addAttribute("mesAnterior", referencia.minusMonths(1));
        model.addAttribute("proximoMes", referencia.plusMonths(1));
        model.addAttribute("semanas", semanas);
        model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("colaboradorId", colaboradorId);
        return "escalas/calendario";
    }

    @PostMapping("/calendario/gerar")
    public String gerarCalendario(@RequestParam String mes,
                                  @RequestParam(required = false) Long colaboradorId,
                                  RedirectAttributes redirect) {
        YearMonth referencia;
        try {
            referencia = YearMonth.parse(mes);
        } catch (java.time.format.DateTimeParseException ex) {
            redirect.addFlashAttribute("erro", "Mês inválido. Selecione um mês válido para gerar as escalas.");
            return "redirect:/escalas/calendario";
        }

        LocalDate inicio = referencia.atDay(1);
        LocalDate fim = referencia.atEndOfMonth();
        List<Colaborador> alvos = colaboradorId == null
                ? colaboradorRepository.findByAtivoTrueOrderByNomeAsc()
                : colaboradorRepository.findById(colaboradorId)
                    .filter(Colaborador::isAtivo)
                    .map(List::of)
                    .orElse(List.of());

        if (alvos.isEmpty()) {
            redirect.addFlashAttribute("erro", "Nenhum colaborador ativo foi encontrado para gerar a escala.");
            redirect.addAttribute("mes", referencia);
            return "redirect:/escalas/calendario";
        }

        int colaboradoresProcessados = 0;
        int diasProgramados = 0;
        int diasFerias = 0;
        int diasBancoHoras = 0;
        List<String> falhas = new ArrayList<>();

        for (Colaborador colaborador : alvos) {
            try {
                ResultadoGeracaoEscala resultado =
                        escalaService.gerarPeriodoDetalhado(colaborador.getId(), inicio, fim);
                colaboradoresProcessados++;
                diasProgramados += resultado.programados();
                diasFerias += resultado.ferias();
                diasBancoHoras += resultado.bancoHoras();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                falhas.add(colaborador.getNome() + ": " + ex.getMessage());
            }
        }

        if (colaboradoresProcessados > 0) {
            redirect.addFlashAttribute("sucesso",
                    "Calendário gerado para " + colaboradoresProcessados + " colaborador(es). "
                    + diasProgramados + " dia(s) programado(s), " + diasFerias
                    + " dia(s) de férias e " + diasBancoHoras + " dia(s) de banco de horas.");
        }
        if (!falhas.isEmpty()) {
            String resumo = String.join(" | ", falhas);
            if (resumo.length() > 1800) resumo = resumo.substring(0, 1800) + "...";
            redirect.addFlashAttribute("erro",
                    "Alguns colaboradores não foram gerados. Verifique regime, data de referência e conflitos: " + resumo);
        }
        redirect.addAttribute("mes", referencia.toString());
        if (colaboradorId != null) redirect.addAttribute("colaboradorId", colaboradorId);
        return "redirect:/escalas/calendario";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        EscalaPlantaoForm form = new EscalaPlantaoForm();
        form.setDataPlantao(LocalDate.now());
        model.addAttribute("form", form);
        prepararFormulario(model);
        return "escalas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        EscalaPlantao escala = escalaService.buscarPorId(id);
        EscalaPlantaoForm form = new EscalaPlantaoForm();
        form.setId(escala.getId());
        form.setColaboradorId(escala.getColaborador().getId());
        form.setDataPlantao(escala.getDataPlantao());
        form.setHoraInicio(escala.getHoraInicio());
        form.setHoraFim(escala.getHoraFim());
        form.setHoraInicioIntervalo(escala.getHoraInicioIntervalo());
        form.setHoraFimIntervalo(escala.getHoraFimIntervalo());
        form.setTipoPlantao(escala.getTipoPlantao());
        form.setStatus(escala.getStatus());
        form.setObservacao(escala.getObservacao());
        model.addAttribute("form", form);
        prepararFormulario(model);
        return "escalas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("form") EscalaPlantaoForm form,
                         Model model, RedirectAttributes redirect) {
        try {
            EscalaPlantao escala = form.getId() == null
                    ? new EscalaPlantao() : escalaService.buscarPorId(form.getId());
            Colaborador referencia = new Colaborador();
            referencia.setId(form.getColaboradorId());
            escala.setColaborador(referencia);
            escala.setDataPlantao(form.getDataPlantao());
            escala.setHoraInicio(form.getHoraInicio());
            escala.setHoraFim(form.getHoraFim());
            escala.setHoraInicioIntervalo(form.getHoraInicioIntervalo());
            escala.setHoraFimIntervalo(form.getHoraFimIntervalo());
            escala.setTipoPlantao(form.getTipoPlantao());
            escala.setStatus(form.getStatus() == null ? StatusEscala.PROGRAMADO : form.getStatus());
            escala.setObservacao(form.getObservacao());
            escalaService.salvar(escala);
            redirect.addFlashAttribute("sucesso", "Escala salva com sucesso.");
            return "redirect:/escalas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            prepararFormulario(model);
            return "escalas/formulario";
        }
    }

    @PostMapping("/gerar")
    public String gerar(@RequestParam Long colaboradorId,
                        @RequestParam String inicio,
                        @RequestParam String fim,
                        RedirectAttributes redirect) {
        LocalDate dataInicio = null;
        LocalDate dataFim = null;
        try {
            dataInicio = parseDataObrigatoria(inicio, "Data inicial");
            dataFim = parseDataObrigatoria(fim, "Data final");
            ResultadoGeracaoEscala resultado = escalaService.gerarPeriodoDetalhado(colaboradorId, dataInicio, dataFim);
            redirect.addFlashAttribute("sucesso", "Geração concluída: " + resultado.programados()
                    + " plantão(ões) programado(s), " + resultado.ferias() + " dia(s) marcado(s) como FÉRIAS e "
                    + resultado.bancoHoras() + " dia(s) marcado(s) como BANCO_HORAS.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        if (dataInicio != null) redirect.addAttribute("inicio", dataInicio.toString());
        if (dataFim != null) redirect.addAttribute("fim", dataFim.toString());
        redirect.addAttribute("colaboradorId", colaboradorId);
        return "redirect:/escalas";
    }

    /**
     * Aceita o formato padrão de input type=date (yyyy-MM-dd) e o formato
     * legado dd/MM/yyyy para compatibilidade com links antigos.
     */
    private LocalDate parseDataOpcional(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return parseDataObrigatoria(valor, "Data");
    }

    private LocalDate parseDataObrigatoria(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatória.");
        }
        String normalizado = valor.trim();
        try {
            return LocalDate.parse(normalizado, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(normalizado, DateTimeFormatter.ofPattern("dd/MM/uuuu"));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(campo
                        + " inválida. Use o formato dd/MM/aaaa ou a data do seletor do navegador.");
            }
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            escalaService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Escala excluída com sucesso.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/escalas";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("tiposPlantao", TipoPlantao.values());
        model.addAttribute("statusEscala", StatusEscala.values());
    }
}
