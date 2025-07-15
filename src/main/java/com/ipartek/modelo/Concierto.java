package com.ipartek.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conciertos")  
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concierto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String grupo;
    
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
	private BigDecimal precio;

    private LocalDate fecha;
    
    @Min(value = 0, message = "El aforo debe ser mayor que cero")
    private int aforo;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id")  
    private Ubicacion ubicacion;

    private String foto;

    @OneToMany(mappedBy = "concierto")
    @JsonManagedReference
    private List<Entrada> entradas;
}
