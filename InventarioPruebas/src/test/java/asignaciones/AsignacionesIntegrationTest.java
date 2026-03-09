package asignaciones;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Enums.EstadoEquipo;
import Implementaciones.FachadaPrestamos;
import Servicios.ServicioPrestamos;
import config.BaseIntegrationTest;
import excepciones.ReglaNegocioException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AsignacionesIntegrationTest extends BaseIntegrationTest {

    @Mock
    private DaoEquipoDeComputo daoEquipo;
    @Mock
    private DaoUsuario daoUsuario;
    @Mock
    private DaoEquipoAsignado daoAsignacion;

    @InjectMocks
    private ServicioPrestamos servicioPrestamos;
    
    private FachadaPrestamos fachadaPrestamos;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        fachadaPrestamos = new FachadaPrestamos(servicioPrestamos);
    }

    @Test
    void AS001_asignarEquipo_DisponibleAUsuarioActivo_Exitoso() {
        printTestInfo("AS-001: Asignar equipo disponible a usuario activo");
        
        // ARRANGE
        Long idEquipo = 1L;
        Long idUsuario = 1L;

        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(idEquipo);
        equipoMock.setGry(12345);
        equipoMock.setEstado(EstadoEquipo.EN_STOCK);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(idUsuario);
        usuarioMock.setNombre("Juan Pérez");
        usuarioMock.setActivo(true);

        when(daoEquipo.buscarPorId(idEquipo)).thenReturn(equipoMock);
        when(daoUsuario.buscarPorId(idUsuario)).thenReturn(usuarioMock);
        when(daoAsignacion.buscarPorUsuarioActivo(idUsuario)).thenReturn(List.of());
        
        when(daoAsignacion.guardar(any(EquipoAsignado.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        fachadaPrestamos.asignarEquipo(idEquipo, idUsuario);

        // ASSERT
        assertAll("Verificar asignación",
            () -> assertEquals(EstadoEquipo.ASIGNADO, equipoMock.getEstado(), 
                "Equipo debe cambiar a ASIGNADO"),
            () -> verify(daoEquipo, times(1)).actualizar(equipoMock),
            () -> verify(daoAsignacion, times(1)).guardar(any(EquipoAsignado.class))
        );
    }

    @Test
    void AS002_asignarEquipo_YaAsignado_LanzaExcepcion() {
        printTestInfo("AS-002: Asignar equipo ya asignado");
        
        // ARRANGE
        Long idEquipo = 1L;
        Long idUsuario = 2L;

        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(idEquipo);
        equipoMock.setEstado(EstadoEquipo.ASIGNADO);

        when(daoEquipo.buscarPorId(idEquipo)).thenReturn(equipoMock);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPrestamos.asignarEquipo(idEquipo, idUsuario),
            "Debe lanzar excepción por equipo ya asignado"
        );
        
        assertTrue(exception.getMessage().contains("no está disponible"),
            "Mensaje de error incorrecto");
        
        verify(daoAsignacion, never()).guardar(any(EquipoAsignado.class));
    }

    @Test
    void AS003_asignarEquipo_UsuarioInactivo_LanzaExcepcion() {
        printTestInfo("AS-003: Asignar equipo a usuario inactivo");
        
        // ARRANGE
        Long idEquipo = 1L;
        Long idUsuario = 1L;

        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(idEquipo);
        equipoMock.setEstado(EstadoEquipo.EN_STOCK);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(idUsuario);
        usuarioMock.setActivo(false);

        when(daoEquipo.buscarPorId(idEquipo)).thenReturn(equipoMock);
        when(daoUsuario.buscarPorId(idUsuario)).thenReturn(usuarioMock);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPrestamos.asignarEquipo(idEquipo, idUsuario),
            "Debe lanzar excepción por usuario inactivo"
        );
        
        assertTrue(exception.getMessage().contains("usuario está inactivo"),
            "Mensaje de error incorrecto");
    }

    // ============= PRUEBAS AS-004 a AS-005: Devoluciones =============

    @Test
    void AS004_devolverEquipo_Asignado_Exitoso() {
        printTestInfo("AS-004: Devolver equipo asignado");
        
        // ARRANGE
        Long idAsignacion = 1L;

        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(1L);
        equipoMock.setEstado(EstadoEquipo.ASIGNADO);

        EquipoAsignado asignacionMock = new EquipoAsignado();
        asignacionMock.setId(idAsignacion);
        asignacionMock.setEquipoDeComputo(equipoMock);
        asignacionMock.setFechaEntrega(LocalDate.now().minusDays(5));
        asignacionMock.setFechaDevolucion(null);

        when(daoAsignacion.buscarPorId(idAsignacion)).thenReturn(asignacionMock);

        // ACT
        fachadaPrestamos.devolverEquipo(idAsignacion);

        // ASSERT
        assertAll("Verificar devolución",
            () -> assertNotNull(asignacionMock.getFechaDevolucion(), 
                "Fecha de devolución debe estar asignada"),
            () -> assertEquals(EstadoEquipo.EN_STOCK, equipoMock.getEstado(), 
                "Equipo debe volver a EN_STOCK"),
            () -> verify(daoAsignacion, times(1)).actualizar(asignacionMock),
            () -> verify(daoEquipo, times(1)).actualizar(equipoMock)
        );
    }

    @Test
    void AS005_devolverEquipo_YaDevuelto_LanzaExcepcion() {
        printTestInfo("AS-005: Devolver equipo ya devuelto");
        
        // ARRANGE
        Long idAsignacion = 1L;

        EquipoAsignado asignacionMock = new EquipoAsignado();
        asignacionMock.setId(idAsignacion);
        asignacionMock.setFechaDevolucion(LocalDate.now().minusDays(2));

        when(daoAsignacion.buscarPorId(idAsignacion)).thenReturn(asignacionMock);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPrestamos.devolverEquipo(idAsignacion),
            "Debe lanzar excepción por equipo ya devuelto"
        );
        
        assertTrue(exception.getMessage().contains("ya fue devuelto"),
            "Mensaje de error incorrecto");
        
        verify(daoAsignacion, never()).actualizar(any(EquipoAsignado.class));
    }

    // ============= PRUEBA AS-006: Consultas =============

    @Test
    void AS006_obtenerEquiposDeUsuario_Activo_RetornaLista() {
        printTestInfo("AS-006: Obtener equipos de usuario activo");
        
        // ARRANGE
        Long idUsuario = 1L;
        
        EquipoAsignado asignacionMock1 = new EquipoAsignado();
        asignacionMock1.setId(1L);
        
        EquipoAsignado asignacionMock2 = new EquipoAsignado();
        asignacionMock2.setId(2L);
        
        List<EquipoAsignado> asignacionesMock = Arrays.asList(
            asignacionMock1, asignacionMock2
        );
        
        when(daoAsignacion.buscarPorUsuarioActivo(idUsuario))
            .thenReturn(asignacionesMock);

        // ACT
        List<AsignacionDTO> resultados = 
            fachadaPrestamos.obtenerEquiposDeUsuarios(idUsuario);

        // ASSERT
        assertAll("Verificar consulta",
            () -> assertNotNull(resultados, "La lista no debe ser nula"),
            () -> assertEquals(2, resultados.size(), "Debe retornar 2 asignaciones"),
            () -> verify(daoAsignacion, times(1)).buscarPorUsuarioActivo(idUsuario)
        );
    }

    // ============= PRUEBA AS-007: Búsqueda =============

    @Test
    void AS007_buscarAsignaciones_PorFiltro_RetornaListaFiltrada() {
        printTestInfo("AS-007: Buscar asignaciones por filtro");
        
        // ARRANGE
        String filtro = "Juan";
        
        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(1L);
        equipoMock.setGry(12345);
        
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(1L);
        usuarioMock.setNombre("Juan Pérez");
        
        EquipoAsignado asignacionMock = new EquipoAsignado();
        asignacionMock.setId(1L);
        asignacionMock.setEquipoDeComputo(equipoMock);
        asignacionMock.setUsuario(usuarioMock);
        asignacionMock.setFechaEntrega(LocalDate.now());
        
        List<EquipoAsignado> todas = Arrays.asList(asignacionMock);
        when(daoAsignacion.buscarTodos()).thenReturn(todas);

        // ACT
        List<AsignacionDTO> resultados = fachadaPrestamos.buscarAsignaciones(filtro);

        // ASSERT
        assertNotNull(resultados, "La lista no debe ser nula");
        verify(daoAsignacion, times(1)).buscarTodos();
    }

    // ============= PRUEBA AS-008: Límite de equipos =============

    @Test
    void AS008_usuarioPuedeRecibirMasEquipos_ExcedeLimite_LanzaExcepcion() {
        printTestInfo("AS-008: Asignar cuando usuario excede límite");
        
        // ARRANGE
        Long idUsuario = 1L;
        int limiteMaximo = 2;
        
        // Simular que ya tiene 2 equipos asignados
        when(daoAsignacion.buscarPorUsuarioActivo(idUsuario))
            .thenReturn(Arrays.asList(
                new EquipoAsignado(),
                new EquipoAsignado()
            ));

        // ACT & ASSERT
        boolean puedeRecibir = fachadaPrestamos.usuarioPuedeRecibirMasEquipos(idUsuario, limiteMaximo);
        
        assertFalse(puedeRecibir, "Usuario no debe poder recibir más equipos");
    }

    // ============= PRUEBAS AS-009 a AS-010: Rangos de fechas =============

    @Test
    void AS009_obtenerAsignacionesPorRangoFechas_Valido_RetornaLista() {
        printTestInfo("AS-009: Obtener asignaciones por rango de fechas válido");
        
        // ARRANGE
        String fechaInicio = "2025-01-01";
        String fechaFin = "2025-01-31";

        // ACT
        List<AsignacionDTO> resultados = 
            fachadaPrestamos.obtenerAsignacionesPorRangoFechas(fechaInicio, fechaFin);

        // ASSERT
        assertNotNull(resultados, "La lista no debe ser nula");
    }

    @Test
    void AS010_obtenerAsignacionesPorRangoFechas_FechasInvertidas_LanzaExcepcion() {
        printTestInfo("AS-010: Obtener asignaciones con fechas invertidas");
        
        // ARRANGE
        String fechaInicio = "2025-02-01";
        String fechaFin = "2025-01-01";

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaPrestamos.obtenerAsignacionesPorRangoFechas(fechaInicio, fechaFin),
            "Debe lanzar excepción por fechas invertidas"
        );
        
        assertTrue(exception.getMessage().contains("fecha de inicio no puede ser mayor"),
            "Mensaje de error incorrecto");
    }
}