package com.ipartek.modelo.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.ipartek.modelo.Concierto;
import com.ipartek.modelo.Entrada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConciertoTikDTO {
    private Concierto concierto;
    private List<Entrada> entradas;
    private int numeroEntradas;
    private BigDecimal totalPagado;
    
    private List<EntradaQR> entradasQR;
}

