package Entidades;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EquipoDeComputo")
@Inheritance(strategy = InheritanceType.JOINED) 
public class EquipoDeComputo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_EquipoDeComputo")
    private Integer idEquipo;

    @Column(name = "Condicion", nullable = false)
    private String condicion;
    
    @Column(name = "GRI", nullable = false)
    private Integer gri;
    
    @Column(name = "Factura", nullable = false)
    private String factura;
    
    @Column(name = "Estado", nullable = false)
    private String estado;
    
    @Column(name = "Observaciones")
    private String observaciones;
    
    @Column(name = "FechaCompra")
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "IdSucursal")
    private Sucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "IdModelo")
    private Modelo modelo;


}