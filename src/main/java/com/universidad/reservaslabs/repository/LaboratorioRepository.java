package com.universidad.reservaslabs.repository;

import com.universidad.reservaslabs.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
