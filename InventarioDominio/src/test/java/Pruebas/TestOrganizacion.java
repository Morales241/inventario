package Pruebas;

import Dao.*;
import Entidades.*;
import Interfaces.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrganizacion {

    private static EntityManagerFactory emf;
    private static IDaoEmpresa daoEmpresa;
    private static IDaoSucursal daoSucursal;
    private static IDaoTrabajador daoTrabajador;
    private static IDaoPuesto daoPuesto;
    private static IDaoDepartamento daoDepto;

    private static Long idEmpresaGlobal;
    private static Long idPuestoGlobal;

    @BeforeAll
    public static void setUp() {
        // --- CONFIGURACIÓN H2 ---
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:testdb_org;DB_CLOSE_DELAY=-1");
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        properties.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");

        emf = Persistence.createEntityManagerFactory("ConexionPU", properties);
        
        daoEmpresa = new DaoEmpresa(emf);
        daoSucursal = new DaoSucursal(emf);
        daoTrabajador = new DaoTrabajador(emf);
        daoPuesto = new DaoPuesto(emf);
        daoDepto = new DaoDepartamento(emf);
    }

    @Test
    @Order(1)
    @DisplayName("Debe crear una Empresa y recuperar su ID autogenerado")
    public void testCrearEmpresa() {
        Empresa nuevaEmpresa = new Empresa();
        nuevaEmpresa.setNombre("Tech Solutions");

        daoEmpresa.guardar(nuevaEmpresa);
        idEmpresaGlobal = nuevaEmpresa.getIdEmpresa(); 

        assertNotNull(idEmpresaGlobal, "El ID no debería ser nulo tras guardar");
    }

    @Test
    @Order(2)
    @DisplayName("Debe crear la cadena Sucursal -> Depto -> Puesto")
    public void testCrearEstructura() {
        Empresa empresa = daoEmpresa.buscarPorId(idEmpresaGlobal);
        
        Sucursal suc = new Sucursal();
        suc.setNombre("Sede Central");
        suc.setEmpresa(empresa);
        suc.setUbicacion("Av. Reforma 123");

        Departamento depto = new Departamento();
        depto.setNombre("Sistemas");
        depto.setSucursal(suc);

        Puesto puesto = new Puesto();
        puesto.setNombre("Desarrollador Java");
        puesto.setDepartamento(depto);

        daoSucursal.guardar(suc);
        daoDepto.guardar(depto);
        daoPuesto.guardar(puesto);
        idPuestoGlobal = puesto.getIdPuesto();

        assertNotNull(idPuestoGlobal);
        assertEquals("Sede Central", depto.getSucursal().getNombre());
    }

    @Test
    @Order(3)
    @DisplayName("Debe encontrar trabajador usando filtros (Nombre y Puesto)")
    public void testBuscarTrabajador() {
        Puesto puesto = daoPuesto.buscarPorId(idPuestoGlobal);
        Trabajador trabajador = new Trabajador();
        trabajador.setNombre("Roberto Gomez");
        trabajador.setNoNomina("00000255255");
        trabajador.setPuesto(puesto);
        daoTrabajador.guardar(trabajador);

        List<Trabajador> resultados = daoTrabajador.busquedaConFiltros("Roberto Gomez", "", puesto.getNombre());

        assertFalse(resultados.isEmpty(), "La lista no debería estar vacía");
        assertEquals("Roberto Gomez", resultados.get(0).getNombre());
    }
}