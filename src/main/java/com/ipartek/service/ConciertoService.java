package com.ipartek.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ipartek.modelo.Concierto;


@Service
public class ConciertoService {

	private static final Map<String, Integer> AFOROS_REALES = Map.ofEntries(Map.entry("Sala BBK", 550),
			Map.entry("Kafe Antzokia", 870), Map.entry("Cotton Club", 110), Map.entry("Escenario Santander", 973),
			Map.entry("Black Bird Club", 200), Map.entry("Sala Acapulco", 500), Map.entry("La Salvaje", 130),
			Map.entry("Mardi Gras", 130), Map.entry("Sala Capitol", 1000), Map.entry("El Gran Café", 1000),
			Map.entry("Studio 54 León", 1000));

	/**
	 * Comprueba y ajusta el aforo del concierto según la capacidad real de la sala.
	 * 
	 * @param concierto Concierto a comprobar
	 * @return aforo ajustado
	 */
	public int comprobarCapacidad(Concierto concierto) {
		if (concierto == null || concierto.getUbicacion() == null) {
			return concierto != null ? concierto.getAforo() : 0;
		}

		String nombreSala = concierto.getUbicacion().getNombreSala();
		int capacidadReal = AFOROS_REALES.getOrDefault(nombreSala, Integer.MAX_VALUE);
		int aforoConcierto = concierto.getAforo();

		return aforoConcierto > capacidadReal ? capacidadReal : aforoConcierto;
	}

	public void ajustarAforo(Concierto concierto) {
		int aforoAjustado = comprobarCapacidad(concierto);
		concierto.setAforo(aforoAjustado);
	}

}
