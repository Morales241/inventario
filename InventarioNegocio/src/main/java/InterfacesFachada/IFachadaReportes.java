/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package InterfacesFachada;

import interfacesServicios.*;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author jesusmorales
 */
public interface IFachadaReportes {

    public JasperPrint generarReporte(String rutaReporte, Map<String, Object> parametros) throws JRException;

    public JasperPrint generarReporteDesdeJrxml(String rutaJrxml, Map<String, Object> parametros) throws JRException;

    public JasperPrint generarReporteEquipos(String titulo, String usuario) throws JRException;

    public JasperPrint generarReporteEquiposPorSucursal(Long idSucursal, String nombreSucursal) throws JRException;

    public JasperPrint generarReporteAsignacionesUsuario(Long idUsuario, String nombreUsuario) throws JRException;

    public JasperPrint generarReporteInventarioGeneral() throws JRException;
}
