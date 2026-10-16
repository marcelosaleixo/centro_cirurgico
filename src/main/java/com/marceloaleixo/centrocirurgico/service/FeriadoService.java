package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.AbrangenciaFeriado;
import com.marceloaleixo.centrocirurgico.entity.Feriado;
import com.marceloaleixo.centrocirurgico.repository.FeriadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import com.marceloaleixo.centrocirurgico.enums.RegraFeriado;

@Service
@Transactional
public class FeriadoService {
    private final FeriadoRepository repository;
    public FeriadoService(FeriadoRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Feriado> listarTodos() { return repository.findAllByOrderByDataAscNomeAsc(); }

    @Transactional(readOnly = true)
    public Feriado buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Feriado não encontrado."));
    }

    public Feriado salvar(Feriado f) {
        if (f == null || f.getNome() == null || f.getNome().isBlank())
            throw new IllegalArgumentException("Informe o nome do feriado.");
        if (f.getData() == null) throw new IllegalArgumentException("Informe a data do feriado.");
        if (f.getAbrangencia() == null) throw new IllegalArgumentException("Selecione a abrangência.");
        f.setNome(f.getNome().trim());
        if (f.getRegraOperacional() == null) f.setRegraOperacional(RegraFeriado.FUNCIONAMENTO_NORMAL);
        if (f.getPercentualAdicional() != null && f.getPercentualAdicional().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("O percentual adicional não pode ser negativo.");
        if (f.getPercentualAdicional() != null && f.getPercentualAdicional().compareTo(new BigDecimal("999.99")) > 0)
            throw new IllegalArgumentException("O percentual adicional máximo permitido é 999,99%.");
        if (f.getRegraOperacional() != RegraFeriado.PAGAMENTO_DIFERENCIADO) f.setPercentualAdicional(null);
        if (f.getAbrangencia() == AbrangenciaFeriado.ESTADUAL && (f.getUf() == null || f.getUf().isBlank()))
            throw new IllegalArgumentException("Informe a UF para feriado estadual.");
        if (f.getAbrangencia() == AbrangenciaFeriado.MUNICIPAL && (f.getMunicipio() == null || f.getMunicipio().isBlank()))
            throw new IllegalArgumentException("Informe o município para feriado municipal.");
        if (f.getUf() != null) f.setUf(f.getUf().trim().toUpperCase());
        if (f.getMunicipio() != null) f.setMunicipio(f.getMunicipio().trim());
        return repository.save(f);
    }

    public void inativar(Long id) {
        Feriado f = buscar(id);
        f.setAtivo(false);
        repository.save(f);
    }
}
