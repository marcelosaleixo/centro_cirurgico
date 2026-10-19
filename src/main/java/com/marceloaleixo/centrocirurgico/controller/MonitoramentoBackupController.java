package com.marceloaleixo.centrocirurgico.controller;

import com.marceloaleixo.centrocirurgico.service.MonitoramentoBackupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/backups")
@PreAuthorize("hasRole('ADMIN_RH')")
public class MonitoramentoBackupController {
    private final MonitoramentoBackupService service;
    public MonitoramentoBackupController(MonitoramentoBackupService service) { this.service = service; }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("painel", service.carregar());
        return "admin/monitoramento-backups";
    }
}
