package Pruebas;

import Dao.*;
import Entidades.*;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Interfaces.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestInventario {

    private static EntityManagerFactory emf;
    private static IDaoEquipoDeComputo daoEquipo;
    private static IDaoMovil daoMovil;
    private static IDaoModelo daoModelo;
    private static IDaoEmpresa daoEmp;
    private static IDaoSucursal daoSuc;

    private static Long idSucursalTest;

    @BeforeAll
    public static void setUp() {
        // --- CONFIGURACIÓN H2 ---
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:testdb_inventario;DB_CLOSE_DELAY=-1");
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        properties.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");

        emf = Persistence.createEntityManagerFactory("ConexionPU", properties);
        
        daoEquipo = new DaoEquipoDeComputo(emf);
        daoMovil = new DaoMovil(emf);
        daoModelo = new DaoModelo(emf);
        daoEmp = new DaoEmpresa(emf);
        daoSuc = new DaoSucursal(emf); 

        Empresa e = new Empresa();
        e.setNombre("X");
        daoEmp.guardar(e);
        
        Sucursal s = new Sucursal();
        s.setNombre("Bodega Test");
        s.setEmpresa(e);
        s.setUbicacion("X");
        daoSuc.guardar(s);
        idSucursalTest = s.getIdSucursal();
    }

    @Test
    @Order(1)
    @DisplayName("Debe guardar un Equipo Móvil (Herencia)")
    public void testGuardarMovil() {
        // ARRANGE
        Sucursal sucursal = daoSuc.buscarPorId(idSucursalTest);

        Modelo modelo = new Modelo();
        modelo.setMarca("Apple");
        modelo.setNombre("iPhone 15");
        daoModelo.guardar(modelo);

        Movil celular = new Movil(Boolean.TRUE, "55-5555-5555", Boolean.TRUE, Boolean.TRUE);
        celular.setGri(500);
        celular.setCondicion(CondicionFisica.NUEVO);
        celular.setEstado(EstadoEquipo.DISPONIBLE);
        celular.setModelo(modelo);
        celular.setFactura("X");

        // ACT
        daoMovil.guardar(celular);

        // ASSERT
        assertNotNull(celular.getIdEquipo(), "El ID debería generarse en la tabla padre");
        assertEquals(EstadoEquipo.DISPONIBLE, celular.getEstado());
    }

    @Test
    @Order(2)
    @DisplayName("Criteria API: Buscar por Marca y Estado")
    public void testBusquedaAvanzada() {
        // ARRANGE
        String busquedaTexto = "Apple";
        EstadoEquipo estado = EstadoEquipo.DISPONIBLE;

        // ACT
        List<EquipoDeComputo> resultados = daoEquipo.buscarConFiltros(null, estado, busquedaTexto);

        // ASSERT
        assertFalse(resultados.isEmpty(), "Debería encontrar el iPhone creado antes");
        assertEquals("iPhone 15", resultados.get(0).getModelo().getNombre());
    }
}