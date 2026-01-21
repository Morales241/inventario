package Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author JMorales
 */
@Entity
public class EquipoAsignado implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "FechaEntrega")
    private LocalDate fechaEntrega;
    
    @Column(name = "FechaDevolucion")
    private LocalDate fechaDevolucion;
    
    
    
}
