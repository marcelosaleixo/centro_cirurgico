package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaSeguranca;
import com.marceloaleixo.centrocirurgico.repository.AuditoriaSegurancaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaSegurancaService {
    private final AuditoriaSegurancaRepository repository;
    public AuditoriaSegurancaService(AuditoriaSegurancaRepository repository) { this.repository = repository; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String usuario, String acao, String detalhes, boolean sucesso) {
        String safeUser = usuario == null || usuario.isBlank() ? "desconhecido" : usuario.trim();
        String safeDetails = detalhes == null ? "" : detalhes.trim();
        if (safeUser.length() > 100) safeUser = safeUser.substring(0, 100);
        if (safeDetails.length() > 1000) safeDetails = safeDetails.substring(0, 1000);
        repository.save(new AuditoriaSeguranca(safeUser, acao, safeDetails, sucesso));
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaSeguranca> listar(int pagina) {
        return repository.findAll(PageRequest.of(Math.max(0, pagina), 30, Sort.by(Sort.Direction.DESC, "ocorridoEm")));
    }
}
