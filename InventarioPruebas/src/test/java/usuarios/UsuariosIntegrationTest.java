package usuarios;

import Dao.*;
import Dtos.*;
import Entidades.CuentaSistema;
import Entidades.EquipoAsignado;
import Entidades.Puesto;
import Entidades.Usuario;
import Enums.*;
import Implementaciones.FachadaPersonas;
import Servicios.ServicioPersonas;
import config.BaseIntegrationTest;
import excepciones.ReglaNegocioException;
import excepciones.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import utils.TestDataFactory;

public class UsuariosIntegrationTest extends BaseIntegrationTest {

    @Mock
    private DaoUsuario daoUsuario;
    @Mock
    private DaoCuentaSistema daoCuenta;
    @Mock
    private DaoPuesto daoPuesto;

    @InjectMocks
    private ServicioPersonas servicioPersonas;
    
    private FachadaPersonas fachadaPersonas;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        fachadaPersonas = new FachadaPersonas(servicioPersonas);
    }

    // ============= PRUEBAS US-001 a US-002: Guardar Usuarios =============

    @Test
    void US001_guardarUsuario_DatosValidos_Exitoso() {
        printTestInfo("US-001: Guardar usuario con datos válidos");
        
        // ARRANGE
        UsuarioDTO dto = TestDataFactory.crearUsuarioValido();

        Puesto puestoMock = new Puesto();
        puestoMock.setIdPuesto(1L);
        when(daoPuesto.buscarPorId(1L)).thenReturn(puestoMock);

        when(daoUsuario.busquedaEspecifica(null, "NOM-001")).thenReturn(null);
        
        when(daoUsuario.guardar(any(Usuario.class)))
            .thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setIdUsuario(100L);
                return u;
            });

        // ACT
        fachadaPersonas.guardarUsuario(dto);

        // ASSERT
        verify(daoUsuario, times(1)).guardar(any(Usuario.class));
        verify(daoUsuario, never()).actualizar(any(Usuario.class));
    }

    @Test
    void US002_guardarUsuario_NominaDuplicada_LanzaExcepcion() {
        printTestInfo("US-002: Guardar usuario con nómina duplicada");
        
        // ARRANGE
        UsuarioDTO dto = TestDataFactory.crearUsuarioValido();

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(200L);
        usuarioExistente.setNoNomina("NOM-001");
        
        when(daoUsuario.busquedaEspecifica(null, "NOM-001"))
            .thenReturn(usuarioExistente);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPersonas.guardarUsuario(dto),
            "Debe lanzar excepción por nómina duplicada"
        );
        
        assertTrue(exception.getMessage().contains("Ya existe un usuario con el número de nómina"),
            "Mensaje de error incorrecto");
        
        verify(daoUsuario, never()).guardar(any(Usuario.class));
    }

    @Test
    void US003_buscarUsuarios_PorFiltroGlobal_RetornaLista() {
        printTestInfo("US-003: Buscar usuarios por filtro global");
        
        // ARRANGE
        String filtro = "Juan";
        
        List<Usuario> usuariosMock = Arrays.asList(
            new Usuario(),
            new Usuario()
        );
        
        when(daoUsuario.busquedaConFiltros(eq("Juan"), isNull(), isNull()))
            .thenReturn(usuariosMock);

        // ACT
        List<UsuarioDTO> resultados = fachadaPersonas.buscarUsuarios(filtro);

        // ASSERT
        assertNotNull(resultados, "La lista no debe ser nula");
        assertEquals(2, resultados.size(), "Debe retornar 2 usuarios");
        
        verify(daoUsuario, times(1))
            .busquedaConFiltros("Juan", null, null);
    }
    
    @Test
    void US004_cambiarEstadoUsuario_SinEquiposAsignados_Exitoso() {
        printTestInfo("US-004: Cambiar estado de usuario sin equipos asignados");
        
        // ARRANGE
        Long idUsuario = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(idUsuario);
        usuarioMock.setEquiposAsignados(List.of()); // Sin equipos

        when(daoUsuario.buscarPorId(idUsuario)).thenReturn(usuarioMock);
        when(daoUsuario.actualizar(any(Usuario.class))).thenReturn(usuarioMock);

        // ACT
        fachadaPersonas.cambiarEstadoUsuario(idUsuario, false);

        // ASSERT
        assertFalse(usuarioMock.getActivo(), "Usuario debe estar inactivo");
        verify(daoUsuario, times(1)).actualizar(usuarioMock);
    }

    @Test
    void US005_cambiarEstadoUsuario_ConEquiposAsignados_LanzaExcepcion() {
        printTestInfo("US-005: Cambiar estado de usuario con equipos asignados");
        
        // ARRANGE
        Long idUsuario = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(idUsuario);
        
        EquipoAsignado asignacionActiva = new EquipoAsignado();
        asignacionActiva.setFechaDevolucion(null); // Activa
        usuarioMock.setEquiposAsignados(Arrays.asList(asignacionActiva));

        when(daoUsuario.buscarPorId(idUsuario)).thenReturn(usuarioMock);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPersonas.cambiarEstadoUsuario(idUsuario, false),
            "Debe lanzar excepción por equipos asignados"
        );
        
        assertTrue(exception.getMessage().contains("No se puede desactivar un usuario que tiene equipos asignados"),
            "Mensaje de error incorrecto");
        
        verify(daoUsuario, never()).actualizar(any(Usuario.class));
    }

    @Test
    void US006_login_CredencialesValidas_RetornaDTO() {
        printTestInfo("US-006: Login con credenciales válidas");
        
        // ARRANGE
        String username = "admin";
        String password = "1234";
        
        CuentaSistema cuentaMock = new CuentaSistema();
        cuentaMock.setId(1L);
        cuentaMock.setUsername(username);
        cuentaMock.setRol(RolCuenta.ADMIN);
        
        when(daoCuenta.login(username, password)).thenReturn(cuentaMock);

        // ACT
        CuentaSistemaDTO resultado = fachadaPersonas.login(username, password);

        // ASSERT
        assertAll("Verificar resultado del login",
            () -> assertNotNull(resultado, "Resultado no debe ser nulo"),
            () -> assertEquals(username, resultado.getUsername(), "Username incorrecto"),
            () -> assertEquals(RolCuenta.ADMIN.name(), resultado.getRol(), "Rol incorrecto")
        );
        
        verify(daoCuenta, times(1)).login(username, password);
    }

    @Test
    void US007_login_CredencialesInvalidas_LanzaExcepcion() {
        printTestInfo("US-007: Login con credenciales inválidas");
        
        // ARRANGE
        String username = "admin";
        String password = "wrong";
        
        when(daoCuenta.login(username, password)).thenReturn(null);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPersonas.login(username, password),
            "Debe lanzar excepción por credenciales inválidas"
        );
        
        assertTrue(exception.getMessage().contains("Usuario o contraseña incorrectos"),
            "Mensaje de error incorrecto");
    }

    @Test
    void US008_guardarCuentaSistema_UsernameDuplicado_LanzaExcepcion() {
        printTestInfo("US-008: Guardar cuenta de sistema con username duplicado");
        
        // ARRANGE
        CuentaSistemaDTO dto = TestDataFactory.crearCuentaSistemaValida();
        
        CuentaSistema cuentaExistente = new CuentaSistema();
        cuentaExistente.setId(200L);
        cuentaExistente.setUsername("admin");
        
        when(daoCuenta.busquedaEspecifica("admin")).thenReturn(cuentaExistente);

        // ACT & ASSERT
        assertThrows(ReglaNegocioException.class, 
            () -> fachadaPersonas.guardarCuentaSistema(dto),
            "Debe lanzar excepción por username duplicado"
        );
        
        verify(daoCuenta, never()).guardar(any(CuentaSistema.class));
    }

    @Test
    void US009_eliminarCuentaSistema_UltimoAdmin_LanzaExcepcion() {
        printTestInfo("US-009: Eliminar último administrador");
        
        // ARRANGE
        Long id = 1L;
        CuentaSistema cuentaMock = new CuentaSistema();
        cuentaMock.setId(id);
        cuentaMock.setRol(RolCuenta.ADMIN);
        
        CuentaSistema otroAdmin = new CuentaSistema();
        otroAdmin.setRol(RolCuenta.ADMIN);
        
        List<CuentaSistema> todasLasCuentas = Arrays.asList(cuentaMock); // Solo un admin
        
        when(daoCuenta.buscarPorId(id)).thenReturn(cuentaMock);
        when(daoCuenta.buscarTodos()).thenReturn(todasLasCuentas);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPersonas.eliminarCuentaSistema(id),
            "Debe lanzar excepción por último administrador"
        );
        
        assertTrue(exception.getMessage().contains("No se puede eliminar el último administrador"),
            "Mensaje de error incorrecto");
        
        verify(daoCuenta, never()).eliminar(anyLong());
    }
}