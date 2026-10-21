package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.SolicitacaoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
public interface SolicitacaoCompraRepository extends JpaRepository<SolicitacaoCompra, Long> {
    List<SolicitacaoCompra> findAllByOrderByCriadoEmDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SolicitacaoCompra s join fetch s.item where s.id = :id")
    java.util.Optional<SolicitacaoCompra> bloquearPorId(@Param("id") Long id);
}
