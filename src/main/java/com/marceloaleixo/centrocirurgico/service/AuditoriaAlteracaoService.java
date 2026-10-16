package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaAlteracao;
import com.marceloaleixo.centrocirurgico.repository.AuditoriaAlteracaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class AuditoriaAlteracaoService {
    private final AuditoriaAlteracaoRepository repository;
    public AuditoriaAlteracaoService(AuditoriaAlteracaoRepository repository) { this.repository = repository; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarDiferencas(String entidade, Object id, String operacao,
                                    Map<String, String> antes, Map<String, String> depois) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth == null || !auth.isAuthenticated() ? "sistema" : auth.getName();
        for (String campo : depois.keySet()) {
            String antigo = antes.get(campo);
            String novo = depois.get(campo);
            if (!Objects.equals(antigo, novo)) {
                repository.save(new AuditoriaAlteracao(usuario, entidade,
                        id == null ? null : id.toString(), campo, antigo, novo, operacao));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaAlteracao> pesquisar(int pagina, String usuario, String entidade, String operacao,
                                               LocalDate dataInicio, LocalDate dataFim) {
        OffsetDateTime inicio = dataInicio == null ? null : dataInicio.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime fim = dataFim == null ? null : dataFim.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        return repository.pesquisar(normalizar(usuario), normalizar(entidade), normalizar(operacao), inicio, fim,
                PageRequest.of(Math.max(0, pagina), 30));
    }

    @Transactional(readOnly = true)
    public List<AuditoriaAlteracao> exportar(String usuario, String entidade, String operacao,
                                              LocalDate dataInicio, LocalDate dataFim) {
        OffsetDateTime inicio = dataInicio == null ? null : dataInicio.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        OffsetDateTime fim = dataFim == null ? null : dataFim.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        return repository.exportar(normalizar(usuario), normalizar(entidade), normalizar(operacao), inicio, fim);
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }

}
