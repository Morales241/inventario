package Servicios;

import conexion.Conexion;
import interfacesServicios.IServicioReportes;
import jakarta.persistence.EntityManager;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Servicio para la generación de reportes con JasperReports 7.0.6
 */
public class ServicioReportes implements IServicioReportes{
    
    private static ServicioReportes instancia;
    private EntityManager em;
    
    private ServicioReportes() {}
    
    public static ServicioReportes getInstancia() {
        if (instancia == null) {
            instancia = new ServicioReportes();
        }
        return instancia;
    }
    
    /**
     * Obtiene una conexión JDBC desde el EntityManager
     */
    private Connection getConexion() throws Exception {
        if (em == null || !em.isOpen()) {
            em = Conexion.getEntityManager();
        }
        return em.unwrap(Connection.class);
    }
    
    /**
     * Cierra el EntityManager si fue abierto por este servicio
     */
    private void cerrarConexion() {
        if (em != null && em.isOpen()) {
            em.close();
            em = null;
        }
    }
    
    /**
     * Genera un reporte a partir de un archivo .jasper compilado
     * @param rutaReporte Ruta del recurso (ej: "/reportes/reporte_equipos.jasper")
     * @param parametros Mapa de parámetros para el reporte
     * @return JasperPrint listo para visualizar/exportar
     * @throws JRException si hay error en la generación
     */
    @Override
    public JasperPrint generarReporte(String rutaReporte, Map<String, Object> parametros) throws JRException {
        Connection connection = null;
        try {
            connection = getConexion();
            
            // Cargar el reporte compilado
            InputStream reportStream = getClass().getResourceAsStream(rutaReporte);
            if (reportStream == null) {
                throw new JRException("No se encontró el archivo de reporte: " + rutaReporte);
            }
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            
            // Llenar el reporte con datos
            return JasperFillManager.fillReport(jasperReport, parametros, connection);
            
        } catch (Exception e) {
            throw new JRException("Error al generar el reporte: " + e.getMessage(), e);
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Genera un reporte compilando primero un archivo .jrxml
     * @param rutaJrxml Ruta del recurso (ej: "/reportes/reporte_equipos.jrxml")
     * @param parametros Mapa de parámetros para el reporte
     * @return JasperPrint listo para visualizar/exportar
     * @throws JRException si hay error en la compilación o generación
     */
    @Override
    public JasperPrint generarReporteDesdeJrxml(String rutaJrxml, Map<String, Object> parametros) throws JRException {
        Connection connection = null;
        try {
            connection = getConexion();
            
            // Cargar y compilar el JRXML
            InputStream jrxmlStream = getClass().getResourceAsStream(rutaJrxml);
            if (jrxmlStream == null) {
                throw new JRException("No se encontró el archivo: " + rutaJrxml);
            }
            
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            
            // Llenar el reporte
            return JasperFillManager.fillReport(jasperReport, parametros, connection);
            
        } catch (Exception e) {
            throw new JRException("Error al generar el reporte desde JRXML: " + e.getMessage(), e);
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Genera reporte de equipos
     * @param titulo
     * @param usuario
     * @return 
     * @throws net.sf.jasperreports.engine.JRException
     */
    @Override
    public JasperPrint generarReporteEquipos(String titulo, String usuario) throws JRException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", titulo != null ? titulo : "Reporte de Equipos");
        parametros.put("USUARIO", usuario != null ? usuario : "Sistema");
        parametros.put("FECHA", new java.util.Date());
        
        return generarReporte("/reportes/reporte_equipos.jasper", parametros);
    }
    
    /**
     * Genera reporte de equipos por sucursal
     * @param idSucursal
     * @param nombreSucursal
     * @return 
     * @throws net.sf.jasperreports.engine.JRException
     */
    @Override
    public JasperPrint generarReporteEquiposPorSucursal(Long idSucursal, String nombreSucursal) throws JRException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ID_SUCURSAL", idSucursal);
        parametros.put("NOMBRE_SUCURSAL", nombreSucursal);
        parametros.put("FECHA", new java.util.Date());
        
        return generarReporte("/reportes/reporte_equipos_sucursal.jasper", parametros);
    }
    
    /**
     * Genera reporte de asignaciones por usuario
     * @param idUsuario
     * @param nombreUsuario
     * @return 
     * @throws net.sf.jasperreports.engine.JRException
     */
    @Override
    public JasperPrint generarReporteAsignacionesUsuario(Long idUsuario, String nombreUsuario) throws JRException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ID_USUARIO", idUsuario);
        parametros.put("NOMBRE_USUARIO", nombreUsuario);
        parametros.put("FECHA", new java.util.Date());
        
        return generarReporte("/reportes/reporte_asignaciones_usuario.jasper", parametros);
    }
    
    /**
     * Genera reporte de inventario general
     * @return 
     * @throws net.sf.jasperreports.engine.JRException 
     */
    @Override
    public JasperPrint generarReporteInventarioGeneral() throws JRException {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", "Inventario General de Equipos");
        parametros.put("FECHA", new java.util.Date());
        
        return generarReporte("/reportes/reporte_inventario_general.jasper", parametros);
    }
}