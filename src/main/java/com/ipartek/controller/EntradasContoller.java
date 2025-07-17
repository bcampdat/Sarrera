package com.ipartek.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ipartek.modelo.Concierto;
import com.ipartek.modelo.Entrada;
import com.ipartek.modelo.DTO.EntradaDTO;
import com.ipartek.modelo.DTO.EntradaQR;
import com.ipartek.repository.ConciertoRepository;
import com.ipartek.service.EntradaService;
import com.ipartek.service.QrCodeService;

@Controller
@RequestMapping("/entradas")
public class EntradasContoller {	
	
	@Autowired
    private EntradaService entradaService;
	
	@Autowired
    private ConciertoRepository conciertoRepo;
	
	@Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/comprar/{conciertoId}")
    public String mostrarFormularioCompra(@PathVariable int conciertoId, Model model) {
        Optional<Concierto> conciertoOpt = conciertoRepo.findById(conciertoId);
        if (conciertoOpt.isEmpty()) {
            model.addAttribute("error", "Concierto no encontrado");
            return "error";
        }
        Concierto concierto = conciertoOpt.get();
        int entradasDisponibles = entradaService.aforoDisponible(concierto);
        model.addAttribute("concierto", concierto);
        model.addAttribute("entradasDisponibles", entradasDisponibles);
        return "compra";
    }

    @PostMapping("/comprar/{conciertoId}")
    public String procesarCompra(@PathVariable int conciertoId,
                                 @RequestParam int cantidad,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 Model model) throws Exception {
        
        Optional<Concierto> conciertoOpt = conciertoRepo.findById(conciertoId);
        if (conciertoOpt.isEmpty()) {
            model.addAttribute("error", "Concierto no encontrado");
            return "error";
        }
        Concierto concierto = conciertoOpt.get();

        if (principal == null) {
            model.addAttribute("error", "Debes estar autenticado para comprar");
            return "error";
        }

        String username = principal.getName();

        try {
            List<Entrada> entradasCompradas = entradaService.comprarEntradas(concierto, username, cantidad);

            List<EntradaQR> entradasQR = new ArrayList<>();
            String baseUrl = "http://SARRERA-NIRE/entradas/validar-json/";

            for (Entrada e : entradasCompradas) {
                String qrContent = baseUrl + e.getCodigo();
                String qrBase64 = qrCodeService.generarCodigoQR(qrContent, 200, 200);
                String barcodeBase64 = qrCodeService.generarCodigoBarra(e.getCodigo(), 150, 100);
                entradasQR.add(new EntradaQR(e, qrBase64, barcodeBase64));
            }
            
            int totalEntradasUsuario = entradaService.contarEntradasUsuario(concierto.getId(), username);
            BigDecimal totalPagar = concierto.getPrecio().multiply(BigDecimal.valueOf(entradasQR.size()));
            
            redirectAttributes.addFlashAttribute("entradasQR", entradasQR);
            redirectAttributes.addFlashAttribute("concierto", concierto);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("totalUsuario", totalEntradasUsuario);
            redirectAttributes.addFlashAttribute("totalPagar", totalPagar);

            return "redirect:/entradas/compras-exito";  

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return mostrarFormularioCompra(conciertoId, model);
        }
    }

    @GetMapping("/compras-exito")
    public String mostrarCompra(Model model) {
        // Si no hay entradas (por acceso directo o recarga), redirige
        if (!model.containsAttribute("entradasQR")) {
            return "redirect:/MenuEntradas";  // O al inicio, como prefieras
        }
        return "compras_exito";  // Vista segura, solo accesible tras compra válida
    }
    
    @GetMapping("/validar-json/{codigo}")
    @ResponseBody
    public EntradaDTO validarEntradaJson(@PathVariable String codigo) {
        Entrada entrada = entradaService.buscarPorCodigo(codigo);
        if (entrada == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no válida");
        }

        Concierto c = entrada.getConcierto();

        return new EntradaDTO(
            entrada.getCodigo(),
            c.getGrupo(),
            c.getFecha().toString(),
            c.getUbicacion().getNombreSala(),
            c.getUbicacion().getDireccion(),
            c.getPrecio()
        );
    }
    
    
}

