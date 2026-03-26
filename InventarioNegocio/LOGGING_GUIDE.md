# Guía de Logging del Proyecto

## 📋 Descripción

Se ha implementado un sistema de logging **completo y bidireccional**:
- **Backend**: Logging a consola y archivos con SLF4J + Logback
- **Frontend**: Notificaciones visuales en pantalla con AlertService
- **Manejo de excepciones**: Manejador global que captura errores no controlados

Este sistema permite capturar y visualizar errores, advertencias e información de depuración de manera organizada.

---

## 🚀 Características

### ✅ Backend (InventarioNegocio)
- **Logging en Terminal**: Los logs se muestran en tiempo real en la consola
- **Logging en Archivos**: Los logs se guardan en la carpeta `logs/` del proyecto
- **Niveles de Log**: DEBUG, INFO, WARN, ERROR
- **Archivos de Error Separados**: Los errores y advertencias se guardan en archivos específicos
- **Logs de Debug**: Archivo separado para información de depuración (`inventario-debug.log`)
- **Rotación Automática**: Los archivos se rotan automáticamente cuando alcanzan 10MB
- **Retención de 30 días**: Los logs más antiguos se eliminan automáticamente

### ✅ Frontend (InventarioFrontFx)
- **AlertService**: Servicio centralizado para mostrar alertas al usuario
- **GlobalExceptionHandler**: Captura excepciones no manejadas
- **Notificaciones visuales**: Diálogos amigables con detalles técnicos expandibles
- **Clasificación inteligente**: Diferencia entre errores, advertencias e información

### 📍 Ubicaciones de Logs

```
logs/
├── inventario.log              # Todos los logs (INFO y superior)
├── inventario-error.log        # Solo WARN y ERROR
├── inventario-debug.log        # Solo DEBUG (desarrollo)
└── inventario-*.log            # Logs archivados por fecha
```

---

## 📝 Uso del Logging

### 1. **Agregar Logger a una clase**

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {
    private static final Logger logger = LoggerFactory.getLogger(MiClase.class);
    // ...
}
```

### 2. **Niveles de Log Disponibles**

```java
// DEBUG - Información detallada para depuración (desarrollo)
logger.debug("Buscando usuario con ID: {}", id);

// INFO - Información general del flujo
logger.info("Usuario guardado exitosamente");

// WARN - Advertencias (puede haber un problema)
logger.warn("Transacción reversada: {}", razon);

// ERROR - Errores (algo salió mal)
logger.error("Error al conectar a BD", e);
```

### 3. **Formatos de Log Recomendados**

```java
// Con un parámetro
logger.info("Iniciando operación: {}", nombreOperacion);

// Con múltiples parámetros
logger.info("Usuario {} creado en {} con estado: {}", nombre, fecha, estado);

// Con excepción
logger.error("Error durante guardado", exception);

// Con excepción y parámetros
logger.error("Error al procesar {}: {}", id, exception.getMessage(), exception);
```

### 4. **Usar LoggerUtil para operaciones**

```java
import Utilidades.LoggerUtil;

// Registrar éxito
LoggerUtil.registrarExito(logger, "guardarUsuario", "Usuario ID: " + id);

// Registrar error
LoggerUtil.registrarError(logger, "buscarUsuario", "Usuario no encontrado", exception);

// Registrar advertencia
LoggerUtil.registrarAdvertencia(logger, "validarEquipo", "Estado sospechoso");
```

---

## 🎨 Mostrando Errores en Pantalla

### Backend → Frontend

#### En Servicios (Backend)
```java
public class ServicioUsuario {
    private static final Logger logger = LoggerFactory.getLogger(ServicioUsuario.class);
    
    public void guardarUsuario(UsuarioDTO dto) throws ReglaNegocioException {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
            logger.warn("Intento de guardar usuario sin nombre");
            throw new ReglaNegocioException("El nombre del usuario es obligatorio");
        }
        // ... resto del código
    }
}
```

#### En Controladores (Frontend)
```java
public class UsuariosController implements BaseController {
    private final IServicioPersonas servicio = ...;
    
