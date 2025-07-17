package com.ipartek.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ipartek.modelo.Concierto;
import com.ipartek.modelo.Ubicacion;
import com.ipartek.repository.ConciertoRepository;
import com.ipartek.repository.UbicacionRepository;
import com.ipartek.service.ConciertoService;
import com.ipartek.service.EntradaService;

@Controller
public class IncioController {

	@Autowired
	private ConciertoRepository conciertoRepo;

	@Autowired
	private ConciertoService conciertoServ;
	
	@Autowired
	private EntradaService entradaServ;

	@Autowired
	private UbicacionRepository ubicacionRepo;

	@GetMapping("/")
	public String cargarInicio(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "8") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Concierto> paginaConciertos = conciertoRepo.findAll(pageable);
		
		model.addAttribute("paginaConciertos", paginaConciertos);
		return "index";
	}

	@GetMapping("/conciertos")
	public String listarConciertos(Model model) {

		model.addAttribute("listaConciertos", conciertoRepo.findAll());

		return "listadoConciertos";
	}

	@GetMapping("/MenuConciertos")
	public String cargarConciertos(Model model) {
		List<Concierto> listaConciertos = conciertoRepo.findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate.now());
		List<Ubicacion> listaUbicaciones = ubicacionRepo.findAll();

		model.addAttribute("listaConciertos", listaConciertos);
		model.addAttribute("listaUbicaciones", listaUbicaciones);
		model.addAttribute("concierto", new Concierto());

		return "conciertos";
	}

	@GetMapping("/MenuSalas")
	public String cargarSalas(Model model) {
		List<Ubicacion> listaUbicaciones = ubicacionRepo.findAll();
		model.addAttribute("listaUbicaciones", listaUbicaciones);
		model.addAttribute("ubicacion", new Ubicacion());
		return "salas";
	}

	@GetMapping("/MenuEntradas")
	public String listaConciertos(Model model) {
		List<Concierto> conciertos = conciertoRepo.findAll();
		conciertos.forEach(conciertoServ::ajustarAforo);
		
		Map<Integer, Integer> entradasDisponiblesMap = new HashMap<>();
	    for (Concierto c : conciertos) {
	        int disponibles = entradaServ.aforoDisponible(c);
	        entradasDisponiblesMap.put(c.getId(), disponibles);
	    }
		model.addAttribute("conciertos", conciertos);
		model.addAttribute("entradasDisponiblesMap", entradasDisponiblesMap);
		return "entradas";
	}

}
