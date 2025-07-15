package com.ipartek.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@Autowired
    private ValidarUsuarioProvider validarUsuarioProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.authenticationProvider(validarUsuarioProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/login","/registro", "/css/**", "/js/**", "/logo/**","/entradas/validar-json/**", "/concierto/imagen/**").permitAll() 
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .defaultSuccessUrl("/conciertos", true)
                .failureUrl("/login?error=true") 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true") 
                .permitAll()
            );

        return http.build();
    }
    
	/*
	 * INSERT INTO usuarios (user, pass, salt) VALUES ( 'hola',
	 * '9e478fd30b48d8254acabcd23f220640d1e74ba31664c8ae21857e0952148d41',
	 * '1a2b3c4d5e6f7890abcdef1234567890' );
	 */
    
 
	 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
}
