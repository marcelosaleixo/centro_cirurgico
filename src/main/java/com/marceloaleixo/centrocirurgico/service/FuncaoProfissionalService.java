package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.FuncaoProfissional;
import com.marceloaleixo.centrocirurgico.repository.FuncaoProfissionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FuncaoProfissionalService {
    private final FuncaoProfissionalRepository repository;
    public FuncaoProfissionalService(FuncaoProfissionalRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<FuncaoProfissional> listarTodos() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public FuncaoProfissional buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Função profissional não encontrada: " + id));
    }

    public FuncaoProfissional salvar(FuncaoProfissional funcao) {
        if (funcao == null || funcao.getNome() == null || funcao.getNome().isBlank())
            throw new IllegalArgumentException("O nome da função é obrigatório.");
        repository.findByNomeIgnoreCase(funcao.getNome().trim()).ifPresent(existente -> {
            if (!existente.getId().equals(funcao.getId()))
                throw new IllegalArgumentException("Já existe uma função com esse nome.");
        });
        funcao.setNome(funcao.getNome().trim());
        return repository.save(funcao);
    }

    public void excluir(Long id) {
        FuncaoProfissional funcao = buscarPorId(id);
        if (!funcao.getColaboradores().isEmpty())
            throw new IllegalStateException("Não é possível excluir uma função vinculada a colaboradores.");
        repository.delete(funcao);
    }
}
