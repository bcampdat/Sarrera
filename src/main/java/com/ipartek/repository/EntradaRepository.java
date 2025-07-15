package com.ipartek.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipartek.modelo.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Integer> {
    // Número de entradas vendidas para un concierto (para control aforo)
    int countByConciertoId(int conciertoId);

    // Número de entradas compradas por un usuario en un concierto
    int countByConciertoIdAndCodigoStartingWith(int conciertoId, String codigoUsuarioPrefix);

    // Entradas de un usuario en un concierto (para mostrar al final)
    List<Entrada> findByConciertoIdAndCodigoStartingWith(int conciertoId, String codigoUsuarioPrefix);
    
    Optional<Entrada> findByCodigo(String codigo);

}