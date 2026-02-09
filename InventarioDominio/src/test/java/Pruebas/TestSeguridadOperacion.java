package Pruebas;

import Dao.*;
import Entidades.*;
import Enums.*;
import Interfaces.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSeguridadOperacion {

    private static EntityManagerFactory emf;
    private static IDaoUsuario daoUsuario;
    private static IDaoEquipoAsignado daoAsignacion;
    private static IDaoModelo daoModelo;
    private static IDaoSucursal daoSucursal;
    private static IDaoTrabajador daoTrab;
    private static IDaoEmpresa daoEmpresa; 
    private static IDaoDepartamento daoDepto;
    private static IDaoPuesto daoPuesto; 

    @BeforeAll
    public static void setUp() {
        // --- CONFIGURACIÓN H2 ---
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:testdb_seguridad;DB_CLOSE_DELAY=-1");
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        properties.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");

        emf = Persistence.createEntityManagerFactory("ConexionPU", properties);
        
        daoUsuario = new DaoUsuario(emf);
        daoAsignacion = new DaoEquipoAsignado(emf);
        daoTrab = new DaoTrabajador(emf);
        daoModelo = new DaoModelo(emf);
        daoSucursal = new DaoSucursal(emf);
        
        daoEmpresa = new DaoEmpresa(emf);
        daoDepto = new DaoDepartamento(emf);
        daoPuesto = new DaoPuesto(emf);

        crearDatosBase();
    }
    
    private static void crearDatosBase() {
        Empresa emp = new Empresa();
        emp.setNombre("Seguridad Corp");
        daoEmpresa.guardar(emp);
        
        Sucursal suc = new Sucursal();
        suc.setNombre("Norte");
        suc.setEmpresa(emp);
        daoSucursal.guardar(suc); 
        
        Departamento dep = new Departamento();
        dep.setNombre("TI");
        dep.setSucursal(suc);
        daoDepto.guardar(dep);
        
        Puesto puesto = new Puesto();
        puesto.setNombre("Admin");
        puesto.setDepartamento(dep);
        daoPuesto.guardar(puesto);
        
        Trabajador t = new Trabajador();
        t.setNombre("Juan Perez");
        t.setPuesto(puesto);
        daoTrab.guardar(t);
        
        Modelo m = new Modelo();
        m.setNombre("X-Phone");
        daoModelo.guardar(m); 
    }

    @Test
    @DisplayName("Debe permitir login con credenciales correctas")
    public void testLoginExitoso() {
        UsuarioSistema user = new UsuarioSistema();
        user.setUsername("admin_test");
        user.setPassword("secret123");
        user.setRol(RolUsuario.ADMIN);
        daoUsuario.guardar(user);

        UsuarioSistema resultado = daoUsuario.login("admin_test", "secret123");

        assertNotNull(resultado, "El usuario logueado no debe ser null");
        assertEquals(RolUsuario.ADMIN, resultado.getRol());
    }

    @Test
    @DisplayName("Debe crear una asignación y reflejar que el equipo está prestado")
    public void testAsignarEquipo() {
        // ARRANGE
        List<Trabajador> trabajadores = daoTrab.buscarTodos();
        Trabajador t = trabajadores.get(0); 
        
        EquipoDeComputo celular = new Movil(Boolean.TRUE, "55-5555-5555", Boolean.TRUE, Boolean.TRUE);
        celular.setGri(500);
        celular.setCondicion(CondicionFisica.NUEVO);
        celular.setEstado(EstadoEquipo.DISPONIBLE);
   
        Modelo modelo = daoModelo.buscarTodos().get(0);
        Sucursal sucursal = daoSucursal.buscarTodos().get(0);

        celular.setSucursal(sucursal);
        celular.setModelo(modelo);
        
        IDaoMovil daoMovil = new DaoMovil(emf); 
        daoMovil.guardar((Movil)celular);

        EquipoAsignado asignacion = new EquipoAsignado();
        asignacion.setTrabajador(t);
        asignacion.setFechaEntrega(LocalDate.now());
        asignacion.setEquipoDeComputo(celular); 
        // ACT
        daoAsignacion.guardar(asignacion);
        
        List<EquipoAsignado> activos = daoAsignacion.buscarPorTrabajadorActivo(t.getIdTrabajador());

        // ASSERT
        assertNotNull(asignacion.getId());
        assertFalse(activos.isEmpty());
    }
}