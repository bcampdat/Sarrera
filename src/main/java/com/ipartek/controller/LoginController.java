package com.ipartek.controller;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ipartek.modelo.Usuario;
import com.ipartek.service.UsuarioService;

@Controller
public class LoginController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/login")
	public String cargarLogin(@RequestParam( defaultValue = "login") String form, Model model) {
		model.addAttribute("formulario", form);
		return "login";
	}

	@GetMapping("/registro")
	public String mostrarFormularioRegistro() {
		return "registro";
	}

	@PostMapping("/registro")
	public String procesarRegistro(@RequestParam String username, @RequestParam String password, Model model) {

		// Verificar que el usuario no exista ya
		if (usuarioService.validarUsuario(username, password).isPresent()) {
			model.addAttribute("error", "El usuario ya existe");
			return "registro";
		}

		Usuario usuario = new Usuario();
		usuario.setUser(username);
		usuario.setSalt(generarSalt());
		usuario.setPass(password);

		usuarioService.insertarUsuario(usuario);
			

		return "redirect:/login?registro=true";

	}

	private String generarSalt() {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}
}
