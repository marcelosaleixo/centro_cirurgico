package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import com.marceloaleixo.centrocirurgico.enums.RegimeTrabalho;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ColaboradorService {
    private final ColaboradorRepository colaboradorRepository;
    private final FuncaoProfissionalRepository funcaoRepository;
    private final EquipeRepository equipeRepository;

    public ColaboradorService(ColaboradorRepository colaboradorRepository,
                              FuncaoProfissionalRepository funcaoRepository,
                              EquipeRepository equipeRepository) {
        this.colaboradorRepository = colaboradorRepository;
        this.funcaoRepository = funcaoRepository;
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<Colaborador> listarTodos() { return colaboradorRepository.findAllComRelacionamentos(); }

    @Transactional(readOnly = true)
    public List<Colaborador> listarAtivos() { return colaboradorRepository.findByAtivoTrueOrderByNomeAsc(); }

    @Transactional(readOnly = true)
    public Colaborador buscarPorId(Long id) {
        return colaboradorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado: " + id));
    }

    public Colaborador salvar(Colaborador colaborador) {
        if (colaborador == null || colaborador.getNome() == null || colaborador.getNome().isBlank())
            throw new IllegalArgumentException("O nome do colaborador é obrigatório.");
        if (colaborador.getFuncao() == null || colaborador.getFuncao().getId() == null)
            throw new IllegalArgumentException("Selecione a função profissional do colaborador.");
        FuncaoProfissional funcao = funcaoRepository.findById(colaborador.getFuncao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Função profissional não encontrada."));
        colaborador.setFuncao(funcao);

        if (colaborador.getEquipe() != null && colaborador.getEquipe().getId() != null) {
            Equipe equipe = equipeRepository.findById(colaborador.getEquipe().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada."));
            colaborador.setEquipe(equipe);
        } else {
            colaborador.setEquipe(null);
        }

        if (colaborador.getRegimeTrabalho() == null) {
            colaborador.setRegimeTrabalho(RegimeTrabalho.DOZE_TRINTA_SEIS);
        }
        if (colaborador.getDataReferenciaPlantao() == null)
            throw new IllegalArgumentException("Informe a data de referência da escala.");

        if (colaborador.getRegimeTrabalho() == RegimeTrabalho.DOZE_TRINTA_SEIS) {
            // Regra de negócio no servidor: plantão corresponde à paridade do dia do mês.
            int diaReferencia = colaborador.getDataReferenciaPlantao().getDayOfMonth();
            colaborador.setPlantaoReferencia(
                    diaReferencia % 2 == 0
                            ? com.marceloaleixo.centrocirurgico.enums.TipoPlantao.PAR
                            : com.marceloaleixo.centrocirurgico.enums.TipoPlantao.IMPAR
            );
        } else if (colaborador.getPlantaoReferencia() == null) {
            // Campo mantido por compatibilidade com registros antigos; não define os dias do regime 5x2.
            colaborador.setPlantaoReferencia(com.marceloaleixo.centrocirurgico.enums.TipoPlantao.PAR);
        }

        colaborador.setNome(colaborador.getNome().trim());
        if (colaborador.getCpf() != null && !colaborador.getCpf().isBlank()) {
            String cpf = colaborador.getCpf().replaceAll("\\D", "");
            if (cpf.length() != 11) throw new IllegalArgumentException("O CPF deve conter 11 dígitos.");
            colaborador.setCpf(cpf);
            colaboradorRepository.findByCpf(cpf).ifPresent(existente -> {
                if (!Objects.equals(existente.getId(), colaborador.getId()))
                    throw new IllegalArgumentException("Já existe um colaborador com esse CPF.");
            });
        } else colaborador.setCpf(null);

        if (colaborador.getEmail() != null && !colaborador.getEmail().isBlank()) {
            colaborador.setEmail(colaborador.getEmail().trim().toLowerCase());
            colaboradorRepository.findByEmailIgnoreCase(colaborador.getEmail()).ifPresent(existente -> {
                if (!Objects.equals(existente.getId(), colaborador.getId()))
                    throw new IllegalArgumentException("Já existe um colaborador com esse e-mail.");
            });
        } else colaborador.setEmail(null);
        return colaboradorRepository.save(colaborador);
    }

    public Colaborador desativar(Long id) {
        Colaborador colaborador = buscarPorId(id);
        colaborador.setAtivo(false);
        return colaboradorRepository.save(colaborador);
    }
}
