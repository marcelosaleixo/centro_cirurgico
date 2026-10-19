package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name="cirurgia_mutirao", indexes={@Index(name="ix_cirurgia_mutirao_dia_hora", columnList="dia_id,hora_inicio")})
public class CirurgiaMutirao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="dia_id", nullable=false) private DiaMutirao dia;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="paciente_id", nullable=false) private Paciente paciente;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="procedimento_id", nullable=false) private Procedimento procedimento;
 @Column(name="medico_responsavel", nullable=false, length=160) private String medicoResponsavel;
 @Column(name="hora_inicio", nullable=false) private LocalTime horaInicio;
 @Column(name="hora_fim", nullable=false) private LocalTime horaFim;
 @Column(nullable=false,length=20) private String status="PROGRAMADA";
 @Column(columnDefinition="text") private String observacoes;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public DiaMutirao getDia(){return dia;} public void setDia(DiaMutirao v){dia=v;}
 public Paciente getPaciente(){return paciente;} public void setPaciente(Paciente v){paciente=v;}
 public Procedimento getProcedimento(){return procedimento;} public void setProcedimento(Procedimento v){procedimento=v;}
 public String getMedicoResponsavel(){return medicoResponsavel;} public void setMedicoResponsavel(String v){medicoResponsavel=v;}
 public LocalTime getHoraInicio(){return horaInicio;} public void setHoraInicio(LocalTime v){horaInicio=v;}
 public LocalTime getHoraFim(){return horaFim;} public void setHoraFim(LocalTime v){horaFim=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getObservacoes(){return observacoes;} public void setObservacoes(String v){observacoes=v;}
}
