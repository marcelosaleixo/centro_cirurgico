package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaAlteracao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;

public interface AuditoriaAlteracaoRepository extends JpaRepository<AuditoriaAlteracao, Long> {
    @Query("select a from AuditoriaAlteracao a where (:usuario is null or lower(a.usuario) like lower(concat('%', :usuario, '%'))) and (:entidade is null or lower(a.entidade) like lower(concat('%', :entidade, '%'))) and (:operacao is null or upper(a.operacao) = upper(:operacao)) and (:inicio is null or a.ocorridoEm >= :inicio) and (:fim is null or a.ocorridoEm < :fim) order by a.ocorridoEm desc")
    Page<AuditoriaAlteracao> pesquisar(@Param("usuario") String usuario, @Param("entidade") String entidade,
            @Param("operacao") String operacao, @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim, Pageable pageable);

    @Query("select a from AuditoriaAlteracao a where (:usuario is null or lower(a.usuario) like lower(concat('%', :usuario, '%'))) and (:entidade is null or lower(a.entidade) like lower(concat('%', :entidade, '%'))) and (:operacao is null or upper(a.operacao) = upper(:operacao)) and (:inicio is null or a.ocorridoEm >= :inicio) and (:fim is null or a.ocorridoEm < :fim) order by a.ocorridoEm desc")
    List<AuditoriaAlteracao> exportar(@Param("usuario") String usuario, @Param("entidade") String entidade,
            @Param("operacao") String operacao, @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim);
}
