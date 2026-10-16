package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.Equipe;
import com.marceloaleixo.centrocirurgico.entity.FuncaoProfissional;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import com.marceloaleixo.centrocirurgico.service.ColaboradorService;
import com.marceloaleixo.centrocirurgico.service.EquipeService;
import com.marceloaleixo.centrocirurgico.service.FuncaoProfissionalService;
import com.marceloaleixo.centrocirurgico.service.AuditoriaAlteracaoService;
import java.util.LinkedHashMap;
import java.util.Map;
import com.marceloaleixo.centrocirurgico.web.ColaboradorForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/colaboradores")
public class ColaboradorController {
    private final ColaboradorService colaboradorService;
    private final FuncaoProfissionalService funcaoService;
    private final EquipeService equipeService;
    private final AuditoriaAlteracaoService auditoriaAlteracaoService;

    public ColaboradorController(ColaboradorService colaboradorService,
                                 FuncaoProfissionalService funcaoService,
                                 EquipeService equipeService, AuditoriaAlteracaoService auditoriaAlteracaoService) {
        this.colaboradorService = colaboradorService;
        this.funcaoService = funcaoService;
        this.equipeService = equipeService;
        this.auditoriaAlteracaoService = auditoriaAlteracaoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        var colaboradores = colaboradorService.listarTodos();
        if (busca != null && !busca.isBlank()) {
            String termo = busca.trim().toLowerCase();
            colaboradores = colaboradores.stream()
                    .filter(c -> c.getNome() != null && c.getNome().toLowerCase().contains(termo)
                            || c.getCpf() != null && c.getCpf().contains(termo)
                            || c.getEmail() != null && c.getEmail().toLowerCase().contains(termo))
                    .toList();
        }
        model.addAttribute("colaboradores", colaboradores);
        model.addAttribute("busca", busca == null ? "" : busca);
        return "colaboradores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        ColaboradorForm form = new ColaboradorForm();
        form.setAtivo(true);
        form.setPlantaoReferencia(TipoPlantao.PAR);
        form.setRegimeTrabalho(com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho.DOZE_TRINTA_SEIS);
        model.addAttribute("form", form);
        prepararFormulario(model);
        return "colaboradores/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Colaborador c = colaboradorService.buscarPorId(id);
        ColaboradorForm form = new ColaboradorForm();
        form.setId(c.getId());
        form.setNome(c.getNome());
        form.setCpf(c.getCpf());
        form.setRegistroProfissional(c.getRegistroProfissional());
        form.setEmail(c.getEmail());
        form.setTelefone(c.getTelefone());
        form.setFuncaoId(c.getFuncao() == null ? null : c.getFuncao().getId());
        form.setEquipeId(c.getEquipe() == null ? null : c.getEquipe().getId());
        form.setPlantaoReferencia(c.getPlantaoReferencia());
        form.setRegimeTrabalho(c.getRegimeTrabalho());
        form.setDataReferenciaPlantao(c.getDataReferenciaPlantao());
        form.setAtivo(c.isAtivo());
        model.addAttribute("form", form);
        prepararFormulario(model);
        return "colaboradores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("form") ColaboradorForm form,
                         Model model, RedirectAttributes redirect) {
        try {
            Colaborador c = form.getId() == null ? new Colaborador() : colaboradorService.buscarPorId(form.getId());
            boolean novoRegistro = form.getId() == null;
            Map<String, String> antes = novoRegistro ? Map.of() : snapshot(c);
            c.setNome(form.getNome());
            c.setCpf(form.getCpf());
            c.setRegistroProfissional(form.getRegistroProfissional());
            c.setEmail(form.getEmail());
            c.setTelefone(form.getTelefone());
            c.setFuncao(form.getFuncaoId() == null ? null : criarFuncaoRef(form.getFuncaoId()));
            c.setEquipe(form.getEquipeId() == null ? null : criarEquipeRef(form.getEquipeId()));
            c.setPlantaoReferencia(form.getPlantaoReferencia());
            c.setRegimeTrabalho(form.getRegimeTrabalho());
            c.setDataReferenciaPlantao(form.getDataReferenciaPlantao());
            c.setAtivo(form.isAtivo());
            colaboradorService.salvar(c);
            auditoriaAlteracaoService.registrarDiferencas("Colaborador", c.getId(),
                    novoRegistro ? "CRIACAO" : "EDICAO", antes, snapshot(c));
            redirect.addFlashAttribute("sucesso", "Colaborador salvo com sucesso.");
            return "redirect:/colaboradores";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("erro", ex.getMessage());
            prepararFormulario(model);
            return "colaboradores/formulario";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            colaboradorService.desativar(id);
            redirect.addFlashAttribute("sucesso", "Colaborador desativado. O histórico foi preservado.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/colaboradores";
    }


    /** Campos operacionais permitidos para auditoria; CPF, e-mail e telefone são deliberadamente excluídos. */
    private Map<String, String> snapshot(Colaborador c) {
        Map<String, String> dados = new LinkedHashMap<>();
        dados.put("nome", c.getNome());
        dados.put("funcaoId", c.getFuncao() == null || c.getFuncao().getId() == null ? null : c.getFuncao().getId().toString());
        dados.put("equipeId", c.getEquipe() == null || c.getEquipe().getId() == null ? null : c.getEquipe().getId().toString());
        dados.put("plantaoReferencia", c.getPlantaoReferencia() == null ? null : c.getPlantaoReferencia().name());
        dados.put("regimeTrabalho", c.getRegimeTrabalho() == null ? null : c.getRegimeTrabalho().name());
        dados.put("dataReferenciaPlantao", c.getDataReferenciaPlantao() == null ? null : c.getDataReferenciaPlantao().toString());
        dados.put("ativo", Boolean.toString(c.isAtivo()));
        return dados;
    }

    private FuncaoProfissional criarFuncaoRef(Long id) {
        FuncaoProfissional f = new FuncaoProfissional();
        f.setId(id);
        return f;
    }

    private Equipe criarEquipeRef(Long id) {
        Equipe e = new Equipe();
        e.setId(id);
        return e;
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("funcoes", funcaoService.listarTodos());
        model.addAttribute("equipes", equipeService.listarTodas());
        model.addAttribute("tiposPlantao", TipoPlantao.values());
        model.addAttribute("regimesTrabalho", RegimeTrabalho.values());
    }
}