    @FXML
    private void guardarUsuario() {
        try {
            UsuarioDTO usuario = obtenerDatosFormulario();
            servicio.guardarUsuario(usuario);
            mostrarExito("Éxito", "Usuario guardado correctamente");
        } catch (ReglaNegocioException ex) {
            // Ya se registró en backend, se muestra automáticamente
            mostrarAdvertencia("Validación", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error", "Ocurrió un error al guardar", ex);
        }
    }
}
```

### Métodos Disponibles en BaseController

```java
// Error con detalles técnicos (expandible)
mostrarError("Título", "Mensaje para usuario", exception);

// Error simple
mostrarError("Título", "Mensaje para usuario");

// Advertencia
mostrarAdvertencia("Título", "Mensaje");

// Información
mostrarInfo("Título", "Mensaje");

// Éxito
mostrarExito("Título", "Mensaje");
```

---

## 🔧 Configuración (logback.xml)

El archivo `src/main/resources/logback.xml` controla:

```xml
<!-- Nivel de log (DEBUG, INFO, WARN, ERROR) -->
<root level="DEBUG">

<!-- Filtro para consola (solo INFO y superior) -->
<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>INFO</level>
</filter>

<!-- Rotación: máximo 10MB, máximo 30 días de retención -->
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <maxFileSize>10MB</maxFileSize>
    <maxHistory>30</maxHistory>
    <totalSizeCap>500MB</totalSizeCap>
</rollingPolicy>
```

### Loggers Específicos Para Desarrollo

```xml
<!-- Excepciones - muestra DEBUG -->
<logger name="excepciones" level="DEBUG">
    <appender-ref ref="CONSOLE_DEBUG"/>
    <appender-ref ref="FILE_DEBUG"/>
</logger>

<!-- Servicios - captura lógica de negocio -->
<logger name="Servicios" level="DEBUG"/>

<!-- Fachadas - captura integración entre capas -->
<logger name="Implementaciones" level="DEBUG"/>

<!-- Conexión y DAO - captura SQL y transacciones -->
<logger name="conexion" level="DEBUG"/>
<logger name="Dao" level="DEBUG"/>
```

---

## 🛠 Ejemplos Completos

### Ejemplo 1: Operación CRUD

```java
public class ServicioEquipos {
    private static final Logger logger = LoggerFactory.getLogger(ServicioEquipos.class);
    
    public EquipoBaseDTO crearEquipo(EquipoBaseDTO dto) throws ReglaNegocioException {
        logger.debug("Iniciando creación de equipo: {}", dto.getIdEquipo());
        
        try {
            validarEquipo(dto);
            LoggerUtil.registrarInfo(logger, "validarEquipo", "Equipo válido");
            
            EquipoBaseDTO equipo = mapperEquipos.mapearDTO(dto);
            daoEquipo.crear(equipo);
            
            LoggerUtil.registrarExito(logger, "crearEquipo", 
                "Equipo creado - ID: " + equipo.getIdEquipo());
            
            return equipo;
        } catch (Exception ex) {
            LoggerUtil.registrarError(logger, "crearEquipo", 
                "Fallo al crear equipo: " + dto, ex);
            throw new NegocioException("Error al crear equipo", ex);
        }
    }
    
    private void validarEquipo(EquipoBaseDTO dto) throws ReglaNegocioException {
        if (dto.getModelo() == null) {
            LoggerUtil.registrarAdvertencia(logger, "validarEquipo", 
                "Equipo sin modelo");
            throw new ReglaNegocioException("El modelo del equipo es obligatorio");
        }
    }
}
```

### Ejemplo 2: Manejo en Controller

```java
public class FormInventarioController implements ControllerInventario {
    private final IFachadaEquipos fachada = FabricaFachadas.getFachadaEquipos();
    
