package equipos;

import Dao.*;
import Dtos.*;
import Entidades.EquipoDeComputo;
import Entidades.EquipoDeEscritorio;
import Entidades.Modelo;
import Entidades.Movil;
import Entidades.OtroEquipo;
import Entidades.Sucursal;
import Enums.*;
import Implementaciones.FachadaEquipos;
import Servicios.ServicioEquipos;
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

public class EquiposIntegrationTest extends BaseIntegrationTest {

    @Mock
    private DaoEquipoDeComputo daoEquipoGeneral;
    @Mock
    private DaoEquipoDeEscritorio daoEscritorio;
    @Mock
    private DaoMovil daoMovil;
    @Mock
    private DaoOtroEquipo daoOtro;
    @Mock
    private DaoModelo daoModelo;
    @Mock
    private DaoSucursal daoSucursal;

    @InjectMocks
    private ServicioEquipos servicioEquipos;
    
    private FachadaEquipos fachadaEquipos;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        fachadaEquipos = new FachadaEquipos(servicioEquipos);
    }
    
    @Test
    void EQ001_guardarEscritorio_DatosValidos_RetornaDTOConID() throws Exception {
        printTestInfo("EQ-001: Guardar equipo de escritorio con datos válidos");
        
        // ARRANGE
        EquipoEscritorioDTO dto = TestDataFactory.crearEquipoEscritorioValido();
        
        Modelo modeloMock = new Modelo();
        modeloMock.setId(1L);
        when(daoModelo.buscarPorId(1L)).thenReturn(modeloMock);

        Sucursal sucursalMock = new Sucursal();
        sucursalMock.setIdSucursal(1L);
        when(daoSucursal.buscarPorId(1L)).thenReturn(sucursalMock);

        when(daoEscritorio.guardar(any(EquipoDeEscritorio.class)))
            .thenAnswer(invocation -> {
                EquipoDeEscritorio e = invocation.getArgument(0);
                e.setId(100L);
                return e;
            });

        // ACT
        EquipoEscritorioDTO resultado = fachadaEquipos.guardarEscritorio(dto);

        // ASSERT
        assertAll("Verificar resultado del guardado",
            () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
            () -> assertEquals(100L, resultado.getIdEquipo(), "ID asignado incorrecto"),
            () -> assertEquals(EstadoEquipo.EN_STOCK.name(), resultado.getEstado(), "Estado inicial debe ser EN_STOCK"),
            () -> assertEquals(12345, resultado.getGry(), "GRY debe coincidir"),
            () -> assertEquals("PC-Admin", resultado.getNombreEquipo(), "Nombre de equipo debe coincidir")
        );
        
        verify(daoEscritorio, times(1)).guardar(any(EquipoDeEscritorio.class));
    }

    @Test
    void EQ002_guardarEscritorio_GRYDuplicado_LanzaExcepcion() {
        printTestInfo("EQ-002: Guardar equipo con GRY duplicado");
        
        // ARRANGE
        EquipoEscritorioDTO dto = TestDataFactory.crearEquipoEscritorioValido();
        
        EquipoDeComputo equipoExistente = new EquipoDeEscritorio();
        equipoExistente.setId(200L);
        equipoExistente.setGry(12345);
        
        when(daoEquipoGeneral.buscarPorGry(12345)).thenReturn(equipoExistente);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaEquipos.guardarEscritorio(dto),
            "Debe lanzar excepción por GRY duplicado"
        );
        
        assertTrue(exception.getMessage().contains("Ya existe un equipo con el GRY"),
            "Mensaje de error incorrecto: " + exception.getMessage());
        
        verify(daoEscritorio, never()).guardar(any(EquipoDeEscritorio.class));
    }

    @Test
    void EQ003_guardarMovil_DatosValidos_RetornaDTOConID() throws Exception {
        printTestInfo("EQ-003: Guardar equipo móvil con datos válidos");
        
        // ARRANGE
        MovilDTO dto = TestDataFactory.crearEquipoMovilValido();
        
        Modelo modeloMock = new Modelo();
        modeloMock.setId(2L);
        when(daoModelo.buscarPorId(2L)).thenReturn(modeloMock);

        Sucursal sucursalMock = new Sucursal();
        sucursalMock.setIdSucursal(1L);
        when(daoSucursal.buscarPorId(1L)).thenReturn(sucursalMock);

        when(daoMovil.guardar(any(Movil.class)))
            .thenAnswer(invocation -> {
                Movil m = invocation.getArgument(0);
                m.setId(200L);
                return m;
            });

        // ACT
        MovilDTO resultado = fachadaEquipos.guardarMovil(dto);

        // ASSERT
        assertAll("Verificar resultado del guardado",
            () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
            () -> assertEquals(200L, resultado.getIdEquipo(), "ID asignado incorrecto"),
            () -> assertEquals("5551234567", resultado.getNumCelular(), "Número de celular incorrecto"),
            () -> assertTrue(resultado.getCargador(), "Cargador debe ser true"),
            () -> assertTrue(resultado.getFunda(), "Funda debe ser true"),
            () -> assertFalse(resultado.getManosLibres(), "Manos libres debe ser false")
        );
        
        verify(daoMovil, times(1)).guardar(any(Movil.class));
    }

    @Test
    void EQ004_guardarOtro_DatosValidos_RetornaDTOConID() throws Exception {
        printTestInfo("EQ-004: Guardar otro equipo con datos válidos");
        
        // ARRANGE
        OtroEquipoDTO dto = TestDataFactory.crearOtroEquipoValido();
        
        Modelo modeloMock = new Modelo();
        modeloMock.setId(3L);
        when(daoModelo.buscarPorId(3L)).thenReturn(modeloMock);

        Sucursal sucursalMock = new Sucursal();
        sucursalMock.setIdSucursal(1L);
        when(daoSucursal.buscarPorId(1L)).thenReturn(sucursalMock);

        when(daoOtro.guardar(any(OtroEquipo.class)))
            .thenAnswer(invocation -> {
                OtroEquipo o = invocation.getArgument(0);
                o.setId(300L);
                return o;
            });

        // ACT
        OtroEquipoDTO resultado = fachadaEquipos.guardarOtro(dto);

        // ASSERT
        assertAll("Verificar resultado del guardado",
            () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
            () -> assertEquals(300L, resultado.getIdEquipo(), "ID asignado incorrecto"),
            () -> assertEquals("Número de Serie", resultado.getTituloCampoExtra(), "Título campo extra incorrecto"),
            () -> assertEquals("SN123456", resultado.getContenidoCampoExtra(), "Contenido campo extra incorrecto")
        );
        
        verify(daoOtro, times(1)).guardar(any(OtroEquipo.class));
    }

    @Test
    void EQ005_guardarEquipo_SinModelo_LanzaExcepcion() {
        printTestInfo("EQ-005: Guardar equipo sin modelo asociado");
        
        // ARRANGE
        EquipoEscritorioDTO dto = TestDataFactory.crearEquipoEscritorioValido();
        dto.setIdModelo(null);

        // ACT & ASSERT
        assertThrows(ReglaNegocioException.class, 
            () -> fachadaEquipos.guardarEscritorio(dto),
            "Debe lanzar excepción por falta de modelo"
        );
        
        verify(daoEscritorio, never()).guardar(any(EquipoDeEscritorio.class));
    }


    @Test
    void EQ007_buscarPorGry_Existente_RetornaEquipo() {
        printTestInfo("EQ-007: Buscar equipo por GRY existente");
        
        // ARRANGE
        Integer gry = 12345;
        EquipoDeEscritorio equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(1L);
        equipoMock.setGry(gry);
        equipoMock.setEstado(EstadoEquipo.EN_STOCK);

        when(daoEquipoGeneral.buscarPorGry(gry)).thenReturn(equipoMock);

        // ACT
        EquipoBaseDTO resultado = fachadaEquipos.buscarPorGry(gry);

        // ASSERT
        assertAll("Verificar resultado de búsqueda",
            () -> assertNotNull(resultado, "El resultado no debe ser nulo"),
            () -> assertEquals(gry, resultado.getGry(), "GRY debe coincidir")
        );
        
        verify(daoEquipoGeneral, times(1)).buscarPorGry(gry);
    }

    @Test
    void EQ008_buscarPorGry_Inexistente_LanzaExcepcion() {
        printTestInfo("EQ-008: Buscar equipo por GRY inexistente");
        
        // ARRANGE
        Integer gry = 99999;
        when(daoEquipoGeneral.buscarPorGry(gry)).thenReturn(null);

        // ACT & ASSERT
        assertThrows(RecursoNoEncontradoException.class, 
            () -> fachadaEquipos.buscarPorGry(gry),
            "Debe lanzar excepción por GRY no encontrado"
        );
    }

    @Test
    void EQ009_buscarConFiltros_MultiplesParametros_RetornaLista() {
        printTestInfo("EQ-009: Buscar equipos con múltiples filtros");
        
        // ARRANGE
        String texto = "Dell";
        TipoEquipo tipo = TipoEquipo.DESKTOP;
        CondicionFisica condicion = CondicionFisica.BUENO;
        EstadoEquipo estado = EstadoEquipo.EN_STOCK;

        List<EquipoDeComputo> equiposMock = Arrays.asList(
            new EquipoDeEscritorio(),
            new EquipoDeEscritorio()
        );

        when(daoEquipoGeneral.buscarConFiltros(eq(texto), eq(tipo), eq(condicion), eq(estado)))
            .thenReturn(equiposMock);

        // ACT
        List<EquipoBaseDTO> resultados = fachadaEquipos.buscarConFiltros(texto, tipo, condicion, estado);

        // ASSERT
        assertNotNull(resultados, "La lista no debe ser nula");
        assertEquals(2, resultados.size(), "Debe retornar 2 equipos");
        
        verify(daoEquipoGeneral, times(1))
            .buscarConFiltros(texto, tipo, condicion, estado);
    }

    @Test
    void EQ010_eliminarEquipo_NoAsignado_Exitoso() {
        printTestInfo("EQ-010: Eliminar equipo no asignado");
        
        // ARRANGE
        Long id = 1L;
        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(id);
        equipoMock.setEstado(EstadoEquipo.EN_STOCK);

        when(daoEquipoGeneral.buscarPorId(id)).thenReturn(equipoMock);
        doNothing().when(daoEquipoGeneral).eliminar(id);

        // ACT
        fachadaEquipos.eliminarEquipo(id);

        // ASSERT
        verify(daoEquipoGeneral, times(1)).eliminar(id);
    }

    @Test
    void EQ011_eliminarEquipo_Asignado_LanzaExcepcion() {
        printTestInfo("EQ-011: Eliminar equipo asignado");
        
        // ARRANGE
        Long id = 2L;
        EquipoDeComputo equipoMock = new EquipoDeEscritorio();
        equipoMock.setId(id);
        equipoMock.setEstado(EstadoEquipo.ASIGNADO);

        when(daoEquipoGeneral.buscarPorId(id)).thenReturn(equipoMock);

        // ACT & ASSERT
        ReglaNegocioException exception = assertThrows(ReglaNegocioException.class, 
            () -> fachadaEquipos.eliminarEquipo(id),
            "Debe lanzar excepción por equipo asignado"
        );
        
        assertTrue(exception.getMessage().contains("No se puede eliminar un equipo asignado"),
            "Mensaje de error incorrecto");
        
        verify(daoEquipoGeneral, never()).eliminar(anyLong());
    }
}