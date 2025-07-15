package com.ipartek.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipartek.modelo.Concierto;

@Repository
public interface ConciertoRepository extends JpaRepository<Concierto, Integer> {

    /**
     * 	PARA LA LISTA 
     * Se utiliza para mostrar los conciertos futuros a partir de fecha hoy.
     * 
     * query:
     * SELECT c FROM Concierto c WHERE c.fecha >= :fecha ORDER BY c.fecha ASC
     */
    List<Concierto> findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate fecha);
    

    /**
     * PARA LA BUSQUEDA
     * 
     * 
     * Busca conciertos cuyo nombre de grupo contenga el texto indicado (ignorando mayúsculas),
     * y cuya fecha sea anterior o igual a la proporcionada.
     *  
     * query:
     * SELECT c FROM Concierto c 
     * WHERE LOWER(c.grupo) LIKE LOWER(CONCAT('%', :grupo, '%')) 
     *   AND c.fecha <= :fecha 
     * ORDER BY c.fecha ASC
     */
    List<Concierto> findByGrupoContainingIgnoreCaseAndFechaLessThanEqualOrderByFechaAsc(String grupo, LocalDate fecha);

    /**
     * Busca conciertos cuyo nombre de grupo contenga el texto indicado (ignorando mayúsculas).
     * Se ordenan por fecha ascendente.
     * 
     * query:
     * SELECT c FROM Concierto c 
     * WHERE LOWER(c.grupo) LIKE LOWER(CONCAT('%', :grupo, '%')) 
     * ORDER BY c.fecha ASC
     */
    List<Concierto> findByGrupoContainingIgnoreCaseOrderByFechaAsc(String grupo);

    /**
     * Devuelve los conciertos cuya fecha sea anterior o igual a la proporcionada.
     * Se ordenan por fecha ascendente.
     * 
     * query:
     * SELECT c FROM Concierto c WHERE c.fecha <= :fecha ORDER BY c.fecha ASC
     */
    List<Concierto> findByFechaLessThanEqualOrderByFechaAsc(LocalDate fecha);

    /**
     * Devuelve todos los conciertos, sin filtros, ordenados por fecha ascendente.
     * 
     * JPQL equivalente:
     * SELECT c FROM Concierto c ORDER BY c.fecha ASC
     */
    List<Concierto> findAllByOrderByFechaAsc();
}
