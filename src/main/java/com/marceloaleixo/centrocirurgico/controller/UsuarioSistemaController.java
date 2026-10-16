package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.entity.UsuarioSistema;
import com.marceloaleixo.centrocirurgico.service.GestaoUsuarioSistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioSistemaController {
    private final GestaoUsuarioSistemaService service;
    private final com.marceloaleixo.centrocirurgico.service.AuditoriaSegurancaService auditoria;

    public UsuarioSistemaController(GestaoUsuarioSistemaService service,
                                    com.marceloaleixo.centrocirurgico.service.AuditoriaSegurancaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "admin/usuarios-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new UsuarioSistemaForm());
        model.addAttribute("edicao", false);
        return "admin/usuarios-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            UsuarioSistema u = service.buscar(id);
            UsuarioSistemaForm form = new UsuarioSistemaForm();
            form.setId(u.getId());
            form.setUsername(u.getUsername());
            form.setPerfil(u.getPerfil());
            form.setAtivo(u.isAtivo());
            model.addAttribute("usuario", form);
            model.addAttribute("edicao", true);
            return "admin/usuarios-formulario";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") UsuarioSistemaForm form, RedirectAttributes redirect) {
        try {
            service.salvar(form.getId(), form.getUsername(), form.getPerfil(), form.getSenha(), form.isAtivo());
            auditoria.registrar(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName(),
                    form.getId() == null ? "USUARIO_CRIADO" : "USUARIO_ATUALIZADO",
                    "Conta alvo: " + form.getUsername() + "; perfil: " + form.getPerfil(), true);
            redirect.addFlashAttribute("sucesso", "Usuário salvo com sucesso.");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return form.getId() == null ? "redirect:/admin/usuarios/novo" : "redirect:/admin/usuarios/" + form.getId() + "/editar";
        }
    }

    @PostMapping("/{id}/alternar-status")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.alternarAtivo(id);
            auditoria.registrar(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName(),
                    "STATUS_USUARIO_ALTERADO", "ID da conta alvo: " + id, true);
            redirect.addFlashAttribute("sucesso", "Status do usuário atualizado.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    public static class UsuarioSistemaForm {
        private Long id;
        private String username;
        private String perfil = "RH";
        private String senha;
        private boolean ativo = true;
        public Long getId(){return id;} public void setId(Long id){this.id=id;}
        public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
        public String getPerfil(){return perfil;} public void setPerfil(String perfil){this.perfil=perfil;}
        public String getSenha(){return senha;} public void setSenha(String senha){this.senha=senha;}
        public boolean isAtivo(){return ativo;} public void setAtivo(boolean ativo){this.ativo=ativo;}
    }
}
