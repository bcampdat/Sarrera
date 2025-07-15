package com.ipartek.modelo.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntradaDTO {
	private String codigo;
	private String grupo;
	private String fecha;
	private String nombreSala;
	private String direccion;
	private BigDecimal precio;
}
