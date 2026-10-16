package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.BancoHoras;
import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.repository.EscalaPlantaoRepository;
import com.marceloaleixo.centrocirurgico.enums.StatusBancoHoras;
import com.marceloaleixo.centrocirurgico.enums.StatusFerias;
import com.marceloaleixo.centrocirurgico.repository.BancoHorasRepository;
import com.marceloaleixo.centrocirurgico.repository.ColaboradorRepository;
import com.marceloaleixo.centrocirurgico.repository.FeriasRepository;
import com.marceloaleixo.centrocirurgico.web.BancoHorasForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BancoHorasService {
    private final BancoHorasRepository bancoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final FeriasRepository feriasRepository;
    private final EscalaPlantaoRepository escalaRepository;
    private static final List<StatusFerias> FERIAS_ATIVAS = List.of(StatusFerias.SOLICITADA, StatusFerias.APROVADA);

    public BancoHorasService(BancoHorasRepository bancoRepository, ColaboradorRepository colaboradorRepository, FeriasRepository feriasRepository, EscalaPlantaoRepository escalaRepository) {
        this.bancoRepository=bancoRepository; this.colaboradorRepository=colaboradorRepository; this.feriasRepository=feriasRepository; this.escalaRepository=escalaRepository;
    }
    @Transactional(readOnly=true) public List<BancoHoras> listar(){return bancoRepository.findAllByOrderByDataBancoDesc();}
    @Transactional(readOnly=true) public BancoHoras buscar(Long id){return bancoRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Lançamento de banco de horas não encontrado."));}

    public BancoHoras lancar(BancoHorasForm form) {
        if(form.getColaboradorId()==null) throw new IllegalArgumentException("Selecione o colaborador.");
        if(form.getDataBanco()==null) throw new IllegalArgumentException("Informe a data do banco de horas.");
        if(form.getQuantidadeHoras()==null || form.getQuantidadeHoras().compareTo(BigDecimal.ZERO)<=0 || form.getQuantidadeHoras().compareTo(new BigDecimal("24.00"))>0)
            throw new IllegalArgumentException("A quantidade de horas deve ser maior que zero e no máximo 24.");
        Colaborador c=colaboradorRepository.findById(form.getColaboradorId()).orElseThrow(()->new IllegalArgumentException("Colaborador não encontrado."));
        if(!c.isAtivo()) throw new IllegalStateException("Não é possível lançar banco de horas para colaborador inativo.");
        if(feriasRepository.existeSobreposicao(c.getId(), form.getDataBanco(), form.getDataBanco(), FERIAS_ATIVAS, null))
            throw new IllegalStateException("Não é possível lançar banco de horas em data coberta por férias solicitadas ou aprovadas.");
        boolean duplicado=form.getId()==null
                ? bancoRepository.existsByColaboradorIdAndDataBancoAndStatus(c.getId(), form.getDataBanco(), StatusBancoHoras.REGISTRADO)
                : bancoRepository.existsByColaboradorIdAndDataBancoAndStatusAndIdNot(c.getId(), form.getDataBanco(), StatusBancoHoras.REGISTRADO, form.getId());
        if(duplicado) throw new IllegalStateException("Já existe um lançamento ativo de banco de horas para esse colaborador nessa data.");
        if(form.getDataBanco().isBefore(c.getDataReferenciaPlantao())) throw new IllegalArgumentException("A data do banco de horas não pode anteceder a referência da escala.");
        long diasDesdeReferencia= ChronoUnit.DAYS.between(c.getDataReferenciaPlantao(), form.getDataBanco());
        if(diasDesdeReferencia % 2 != 0) throw new IllegalArgumentException("A data do banco de horas deve coincidir com um dia de plantão da escala 12x36.");
        BancoHoras banco;
        if (form.getId() != null) {
            banco = buscar(form.getId());
        } else {
            banco = bancoRepository.findByColaboradorIdAndDataBancoAndStatus(c.getId(), form.getDataBanco(), StatusBancoHoras.CANCELADO)
                    .orElseGet(BancoHoras::new);
        }
        LocalDate dataAnterior=banco.getDataBanco();
        banco.setColaborador(c); banco.setDataBanco(form.getDataBanco()); banco.setQuantidadeHoras(form.getQuantidadeHoras());
        banco.setMotivo(form.getMotivo()==null?null:form.getMotivo().trim()); banco.setStatus(StatusBancoHoras.REGISTRADO);
        BancoHoras salvo=bancoRepository.save(banco);
        if(dataAnterior!=null && !dataAnterior.equals(salvo.getDataBanco())) normalizarEscalaBanco(c.getId(), dataAnterior);
        EscalaPlantao escala=escalaRepository.findByColaboradorIdAndDataPlantao(c.getId(), salvo.getDataBanco()).orElse(null);
        if(escala!=null && escala.getStatus()!=StatusEscala.CANCELADO && escala.getStatus()!=StatusEscala.FERIAS){
            escala.setStatus(StatusEscala.BANCO_HORAS);
            escala.setObservacao("Dia de banco de horas lançado ("+salvo.getQuantidadeHoras()+" h). "+(salvo.getMotivo()==null?"":salvo.getMotivo()));
            escalaRepository.save(escala);
        }
        return salvo;
    }
    public void cancelar(Long id){
        BancoHoras b=buscar(id);
        if(b.getStatus()!=StatusBancoHoras.REGISTRADO) throw new IllegalStateException("Somente lançamentos ativos podem ser cancelados.");
        b.setStatus(StatusBancoHoras.CANCELADO);
        normalizarEscalaBanco(b.getColaborador().getId(), b.getDataBanco());
    }
    private void normalizarEscalaBanco(Long colaboradorId, LocalDate data){
        EscalaPlantao escala=escalaRepository.findByColaboradorIdAndDataPlantao(colaboradorId,data).orElse(null);
        if(escala!=null && escala.getStatus()==StatusEscala.BANCO_HORAS){
            escala.setStatus(StatusEscala.PROGRAMADO);
            escala.setObservacao("Banco de horas cancelado/alterado; escala 12x36 voltou a PROGRAMADO.");
            escalaRepository.save(escala);
        }
    }
}
