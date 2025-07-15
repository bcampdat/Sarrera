package com.ipartek.service;

import java.util.Optional;
import com.ipartek.modelo.Usuario;

public interface UsuarioService {
	Optional<Usuario> validarUsuario(String username, String passwordIntroducido);

	void insertarUsuario(Usuario usuario);

	void borrarUsuario(Integer id);
	
	Optional<Usuario> buscarPorUser(String username);

}
