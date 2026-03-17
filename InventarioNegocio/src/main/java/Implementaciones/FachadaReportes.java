package Implementaciones;

import InterfacesFachada.IFachadaReportes;
import Servicios.ServicioReportes;
import interfacesServicios.IServicioReportes;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author jesusmorales
 */
public class FachadaReportes implements IFachadaReportes{

    private IServicioReportes servicio;

    public FachadaReportes() {
        this.servicio = ServicioReportes.getInstancia();
    }
    
    @Override
    public JasperPrint generarReporte(String rutaReporte, Map<String, Object> parametros) throws JRException {
        return servicio.generarReporte(rutaReporte, parametros);
    }

    @Override
    public JasperPrint generarReporteDesdeJrxml(String rutaJrxml, Map<String, Object> parametros) throws JRException {
        return servicio.generarReporteDesdeJrxml(rutaJrxml, parametros);
    }

    @Override
    public JasperPrint generarReporteEquipos(String titulo, String usuario) throws JRException {
        return servicio.generarReporteEquipos(titulo, usuario);
    }

    @Override
    public JasperPrint generarReporteEquiposPorSucursal(Long idSucursal, String nombreSucursal) throws JRException {
        return servicio.generarReporteEquiposPorSucursal(idSucursal, nombreSucursal);
    }

    @Override
    public JasperPrint generarReporteAsignacionesUsuario(Long idUsuario, String nombreUsuario) throws JRException {
        return servicio.generarReporteAsignacionesUsuario(idUsuario, nombreUsuario);
    }

    @Override
    public JasperPrint generarReporteInventarioGeneral() throws JRException {
        return servicio.generarReporteInventarioGeneral();
    }
}
