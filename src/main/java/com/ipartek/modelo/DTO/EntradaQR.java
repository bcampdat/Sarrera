package com.ipartek.modelo.DTO;

import com.ipartek.modelo.Entrada;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntradaQR {
    private Entrada entrada;
    private String qrBase64;
    private String barcodeBase64;
}

