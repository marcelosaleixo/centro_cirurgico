package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import com.marceloaleixo.centrocirurgico.enums.StatusCirurgia;

@Entity
@Table(name = "cirurgia", indexes = @Index(name = "ix_cirurgia_data_sala", columnList = "data_cirurgia,sala_cirurgica_id"))
public class Cirurgia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedimento_id", nullable = false)
    private Procedimento procedimento;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_responsavel_id", nullable = false)
    private Colaborador medicoResponsavel;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anestesista_id", nullable = false)
    private Colaborador anestesista;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enfermeiro_id", nullable = false)
    private Colaborador enfermeiro;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_cirurgica_id", nullable = false)
    private SalaCirurgica salaCirurgica;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mutirao_id")
    private Mutirao mutirao;
    @Column(name = "data_cirurgia", nullable = false)
    private LocalDate dataCirurgia;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCirurgia status = StatusCirurgia.AGENDADA;
    @Column(columnDefinition = "text")
    private String observacao;
    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public Cirurgia() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Paciente getPaciente() { return this.paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Procedimento getProcedimento() { return this.procedimento; }
    public void setProcedimento(Procedimento procedimento) { this.procedimento = procedimento; }
    public Colaborador getMedicoResponsavel() { return this.medicoResponsavel; }
    public void setMedicoResponsavel(Colaborador medicoResponsavel) { this.medicoResponsavel = medicoResponsavel; }
    public Colaborador getAnestesista() { return this.anestesista; }
    public void setAnestesista(Colaborador anestesista) { this.anestesista = anestesista; }
    public Colaborador getEnfermeiro() { return this.enfermeiro; }
    public void setEnfermeiro(Colaborador enfermeiro) { this.enfermeiro = enfermeiro; }
    public SalaCirurgica getSalaCirurgica() { return this.salaCirurgica; }
    public void setSalaCirurgica(SalaCirurgica salaCirurgica) { this.salaCirurgica = salaCirurgica; }
    public Mutirao getMutirao() { return this.mutirao; }
    public void setMutirao(Mutirao mutirao) { this.mutirao = mutirao; }
    public LocalDate getDataCirurgia() { return this.dataCirurgia; }
    public void setDataCirurgia(LocalDate dataCirurgia) { this.dataCirurgia = dataCirurgia; }
    public LocalTime getHoraInicio() { return this.horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return this.horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public StatusCirurgia getStatus() { return this.status; }
    public void setStatus(StatusCirurgia status) { this.status = status; }
    public String getObservacao() { return this.observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public OffsetDateTime getCriadoEm() { return this.criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
