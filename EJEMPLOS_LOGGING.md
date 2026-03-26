# Ejemplos de Uso - Sistema de Logging y Notificaciones

## 📚 Ejemplos Prácticos

### 1. Controlador con Manejo de Errores Completo

```java
import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.BaseController;
import fabricaFachadas.FabricaFachadas;
import InterfacesFachada.IFachadaEquipos;
import excepciones.ReglaNegocioException;
import excepciones.NegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiControlador implements BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(MiControlador.class);
    private final IFachadaEquipos fachada = FabricaFachadas.getFachadaEquipos();
    private MenuController dbc;
    
    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
    
    /**
     * Ejemplo: Operación que puede fallar de múltiples formas
     */
    public void operacionCompleja() {
        logger.debug("Iniciando operación compleja");
        
        try {
            // Obtener datos del formulario
            EquipoBaseDTO equipo = obtenerDatos();
            logger.debug("Datos obtenidos del formulario");
            
            // Validar
            validarEquipo(equipo);
            logger.debug("Equipo validado correctamente");
            
            // Guardar
            EquipoBaseDTO resultado = fachada.crear(equipo);
            logger.info("Equipo guardado: ID={}", resultado.getIdEquipo());
            
            // Mostrar éxito
            mostrarExito("Operación exitosa", 
                "El equipo fue guardado correctamente con ID: " + resultado.getIdEquipo());
            
            limpiarFormulario();
            
        } catch (ReglaNegocioException ex) {
            // Error de validación de negocio - mostrar advertencia
            logger.warn("Regla de negocio violada: {}", ex.getMessage());
            mostrarAdvertencia("Validación requerida", ex.getMessage());
            
        } catch (NegocioException ex) {
            // Error general de negocio
            logger.error("Error de negocio: {}", ex.getMessage(), ex);
            mostrarError("Error", "Ocurrió un error: " + ex.getMessage(), ex);
            
        } catch (Exception ex) {
            // Error inesperado
            logger.error("Error inesperado en operationCompleja", ex);
            mostrarError("Error inesperado", 
                "Ocurrió un error inesperado. Por favor, contacte al administrador.", ex);
        }
    }
}
```

### 2. Servicio con Logging Detallado

```java
import Utilidades.LoggerUtil;
import excepciones.ReglaNegocioException;
import excepciones.NegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioEquipos implements IServicioEquipos {
    
    private static final Logger logger = LoggerFactory.getLogger(ServicioEquipos.class);
    
    /**
     * Guardar equipo con validaciones y logging
     */
    public EquipoBaseDTO guardarEquipo(EquipoBaseDTO dto) throws ReglaNegocioException {
        logger.debug("Iniciando guardado de equipo: {}", dto.getIdEquipo());
        
        try {
            // Validación 1: Datos requeridos
            if (dto.getModelo() == null) {
                LoggerUtil.registrarAdvertencia(logger, "guardarEquipo", 
                    "Modelo no especificado");
                throw new ReglaNegocioException("El modelo del equipo es obligatorio");
            }
            
            // Validación 2: Campos específicos por tipo
            if (dto instanceof EquipoEscritorioDTO escritorio) {
                validarEscritorio(escritorio);
            }
            
            LoggerUtil.registrarInfo(logger, "guardarEquipo", 
                "Validaciones completadas");
            
            // Guardar en BD
            EquipoBaseDTO guardado = daoEquipo.crear(dto);
            
            LoggerUtil.registrarExito(logger, "guardarEquipo", 
                "Equipo guardado - ID: " + guardado.getIdEquipo());
            
            return guardado;
            
        } catch (ReglaNegocioException ex) {
            // Re-lanzar excepciones de negocio
            logger.warn("Validación fallida en guardarEquipo: {}", ex.getMessage());
            throw ex;
            
        } catch (Exception ex) {
            // Convertir excepciones no controladas
            LoggerUtil.registrarError(logger, "guardarEquipo", 
                "Error al guardar equipo: " + dto, ex);
            throw new NegocioException("Error al guardar el equipo", ex);
        }
    }
    
    private void validarEscritorio(EquipoEscritorioDTO dto) 
            throws ReglaNegocioException {
        logger.debug("Validando equipo de escritorio");
        
        if (dto.getProcesador() == null || dto.getProcesador().isEmpty()) {
            LoggerUtil.registrarAdvertencia(logger, "validarEscritorio", 
                "Procesador no especificado");
            throw new ReglaNegocioException("El procesador del equipo es obligatorio");
        }
        
        LoggerUtil.registrarDebug(logger, "validarEscritorio", 
            "Validación de escritorio completada");
    }
}
```