    @FXML
    private void guardarEquipo() {
        try {
            EquipoBaseDTO equipo = construirEquipo();
            fachada.crear(equipo);
            mostrarExito("Éxito", "Equipo guardado correctamente");
            limpiarFormulario();
        } catch (ReglaNegocioException ex) {
            // Advertencia - validación de negocio
            mostrarAdvertencia("Validación requerida", ex.getMessage());
        } catch (RecursoNoEncontradoException ex) {
            // Error - recurso no existe
            mostrarError("No encontrado", ex.getMessage(), ex);
        } catch (Exception ex) {
            // Error general
            mostrarError("Error inesperado", 
                "Ocurrió un error al guardar el equipo", ex);
        }
    }
}
```

---

## 📊 Comportamiento Expected

### Cuando ocurre un error:

1. **Backend (InventarioNegocio)**
   - Se registra en el archivo de log correspondiente
   - Se muestra en la consola (si es WARN/ERROR)
   - Se captura la excepción con stack trace completo

2. **Frontend (InventarioFrontFx)**
   - Se captura la excepción lanzada por el servicio
   - Se muestra un diálogo amigable al usuario
   - Se registra en los logs del frontend
   - Si es no capturada, GlobalExceptionHandler la maneja automáticamente

3. **Usuario**
   - Ve un diálogo con el error clasificado (ERROR, ADVERTENCIA, INFO)
   - Puede expandir para ver detalles técnicos si es necesario
   - Puede continuar trabajando después de cerrar el diálogo

---

## 🎯 Mejores Prácticas

1. **Siempre registra excepciones en el backend**
   ```java
   logger.error("Descripción del error", exception);
   ```

2. **Lanza excepciones específicas para que el frontend las atrape**
   ```java
   throw new ReglaNegocioException("Mensaje amigable para el usuario");
   ```

3. **En el frontend, maneja las excepciones visibles**
   ```java
   catch (ReglaNegocioException ex) {
       mostrarAdvertencia("Validación", ex.getMessage());
   }
   ```

4. **Usa niveles apropiados**
   - DEBUG: Info no esencial para desarrollo
   - INFO: Eventos importantes del flujo
   - WARN: Situaciones anómalas pero recuperables
   - ERROR: Situaciones que requieren intervención

5. **Agrupa logs por operación lógica**
   ```java
   logger.debug("Iniciando operación: {}", nombreOperacion);
   // ... código de la operación
   logger.info("Operación completada: {}", nombreOperacion);
   ```


Si necesitas cambiar el nivel de log global:

```xml
<!-- Cambiar DEBUG por INFO, WARN o ERROR según necesites -->
<root level="DEBUG">
```

---

## 📊 Ejemplo de Salida en Terminal

```
2026-03-25 14:30:45.123 [main] INFO  Servicios.ServicioGenerico - Iniciando guardar: Usuario
2026-03-25 14:30:45.234 [main] DEBUG excepciones.NegocioException - NegocioException: Usuario duplicado
2026-03-25 14:30:45.245 [main] ERROR Servicios.ServicioGenerico - Error al guardar Usuario: Usuario duplicado
```

---

## 🛠️ Clases ya con Logging Implementado

### ✅ Actualizadas
- `Servicios/ServicioBase.java` - Logging de transacciones y EntityManager
- `Servicios/ServicioGenerico.java` - Logging en CRUD (guardar, actualizar, eliminar, buscar)
- `excepciones/NegocioException.java` - Logging de excepciones de negocio
- `excepciones/ReglaNegocioException.java` - Logging de reglas violadas
- `excepciones/RecursoNoEncontradoException.java` - Logging de recursos no encontrados

### 📌 Próximas a Implementar (Opcional)
- `Implementaciones/FachadaEquipos.java`
- `Implementaciones/FachadaOrganizacion.java`
- `Implementaciones/FachadaPersonas.java`
- Otros servicios específicos si es necesario

---

## 🔍 Ver Logs en Tiempo Real

### Opción 1: Terminal del IDE
Los logs aparecerán automáticamente en la consola de VS Code/IDE.

### Opción 2: Archivo
```bash
# En Windows (PowerShell)
Get-Content -Path logs/inventario.log -Tail 50 -Wait

# En Linux/Mac (Terminal)
tail -f logs/inventario.log
```

### Opción 3: Solo Errores
```bash
# Windows (PowerShell)
Get-Content logs/inventario-error.log -Tail 30

# Linux/Mac (Terminal)
tail -30 logs/inventario-error.log
```

---

## ⚙️ Troubleshooting

### Los logs no aparecen en terminal
✅ Verifica que `src/main/resources/logback.xml` existe  
✅ Reconstruye el proyecto: `mvn clean compile`  
✅ Verifica que SLF4J está en el pom.xml

### Los archivos de log crecen demasiado
✅ La rotación está configurada automáticamente (maxHistory=30)  
✅ Puedes cambiar el tamaño máximo en `logback.xml` (maxFileSize)

### Quiero más logs detallados
✅ Cambia `<root level="DEBUG">` a `<root level="TRACE">` en logback.xml  
⚠️ Esto puede afectar el rendimiento

---

## 📚 Plantilla para Nuevas Clases

Copia esto en nuevas clases donde necesites logging:

```java
package miPaquete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {
    private static final Logger logger = LoggerFactory.getLogger(MiClase.class);
    
    public void miMetodo(String param) {
        logger.info("Iniciando miMetodo con param: {}", param);
        try {
            // tu código aquí
            logger.debug("Paso intermedio completado");
        } catch (Exception e) {
            logger.error("Error en miMetodo: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

---

## 📖 Referencias

- [SLF4J Documentation](https://www.slf4j.org/)
- [Logback Documentation](https://logback.qos.ch/)
- [Logback Configuration Guide](https://logback.qos.ch/manual/configuration.html)

---

**Última actualización**: 2026-03-25  
**Versión**: 1.0
