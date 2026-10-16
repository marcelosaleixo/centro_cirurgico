package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Equipe;
import com.marceloaleixo.centrocirurgico.repository.EquipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EquipeService {
    private final EquipeRepository repository;
    public EquipeService(EquipeRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Equipe> listarTodas() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Equipe buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada: " + id));
    }

    public Equipe salvar(Equipe equipe) {
        if (equipe == null || equipe.getNome() == null || equipe.getNome().isBlank())
            throw new IllegalArgumentException("O nome da equipe é obrigatório.");
        repository.findByNomeIgnoreCase(equipe.getNome().trim()).ifPresent(existente -> {
            if (!existente.getId().equals(equipe.getId()))
                throw new IllegalArgumentException("Já existe uma equipe com esse nome.");
        });
        equipe.setNome(equipe.getNome().trim());
        return repository.save(equipe);
    }

    public void excluir(Long id) {
        Equipe equipe = buscarPorId(id);
        if (!equipe.getColaboradores().isEmpty())
            throw new IllegalStateException("Não é possível excluir uma equipe que possui colaboradores.");
        repository.delete(equipe);
    }
}