### 3. Uso de AlertService Directamente

```java
import com.mycompany.inventariofrontfx.util.AlertService;

// En cualquier lugar del código de la UI
public void ejemplo() {
    try {
        // ... código que puede fallar
        
    } catch (Exception ex) {
        // Mostrar error con detalles técnicos
        AlertService.mostrarError(
            "Error al procesar",
            "Ocurrió un error al procesar los datos",
            ex
        );
    }
}

// Mostrar información
AlertService.mostrarInfo("Información", "La operación se completó");

// Mostrar advertencia sin excepción
AlertService.mostrarAdvertencia("Advertencia", "Esto puede afectar otros datos");

// Mostrar éxito
AlertService.mostrarExito("Éxito", "Los cambios fueron guardados");
```

### 4. Operación Asincrónica con Manejo de Errores

```java
import javafx.concurrent.Task;
import javafx.application.Platform;

@FXML
private void cargarDatos() {
    logger.debug("Iniciando carga de datos");
    
    Task<List<EquipoBaseDTO>> task = new Task<>() {
        @Override
        protected List<EquipoBaseDTO> call() throws Exception {
            logger.debug("Task: Buscando equipos en BD");
            List<EquipoBaseDTO> equipos = fachada.obtenerTodos();
            logger.info("Task: {} equipos encontrados", equipos.size());
            return equipos;
        }
    };
    
    // Éxito
    task.setOnSucceeded(event -> {
        logger.info("Carga de datos completada");
        List<EquipoBaseDTO> equipos = task.getValue();
        mostrarExito("Éxito", "Se cargaron " + equipos.size() + " equipos");
        cargarTabla(equipos);
    });
    
    // Error
    task.setOnFailed(event -> {
        Throwable ex = task.getException();
        logger.error("Error en carga de datos", ex);
        
        if (ex instanceof NegocioException) {
            mostrarAdvertencia("Error", ex.getMessage());
        } else {
            mostrarError("Error inesperado", 
                "Ocurrió un error al cargar los datos", 
                (Exception) ex);
        }
    });
    
    // Ejecutar en thread separado
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
}
```

### 5. Búsqueda con Filtraje y Manejo de Errores

```java
@FXML
private void buscar() {
    String filtro = txtFiltro.getText();
    logger.debug("Iniciando búsqueda con filtro: '{}'", filtro);
    
    if (filtro == null || filtro.trim().isEmpty()) {
        logger.warn("Búsqueda sin parámetros");
        mostrarAdvertencia("Búsqueda vacía", "Por favor ingrese un criterio de búsqueda");
        return;
    }
    
    try {
        logger.debug("Ejecutando búsqueda");
        List<EquipoBaseDTO> resultados = fachada.buscar(filtro);
        
        LoggerUtil.registrarExito(logger, "buscar", 
            "Búsqueda completada: " + resultados.size() + " resultados");
        
        if (resultados.isEmpty()) {
            mostrarInfo("Sin resultados", 
                "No se encontraron equipos que coincidan con '" + filtro + "'");
        } else {
            mostrarInfo("Búsqueda completada", 
                "Se encontraron " + resultados.size() + " equipos");
            cargarTabla(resultados);
        }
        
    } catch (Exception ex) {
        LoggerUtil.registrarError(logger, "buscar", 
            "Error al buscar: " + filtro, ex);
        mostrarError("Error en búsqueda", 
            "Ocurrió un error al buscar", ex);
    }
}
```

