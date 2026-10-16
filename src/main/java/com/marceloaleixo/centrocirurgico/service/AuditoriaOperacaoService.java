package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaOperacao;
import com.marceloaleixo.centrocirurgico.repository.AuditoriaOperacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaOperacaoService {
    private final AuditoriaOperacaoRepository repository;
    public AuditoriaOperacaoService(AuditoriaOperacaoRepository repository) { this.repository = repository; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String usuario, String metodo, String recurso, int codigoHttp) {
        repository.save(new AuditoriaOperacao(usuario, metodo, recurso, codigoHttp));
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaOperacao> listar(int pagina) {
        return repository.findAll(PageRequest.of(Math.max(0, pagina), 30,
                Sort.by(Sort.Direction.DESC, "ocorridoEm")));
    }
}
