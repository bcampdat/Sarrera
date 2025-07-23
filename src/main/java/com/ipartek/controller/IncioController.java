package com.ipartek.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.zxing.WriterException;
import com.ipartek.modelo.Concierto;
import com.ipartek.modelo.Entrada;
import com.ipartek.modelo.Ubicacion;
import com.ipartek.modelo.DTO.ConciertoTikDTO;
import com.ipartek.modelo.DTO.EntradaQR;
import com.ipartek.repository.ConciertoRepository;
import com.ipartek.repository.EntradaRepository;
import com.ipartek.repository.UbicacionRepository;
import com.ipartek.service.ConciertoService;
import com.ipartek.service.EntradaService;
import com.ipartek.service.QrCodeService;

@Controller
public class IncioController {

	@Autowired
	private ConciertoRepository conciertoRepo;
	@Autowired
	private EntradaRepository entradaRepo;
	
	@Autowired
	private UbicacionRepository ubicacionRepo;

	@Autowired
	private ConciertoService conciertoServ;

	@Autowired
	private EntradaService entradaServ;
	@Autowired
	private QrCodeService QrCodeServ;

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

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/Admin/MenuConciertos")
	public String cargarConciertos(Model model) {
		List<Concierto> listaConciertos = conciertoRepo.findAll();
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

	@GetMapping("/MisConciertos")
	public String misConciertos(Model model, Principal principal) {
	    String username = principal.getName();

	    List<Entrada> entradasUsuario = entradaRepo.findByCodigoStartingWith(username + "-");
	    Map<Concierto, List<Entrada>> entradasPorConcierto = entradasUsuario.stream()
	            .collect(Collectors.groupingBy(Entrada::getConcierto));

	    List<ConciertoTikDTO> listaDTO = new ArrayList<>();
	    //String baseUrl = "http://SARRERA-NIRE/entradas/validar-json/";
	    String baseUrl = "http://http://localhost:8080//entradas/validar-json/";

	    for (Map.Entry<Concierto, List<Entrada>> entry : entradasPorConcierto.entrySet()) {
	        Concierto concierto = entry.getKey();
	        List<Entrada> entradas = entry.getValue();

	        int numEntradas = entradas.size();
	        BigDecimal totalPagado = concierto.getPrecio().multiply(BigDecimal.valueOf(numEntradas));

	        List<EntradaQR> entradasQR = new ArrayList<>();
	        for (Entrada e : entradas) {
	            try {
	                String qrContent = baseUrl + e.getCodigo();
	                String qrBase64 = QrCodeServ.generarCodigoQR(qrContent, 200, 200);
	                String barcodeBase64 = QrCodeServ.generarCodigoBarra(e.getCodigo(), 150, 100);
	                entradasQR.add(new EntradaQR(e, qrBase64, barcodeBase64));
	            } catch (WriterException | IOException ex) {
	                ex.printStackTrace();
	                entradasQR.add(new EntradaQR(e, "", ""));
	            }
	        }

	        ConciertoTikDTO dto = new ConciertoTikDTO();
	        dto.setConcierto(concierto);
	        dto.setEntradas(entradas);
	        dto.setNumeroEntradas(numEntradas);
	        dto.setTotalPagado(totalPagado);
	        dto.setEntradasQR(entradasQR);

	        listaDTO.add(dto);
	    }

	    model.addAttribute("conciertosEntradas", listaDTO);

	    return "misConciertos";
	}
}
