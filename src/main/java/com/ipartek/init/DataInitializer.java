package com.ipartek.init;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ipartek.modelo.Ubicacion;
import com.ipartek.repository.UbicacionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UbicacionRepository ubicacionRepo;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        insertarUbicacion();
        ejecutarScriptSQL("MySql/conciertos.sql");
        ejecutarScriptSQL("MySql/usuario.sql");
        ejecutarScriptSQL("MySql/admins.sql");
        
       
    }

    private void insertarUbicacion() {
        if (ubicacionRepo.count() == 0) {
            List<Ubicacion> ubicaciones = List.of(
                Ubicacion.builder().nombreSala("Sala BBK").direccion("Calle Gran Vía, 25, Bilbao, País Vasco").build(),
                Ubicacion.builder().nombreSala("Kafe Antzokia").direccion("Calle Iparraguirre, 55, Bilbao, País Vasco").build(),
                Ubicacion.builder().nombreSala("Cotton Club").direccion("Calle Iparraguirre, 55, Bilbao, País Vasco").build(),
                Ubicacion.builder().nombreSala("Escenario Santander").direccion("Calle Bonifaz, 6, Santander, Cantabria").build(),
                Ubicacion.builder().nombreSala("Black Bird Club").direccion("Calle Río de la Pila, 8, Santander, Cantabria").build(),
                Ubicacion.builder().nombreSala("Sala Acapulco").direccion("Calle Marqués de Santa Cruz, 14, Gijón, Asturias").build(),
                Ubicacion.builder().nombreSala("La Salvaje").direccion("Calle Martínez Vigil, 17, Oviedo, Asturias").build(),
                Ubicacion.builder().nombreSala("Mardi Gras").direccion("Rúa San Pedro de Mezonzo, 1, A Coruña, Galicia").build(),
                Ubicacion.builder().nombreSala("Sala Capitol").direccion("Rúa do Preguntoiro, 23, Santiago de Compostela, Galicia").build(),
                Ubicacion.builder().nombreSala("El Gran Café").direccion("Avenida de la Facultad, s/n, León").build(),
                Ubicacion.builder().nombreSala("Studio 54 León").direccion("Calle Cardiles, 2, León").build()
            );
            ubicacionRepo.saveAll(ubicaciones);
            System.out.println("Ubicaciones insertadas.");
        } else {
            System.out.println("Ubicaciones ya existen.");
        }
    }
     
    
    private void ejecutarScriptSQL(String path) {
        try {
            var resource = getClass().getClassLoader().getResource(path);
            if (resource == null) {
                System.err.println("Archivo no encontrado: " + path);
                return;
            }
            String sql = new String(resource.openStream().readAllBytes(), StandardCharsets.UTF_8);
            jdbcTemplate.execute(sql);
            System.out.println("Script SQL ejecutado: " + path);
        } catch (Exception e) {
            System.err.println("Error ejecutando script SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