### 6. Transacción Multi-Paso con Logging

```java
public void realizarTransaccion(List<EquipoBaseDTO> equipos) {
    logger.debug("Iniciando transacción con {} equipos", equipos.size());
    
    int procesados = 0;
    int errores = 0;
    StringBuilder erroresLog = new StringBuilder();
    
    try {
        for (EquipoBaseDTO equipo : equipos) {
            try {
                logger.debug("Procesando equipo: {}", equipo.getIdEquipo());
                fachada.crear(equipo);
                procesados++;
                logger.debug("Equipo procesado correctamente");
                
            } catch (ReglaNegocioException ex) {
                errores++;
                erroresLog.append("- ").append(equipo.getIdEquipo())
                    .append(": ").append(ex.getMessage()).append("\n");
                logger.warn("Error en equipo {}: {}", 
                    equipo.getIdEquipo(), ex.getMessage());
            }
        }
        
        // Resumen
        LoggerUtil.registrarExito(logger, "realizarTransaccion",
            "Completada: " + procesados + " éxito, " + errores + " errores");
        
        if (errores == 0) {
            mostrarExito("Transacción completa", 
                "Se procesaron exitosamente " + procesados + " equipos");
        } else {
            mostrarAdvertencia("Transacción parcial", 
                procesados + " equipos guardados.\n" + errores + " equipos con errores.\n\n" + 
                erroresLog.toString());
        }
        
    } catch (Exception ex) {
        LoggerUtil.registrarError(logger, "realizarTransaccion", 
            "Error general en transacción", ex);
        mostrarError("Error en transacción", 
            "Error al procesar: se procesaron " + procesados + " items antes del error", 
            ex);
    }
}
```

---

## 🎯 Patrones Recomendados

### Patrón 1: Try-Catch Simple
```java
try {
    operacion();
    mostrarExito("Título", "Mensaje");
} catch (ReglaNegocioException ex) {
    mostrarAdvertencia("Validación", ex.getMessage());
} catch (Exception ex) {
    mostrarError("Error", "Mensaje", ex);
}
```

### Patrón 2: Logging Detallado
```java
logger.debug("Iniciando operación");
try {
    // ... código
    LoggerUtil.registrarExito(logger, "operacion", "detalles");
    mostrarExito("Título", "Mensaje");
} catch (Exception ex) {
    LoggerUtil.registrarError(logger, "operacion", "detalles", ex);
    mostrarError("Título", "Mensaje", ex);
}
```

### Patrón 3: Validación Temprana
```java
if (!validar()) {
    logger.warn("Validación fallida");
    mostrarAdvertencia("Validación", "Corrija los errores");
    return;
}
```

### Patrón 4: Operación con Recuperación
```java
for (Item item : items) {
    try {
        procesar(item);
    } catch (Exception ex) {
        logger.warn("Error procesando {}: {}", item, ex.getMessage());
        // Continuar con el siguiente
    }
}
// Mostrar resumen
```

---

## 📊 Visuralización esperada

### Logs en Terminal
```
12:34:56.789 [INFO] com.mycompany.inventariofrontfx.menu.MenuController - Iniciando operación
12:34:57.234 [WARN] excepciones.ReglaNegocioException - Regla de negocio violada: El nombre es obligatorio
12:34:58.567 [ERROR] com.mycompany.inventariofrontfx.UsuariosController - Error inesperado, java.lang.NullPointerException
```

### Alertas en Pantalla
- **Error**: Diálogo rojo con mensaje y botón "Expandir detalles"
- **Advertencia**: Diálogo amarillo con mensaje
- **Información**: Diálogo azul con mensaje
- **Éxito**: Diálogo verde con mensaje

---

## 🔍 Depuración

### Ver todos los logs
```bash
tail -f logs/inventario.log
```

### Ver solo errores
```bash
tail -f logs/inventario-error.log
```

### Ver logs de depuración
```bash
tail -f logs/inventario-debug.log
```
