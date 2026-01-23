package Pruebas;

import Dao.*;
import Entidades.*;
import Interfaces.IDaoDepartamento;
import Interfaces.IDaoEmpresa;
import Interfaces.IDaoPuesto;
import Interfaces.IDaoSucursal;
import Interfaces.IDaoTrabajador;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrganizacion {

    private static EntityManagerFactory emf;
    private static IDaoEmpresa daoEmpresa;
    private static IDaoSucursal daoSucursal;
    private static IDaoTrabajador daoTrabajador;
    private static IDaoPuesto daoPuesto;
    private static IDaoDepartamento daoDepto;

    // IDs estáticos para compartir entre tests
    private static Long idEmpresaGlobal;
    private static Long idPuestoGlobal;

    @BeforeAll
    public static void setUp() {
        emf = Persistence.createEntityManagerFactory("ConexionPU");
        daoEmpresa = new DaoEmpresa();
        daoSucursal = new DaoSucursal();
        daoTrabajador = new DaoTrabajador();
        daoPuesto = new DaoPuesto();
        daoDepto = new DaoDepartamento();
    }

    @Test
    @Order(1)
    @DisplayName("Debe crear una Empresa y recuperar su ID autogenerado")
    public void testCrearEmpresa() {
        // ARRANGE 
        Empresa nuevaEmpresa = new Empresa();
        nuevaEmpresa.setNombre("Tech Solutions");

        // ACT 
        daoEmpresa.guardar(nuevaEmpresa);
        idEmpresaGlobal = nuevaEmpresa.getIdEmpresa(); 

        // ASSERT 
        assertNotNull(idEmpresaGlobal, "El ID no debería ser nulo tras guardar");
        assertTrue(idEmpresaGlobal > 0, "El ID debería ser mayor a 0");
    }

    @Test
    @Order(2)
    @DisplayName("Debe crear la cadena Sucursal -> Depto -> Puesto")
    public void testCrearEstructura() {
        // ARRANGE
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

        // ACT 
        daoSucursal.guardar(suc);
        daoDepto.guardar(depto);
        daoPuesto.guardar(puesto);
        idPuestoGlobal = puesto.getIdPuesto();

        // ASSERT
        assertNotNull(idPuestoGlobal);
        assertEquals("Sede Central", depto.getSucursal().getNombre());
    }

    @Test
    @Order(3)
    @DisplayName("Debe encontrar trabajador usando filtros (Nombre y Puesto)")
    public void testBuscarTrabajador() {
        // ARRANGE
        Puesto puesto = daoPuesto.buscarPorId(idPuestoGlobal);
        Trabajador trabajador = new Trabajador();
        trabajador.setNombre("Roberto Gomez");
        trabajador.setNoNomina("00000255255");
        trabajador.setPuesto(puesto);
        daoTrabajador.guardar(trabajador);

        // ACT
        List<Trabajador> resultados = daoTrabajador.busquedaConFiltros("Roberto Gomez", "", puesto.getNombre());

        // ASSERT
        assertFalse(resultados.isEmpty(), "La lista no debería estar vacía");
        assertEquals("Roberto Gomez", resultados.get(0).getNombre());
    }
}