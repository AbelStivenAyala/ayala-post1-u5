package com.universidad.reservaslabs.repository;

import com.universidad.reservaslabs.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByLaboratorioId(Long laboratorioId);

    // Reservas activas (no canceladas) del mismo laboratorio cuyo rango
    // [inicio, fin) se solapa con el rango dado. El filtro lo hace la base
    // de datos, no la memoria de Java (ver Punto de decisión 1).
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.laboratorio.id = :laboratorioId
          AND r.estado <> com.universidad.reservaslabs.model.EstadoReserva.CANCELADA
          AND r.inicio < :fin
          AND r.fin > :inicio
        """)
    List<Reserva> buscarSolapamientos(@Param("laboratorioId") Long laboratorioId,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fin") LocalDateTime fin);
}
