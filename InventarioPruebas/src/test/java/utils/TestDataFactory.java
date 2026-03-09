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
}