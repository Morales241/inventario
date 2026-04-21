package utils;

import Dtos.*;
import Enums.*;
import java.time.LocalDate;

public class TestDataFactory {
    
    public static EquipoEscritorioDTO crearEquipoEscritorioValido() {
        EquipoEscritorioDTO dto = new EquipoEscritorioDTO();
        dto.setGry(12345);
        dto.setIdentificador("EQ-12345");
        dto.setCondicion(CondicionFisica.BUENO.name());
        dto.setTipo(TipoEquipo.DESKTOP.name());
        dto.setIdSucursal(1L);
        dto.setIdModelo(1L);
        dto.setNombreEquipo("PC-Admin");
        dto.setUserRed("admin");
        dto.setFactura("FAC-001");
        dto.setObservaciones("Equipo de pruebas");
        dto.setFechaCompra(LocalDate.now());
        dto.setMochila(true);
        dto.setMouse(true);
        dto.setSisOpertativo("Windows 10 Pro");
        return dto;
    }
    
    public static MovilDTO crearEquipoMovilValido() {
        MovilDTO dto = new MovilDTO();
        dto.setGry(54321);
        dto.setIdentificador("MOV-54321");
        dto.setCondicion(CondicionFisica.NUEVO.name());
        dto.setTipo(TipoEquipo.MOVIL.name());
        dto.setIdSucursal(1L);
        dto.setIdModelo(2L);
        dto.setNumCelular("5551234567");
        dto.setCorreoCuenta("usuario@test.com");
        dto.setContrasenaCuenta("password123");
        dto.setCargador(true);
        dto.setFunda(true);
        dto.setManosLibres(false);
        return dto;
    }
    
    public static OtroEquipoDTO crearOtroEquipoValido() {
        OtroEquipoDTO dto = new OtroEquipoDTO();
        dto.setGry(98765);
        dto.setIdentificador("OTRO-98765");
        dto.setCondicion(CondicionFisica.REGULAR.name());
        dto.setTipo(TipoEquipo.IMPRESORA.name());
        dto.setIdSucursal(1L);
        dto.setIdModelo(3L);
        dto.setTituloCampoExtra("Número de Serie");
        dto.setContenidoCampoExtra("SN123456");
        return dto;
    }
    
