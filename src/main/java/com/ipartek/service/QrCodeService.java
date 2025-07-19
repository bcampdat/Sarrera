package com.ipartek.service;

import com.google.zxing.BarcodeFormat;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;

@Service
public class QrCodeService {

    public String generarCodigoQR(String texto, int ancho, int alto) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);  // márgenes
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        

        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(pngData);  
    }
    
    
    public String generarCodigoBarra(String texto, int ancho, int alto) throws WriterException, IOException {
        Code128Writer barcodeWriter = new Code128Writer();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);  // margen mínimo para barras
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = barcodeWriter.encode(texto, BarcodeFormat.CODE_128, ancho, alto, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(pngData);
    }
    
    public String generarUUID() {
        return UUID.randomUUID().toString();
    }
    
}
