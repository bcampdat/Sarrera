package com.ipartek.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ipartek.modelo.Usuario;
import com.ipartek.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private final UsuarioRepository usuarioRepository;

	@Value("${security.pepper}")
	private String pepper;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Optional<Usuario> validarUsuario(String username, String passwordIntroducido) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUser(username);

		if (usuarioOpt.isEmpty()) {
			return Optional.empty();
		}

		Usuario usuario = usuarioOpt.get();

		// Construimos la cadena para hashear
		String raw = passwordIntroducido + usuario.getSalt() + pepper;
		String hashedInput = hashPassword(raw);

		// Comparamos con la contraseña almacenada
		if (hashedInput.equals(usuario.getPass())) {
			return Optional.of(usuario);
		}

		return Optional.empty();
	}

	private String hashPassword(String raw) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error al hashear la contraseña", e);
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	@Override
	public void insertarUsuario(Usuario usuario) {
		// Generar hash de la contraseña con salt y pepper
		String raw = usuario.getPass() + usuario.getSalt() + pepper;
		String hashed = hashPassword(raw);
		usuario.setPass(hashed);
		usuarioRepository.save(usuario);
	}

	@Override
	public void borrarUsuario(Integer id) {
		usuarioRepository.deleteById(id);
	}

	
	
	@Override
	public Optional<Usuario> buscarPorUser(String username) {
	    return usuarioRepository.findByUser(username);
	}


}