    public static UsuarioDTO crearUsuarioValido() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Juan Pérez");
        dto.setNoNomina("NOM-001");
        dto.setIdPuesto(1L);
        dto.setActivo(true);
        return dto;
    }
    
    public static CuentaSistemaDTO crearCuentaSistemaValida() {
        CuentaSistemaDTO dto = new CuentaSistemaDTO();
        dto.setUsername("admin");
        dto.setPassword("1234");
        dto.setRol(RolCuenta.ADMIN.name());
        return dto;
    }
    
    public static ModeloDTO crearModeloValido() {
        ModeloDTO dto = new ModeloDTO();
        dto.setNombre("Optiplex 7090");
        dto.setMarca("Dell");
        dto.setMemoriaRam(16);
        dto.setAlmacenamiento(512);
        dto.setProcesador("Intel i7 11th Gen");
        return dto;
    }
    
    // ========== CASOS DE VALIDACIÓN - ESPACIOS EN BLANCO ==========
    
    public static EquipoEscritorioDTO crearEscritorioConNombreBlanco() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setNombreEquipo("   ");
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioConNombreNulo() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setNombreEquipo(null);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioConIdentificadorBlanco() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setIdentificador("   ");
        return dto;
    }
    
    public static MovilDTO crearMovilConNumCelularBlanco() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setNumCelular("   ");
        return dto;
    }
    
    public static MovilDTO crearMovilConNumCelularNulo() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setNumCelular(null);
        return dto;
    }
    
    public static MovilDTO crearMovilConCorreoBlanco() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setCorreoCuenta("   ");
        return dto;
    }
    
    public static MovilDTO crearMovilConCorreoNulo() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setCorreoCuenta(null);
        return dto;
    }
    
    public static MovilDTO crearMovilConContraseniaBlanca() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setContrasenaCuenta("   ");
        return dto;
    }
    
    public static OtroEquipoDTO crearOtroEquipoConTituloBlanco() {
        OtroEquipoDTO dto = crearOtroEquipoValido();
        dto.setTituloCampoExtra("   ");
        return dto;
    }
    
    // ========== CASOS DE VALIDACIÓN - VALORES NULOS ==========
    
    public static EquipoEscritorioDTO crearEscritorioSinGry() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setGry(null);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioConGryNegativo() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setGry(-1);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioConGryCero() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setGry(0);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioSinSucursal() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setIdSucursal(null);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioSinModelo() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setIdModelo(null);
        return dto;
    }
    
    public static MovilDTO crearMovilSinGry() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setGry(null);
        return dto;
    }
    
    public static MovilDTO crearMovilConGryNegativo() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setGry(-5);
        return dto;
    }
    
    public static OtroEquipoDTO crearOtroEquipoSinGry() {
        OtroEquipoDTO dto = crearOtroEquipoValido();
        dto.setGry(null);
        return dto;
    }
    
    // ========== CASOS DE VALIDACIÓN - EDICIÓN ==========
    
    public static EquipoEscritorioDTO crearEscritorioParaEditar() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setIdEquipo(100L); // ID existente
        dto.setVersion(1L);
        return dto;
    }
    
    public static EquipoEscritorioDTO crearEscritorioEditadoConNombreNuevo() {
        EquipoEscritorioDTO dto = crearEscritorioParaEditar();
        dto.setNombreEquipo("PC-Gerencia-Actualizado");
        dto.setUserRed("gerencia_nuevo");
        return dto;
    }
    
    public static MovilDTO crearMovilParaEditar() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setIdEquipo(200L); // ID existente
        dto.setVersion(1L);
        return dto;
    }
    
    public static MovilDTO crearMovilEditadoConNumNuevo() {
        MovilDTO dto = crearMovilParaEditar();
        dto.setNumCelular("5559876543");
        dto.setCorreoCuenta("nuevo.email@test.com");
        return dto;
    }
    
    public static OtroEquipoDTO crearOtroEquipoParaEditar() {
        OtroEquipoDTO dto = crearOtroEquipoValido();
        dto.setIdEquipo(300L); // ID existente
        dto.setVersion(1L);
        return dto;
    }
    
    // ========== CASOS DE VALIDACIÓN - MÚLTIPLES EQUIPOS ==========
    
    public static EquipoEscritorioDTO crearSegundoEscritorio() {
        EquipoEscritorioDTO dto = new EquipoEscritorioDTO();
        dto.setGry(11111);
        dto.setIdentificador("EQ-11111");
        dto.setCondicion(CondicionFisica.BUENO.name());
        dto.setTipo(TipoEquipo.LAPTOP.name());
        dto.setIdSucursal(2L);
        dto.setIdModelo(1L);
        dto.setNombreEquipo("Laptop-Vendedor");
        dto.setUserRed("vendedor01");
        dto.setFactura("FAC-002");
        dto.setObservaciones("Laptop de vendedor");
        dto.setFechaCompra(LocalDate.now().minusDays(30));
        dto.setMochila(false);
        dto.setMouse(false);
        dto.setSisOpertativo("Windows 11");
        return dto;
    }
    
    public static MovilDTO crearSegundoMovil() {
        MovilDTO dto = new MovilDTO();
        dto.setGry(22222);
        dto.setIdentificador("MOV-22222");
        dto.setCondicion(CondicionFisica.NUEVO.name());
        dto.setTipo(TipoEquipo.MOVIL.name());
        dto.setIdSucursal(2L);
        dto.setIdModelo(2L);
        dto.setNumCelular("5554445555");
        dto.setCorreoCuenta("gerencia@test.com");
        dto.setContrasenaCuenta("pass456");
        dto.setCargador(true);
        dto.setFunda(false);
        dto.setManosLibres(true);
        return dto;
    }
    
    public static OtroEquipoDTO crearSegundoOtroEquipo() {
        OtroEquipoDTO dto = new OtroEquipoDTO();
        dto.setGry(33333);
        dto.setIdentificador("OTRO-33333");
        dto.setCondicion(CondicionFisica.MALO.name());
        dto.setTipo(TipoEquipo.MONITOR.name());
        dto.setIdSucursal(2L);
        dto.setIdModelo(3L);
        dto.setTituloCampoExtra("Resolución");
        dto.setContenidoCampoExtra("1920x1080");
        return dto;
    }
    
    // ========== CASOS DE CAMBIO DE SUCURSAL ==========
    
    public static EquipoEscritorioDTO crearEscritorioConSucursalDiferente() {
        EquipoEscritorioDTO dto = crearEquipoEscritorioValido();
        dto.setIdSucursal(5L);
        return dto;
    }
    
    public static MovilDTO crearMovilConSucursalDiferente() {
        MovilDTO dto = crearEquipoMovilValido();
        dto.setIdSucursal(3L);
        return dto;
    }
}