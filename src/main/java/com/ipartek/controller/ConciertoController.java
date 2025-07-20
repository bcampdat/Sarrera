package com.ipartek.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ipartek.modelo.Concierto;
import com.ipartek.repository.ConciertoRepository;
import com.ipartek.repository.UbicacionRepository;
import com.ipartek.service.ConciertoService;

@Controller
public class ConciertoController {

	@Autowired
	private ConciertoRepository conciertoRepo;

	@Autowired
	private UbicacionRepository ubicacionRepo;
	
	@Autowired
    private ConciertoService conciertoService;

	@Value("${rutaFotos}")
	private String rutaFotos;


	@GetMapping("/nuevo")
	public String nuevoConcierto(Model model) {
		model.addAttribute("concierto", new Concierto());
		model.addAttribute("ubicaciones", ubicacionRepo.findAll());
		return "nuevoConcierto";
	}

	@PostMapping("/guardar")
	public String guardarConcierto(Model model, @ModelAttribute Concierto concierto, @RequestParam("fotoFile") MultipartFile file) {

		try {
			
			 int aforoOriginal = concierto.getAforo();
		        conciertoService.ajustarAforo(concierto); 

		        if (concierto.getAforo() < aforoOriginal) {
		            model.addAttribute("mensaje", "⚠ Supera la capacidad de la sala");
		            model.addAttribute("concierto", concierto);
		            model.addAttribute("ubicaciones", ubicacionRepo.findAll());
		            return "nuevoConcierto";  
		        }
			 
			Optional<Concierto> existente = concierto.getId() != 0 ? conciertoRepo.findById(concierto.getId())
					: Optional.empty();

			String fotoAntigua = existente.isPresent() ? existente.get().getFoto() : null;

			if (!file.isEmpty()) {
				if (fotoAntigua != null && !fotoAntigua.equals("entradas.png")) {
					Path rutaAnterior = Paths.get(rutaFotos + fotoAntigua);
					Files.deleteIfExists(rutaAnterior);
				}

				// Guarda la nueva
				LocalDateTime fecha = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
				String semilla = fecha.format(formatter);
				String nombreArchivo = semilla + file.getOriginalFilename();

				Path ruta = Paths.get(rutaFotos + nombreArchivo);
				Files.write(ruta, file.getBytes());

				concierto.setFoto(nombreArchivo);
			} else {
				if (fotoAntigua != null) {
					concierto.setFoto(fotoAntigua);
				} else {
					concierto.setFoto("entradas.png");
				}
			}

			conciertoRepo.save(concierto);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/MenuConciertos";
	}

	@GetMapping("/modificar")
	public String modificarConcierto(@RequestParam int id, Model model) {

		Optional<Concierto> con = conciertoRepo.findById(id);

		if (con.isPresent()) {
			model.addAttribute("concierto", con.get());
			model.addAttribute("ubicaciones", ubicacionRepo.findAll());
			return "nuevoConcierto";
		}
		return "redirect:/MenuConciertos";
	}

	@GetMapping("/detalle")
	public String detalleConcierto(@RequestParam int id, Model model) {
		Optional<Concierto> optCon = conciertoRepo.findById(id);
		if (optCon.isPresent()) {
			model.addAttribute("concierto", optCon.get());
			return "detalleConcierto";
		}
		return "redirect:/detalleConcierto";
	}

	@GetMapping("/eliminar")
	public String eliminarConcierto(@RequestParam int id) {
		Optional<Concierto> optCon = conciertoRepo.findById(id);
		if (optCon.isPresent()) {
			Concierto concierto = optCon.get();

			if (concierto.getFoto() != null && !concierto.getFoto().equals("entradas.png")) {
				try {
					Path ruta = Paths.get(rutaFotos, concierto.getFoto());
					Files.deleteIfExists(ruta);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			conciertoRepo.deleteById(id);
		}
		return "redirect:/conciertos";
	}

	@GetMapping("/concierto/imagen/{foto}")
	@ResponseBody
	public ResponseEntity<Resource> mostrarImagen(@PathVariable String foto) throws IOException {
		Path ruta = Paths.get(rutaFotos, foto);
		Resource recurso = new UrlResource(ruta.toUri());

		if (!recurso.exists() || !recurso.isReadable()) {
			Path rutaDefault = Paths.get(rutaFotos, "entradas.png");
			recurso = new UrlResource(rutaDefault.toUri());
			if (!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("No se pudo leer la imagen ni la imagen por defecto");
			}
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(recurso.getFile().toPath()))
				.body(recurso);
	}

	@GetMapping("/buscar")
	public String cargarListadoConciertos(@RequestParam(required = false) String grupo,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			Model model) {

		List<Concierto> listaConciertos;

		if (grupo != null && !grupo.isEmpty() && fecha != null) {
			listaConciertos = conciertoRepo.findByGrupoContainingIgnoreCaseAndFechaLessThanEqualOrderByFechaAsc(grupo,
					fecha);
		} else if (grupo != null && !grupo.isEmpty()) {
			listaConciertos = conciertoRepo.findByGrupoContainingIgnoreCaseOrderByFechaAsc(grupo);
		} else if (fecha != null) {
			listaConciertos = conciertoRepo.findByFechaLessThanEqualOrderByFechaAsc(fecha);
		} else {
			listaConciertos = conciertoRepo.findAllByOrderByFechaAsc();
		}

		model.addAttribute("listaConciertos", listaConciertos);
		model.addAttribute("grupo", grupo);
		model.addAttribute("fecha", fecha);

		return "listadoConciertos";
	}

}
