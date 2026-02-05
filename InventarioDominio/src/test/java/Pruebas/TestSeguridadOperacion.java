package Pruebas;

import Dao.*;
import Entidades.*;
import Enums.CondicionFisica;
import Enums.RolUsuario;
import Enums.EstadoEquipo;
import Interfaces.IDaoEquipoAsignado;
import Interfaces.IDaoEquipoDeComputo;
import Interfaces.IDaoModelo;
import Interfaces.IDaoMovil;
import Interfaces.IDaoSucursal;
import Interfaces.IDaoTrabajador;
import Interfaces.IDaoUsuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSeguridadOperacion {

    private static EntityManagerFactory emf;
    private static IDaoUsuario daoUsuario;
    private static IDaoEquipoAsignado daoAsignacion;

    private static IDaoModelo daoModelo;
    private static IDaoSucursal daoSucursal;

    private static IDaoTrabajador daoTrab;
    private static IDaoMovil daoEq;

    @BeforeAll
    public static void setUp() {
        emf = Persistence.createEntityManagerFactory("ConexionPU");
        daoUsuario = new DaoUsuario();
        daoAsignacion = new DaoEquipoAsignado();
        daoTrab = new DaoTrabajador();
        daoEq = new DaoMovil();
        daoModelo = new DaoModelo();
        daoSucursal = new DaoSucursal();
    }

    @Test
    @DisplayName("Debe permitir login con credenciales correctas")
    public void testLoginExitoso() {
        // ARRANGE
        UsuarioSistema user = new UsuarioSistema();
        user.setUsername("admin_test");
        user.setPassword("secret123");
        user.setRol(RolUsuario.ADMIN);
        daoUsuario.guardar(user);

        // ACT
        UsuarioSistema resultado = daoUsuario.login("admin_test", "secret123");

        // ASSERT
        assertNotNull(resultado, "El usuario logueado no debe ser null");
        assertEquals(RolUsuario.ADMIN, resultado.getRol());
    }

    @Test
    @DisplayName("Debe rechazar login con contraseña incorrecta")
    public void testLoginFallido() {
        // ARRANGE
        String user = "admin_test";
        String passIncorrecta = "bla";

        // ACT
        UsuarioSistema resultado = daoUsuario.login(user, passIncorrecta);

        // ASSERT
        assertNull(resultado, "El login debe retornar null si falla");
    }

    @Test
    @DisplayName("Debe crear una asignación y reflejar que el equipo está prestado")
    public void testAsignarEquipo() {
        // ARRANGE
        Trabajador t = new Trabajador();
        
        t = daoTrab.buscarPorId(1L);
        
        EquipoDeComputo celular = new Movil(Boolean.TRUE, "55-5555-5555", Boolean.TRUE, Boolean.TRUE);
        
        celular.setGri(500);
        celular.setCondicion(CondicionFisica.NUEVO);
        celular.setEstado(EstadoEquipo.DISPONIBLE);
   
        Modelo modelo = new Modelo();
        Sucursal sucursal = new Sucursal();

        EquipoAsignado asignacion = new EquipoAsignado();
        asignacion.setTrabajador(t);
        asignacion.setFechaEntrega(LocalDate.now());
        asignacion.setTrabajador(t);

        // ACT
        modelo = daoModelo.buscarPorId(1L);
        sucursal = daoSucursal.buscarPorId(1L);
        celular.setSucursal(sucursal);
        celular.setModelo(modelo);;
        
        
        daoAsignacion.guardar(asignacion);
//        List<EquipoAsignado> activos = daoAsignacion.(t.getIdTrabajador());

        // ASSERT
        assertNotNull(asignacion.getId());
//        assertEquals(1, activos.size(), "El trabajador debe tener 1 equipo activo");
    }
}
