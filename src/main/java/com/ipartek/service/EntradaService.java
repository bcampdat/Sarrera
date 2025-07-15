package com.ipartek.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipartek.modelo.Concierto;
import com.ipartek.modelo.Entrada;
import com.ipartek.repository.EntradaRepository;

@Service
public class EntradaService {
	
	@Autowired
    private EntradaRepository entradaRepo;
	
	@Autowired
    private ConciertoService conciertoService;

    public int contarEntradasVendidas(int conciertoId) {
        return entradaRepo.countByConciertoId(conciertoId);
    }

    public int contarEntradasUsuario(int conciertoId, String username) {
        // Buscamos entradas cuyo código empieza con username (prefijo)
        return entradaRepo.countByConciertoIdAndCodigoStartingWith(conciertoId, username + "-");
    }

    public int aforoDisponible(Concierto concierto) {
        int aforoReal = conciertoService.comprobarCapacidad(concierto);
        long vendidas = contarEntradasVendidas(concierto.getId());
        return (int) (aforoReal - vendidas);
    }

    public List<Entrada> comprarEntradas(Concierto concierto, String username, int cantidad) throws Exception {
        int disponibles = aforoDisponible(concierto);
        if (cantidad > 6) {
            throw new Exception("No puedes comprar más de 6 entradas por usuario");
        }
        if (cantidad > disponibles) {
            throw new Exception("No hay suficientes entradas disponibles");
        }
        long compradasUsuario = contarEntradasUsuario(concierto.getId(), username);
        if (compradasUsuario + cantidad > 6) {
            throw new Exception("Has alcanzado el límite de 6 entradas para este concierto");
        }

        List<Entrada> nuevasEntradas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Entrada entrada = new Entrada();
            entrada.setConcierto(concierto);
            String codigoUnico = generarCodigoUnico(username);
            entrada.setCodigo(codigoUnico);
            entradaRepo.save(entrada);
            nuevasEntradas.add(entrada);
        }
        return nuevasEntradas;
    }

    private String generarCodigoUnico(String username) throws Exception {
        String input = username + "-" + UUID.randomUUID();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return username + "-" + bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public Entrada buscarPorCodigo(String codigo) {
        return entradaRepo.findByCodigo(codigo).orElse(null);
    }

}

