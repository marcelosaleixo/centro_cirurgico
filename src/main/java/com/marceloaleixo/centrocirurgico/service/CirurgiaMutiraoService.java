package com.marceloaleixo.centrocirurgico.service;
import com.marceloaleixo.centrocirurgico.entity.*;
import com.marceloaleixo.centrocirurgico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service
public class CirurgiaMutiraoService {
 private final CirurgiaMutiraoRepository cirurgias; private final DiaMutiraoRepository dias; private final PacienteMutiraoLookupRepository pacientes; private final ProcedimentoMutiraoLookupRepository procedimentos;
 public CirurgiaMutiraoService(CirurgiaMutiraoRepository c, DiaMutiraoRepository d, PacienteMutiraoLookupRepository p, ProcedimentoMutiraoLookupRepository pr){cirurgias=c;dias=d;pacientes=p;procedimentos=pr;}
 public List<CirurgiaMutirao> listar(Long diaId){return cirurgias.listarPorDia(diaId);} public List<Paciente> pacientes(){return pacientes.findTop300ByOrderByNomeAsc();} public List<Procedimento> procedimentos(){return procedimentos.findByAtivoTrueOrderByNomeAsc();}
 @Transactional public void salvar(Long id, Long diaId, Long pacienteId, Long procedimentoId, String medico, java.time.LocalTime inicio, java.time.LocalTime fim, String status, String observacoes){
  DiaMutirao dia=dias.findById(diaId).orElseThrow(()->new IllegalArgumentException("Dia do mutirão não encontrado."));
  if(dia.getData()==null || dia.getData().isBefore(dia.getEvento().getDataInicio()) || dia.getData().isAfter(dia.getEvento().getDataFim())) throw new IllegalArgumentException("A data do dia não pertence ao período do evento.");
  if(inicio==null||fim==null||!fim.isAfter(inicio)) throw new IllegalArgumentException("O horário final deve ser posterior ao horário inicial.");
  if(cirurgias.existeConflito(diaId,id==null?0L:id,inicio,fim)) throw new IllegalArgumentException("Já existe uma cirurgia com horário sobreposto neste dia.");
  CirurgiaMutirao c=id==null?new CirurgiaMutirao():cirurgias.findById(id).orElseThrow(()->new IllegalArgumentException("Cirurgia não encontrada."));
  c.setDia(dia); c.setPaciente(pacientes.findById(pacienteId).orElseThrow(()->new IllegalArgumentException("Paciente não encontrado."))); c.setProcedimento(procedimentos.findById(procedimentoId).orElseThrow(()->new IllegalArgumentException("Procedimento não encontrado.")));
  if(medico==null||medico.isBlank()) throw new IllegalArgumentException("Informe o médico responsável."); c.setMedicoResponsavel(medico.trim()); c.setHoraInicio(inicio); c.setHoraFim(fim); c.setStatus(status==null||status.isBlank()?"PROGRAMADA":status); c.setObservacoes(observacoes); cirurgias.save(c);
 }
 public CirurgiaMutirao buscar(Long id){return cirurgias.findById(id).orElseThrow(()->new IllegalArgumentException("Cirurgia não encontrada."));}
 @Transactional public void excluir(Long id){cirurgias.deleteById(id);}
}
