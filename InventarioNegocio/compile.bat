@echo off
REM Script para compilar el proyecto InventarioNegocio con Maven

echo.
echo ========================================
echo Compilando InventarioNegocio...
echo ========================================
echo.

REM Intenta usar Maven si está disponible en PATH
where maven >nul 2>nul
if %ERRORLEVEL% == 0 (
    mvn clean compile
    goto :end
)

REM Si no está en PATH, intenta con la ruta por defecto de Maven
if exist "C:\Program Files\Apache\Maven\bin\mvn.bat" (
    "C:\Program Files\Apache\Maven\bin\mvn.bat" clean compile
    goto :end
)

REM Si aún no encuentra Maven, lo intenta en Program Files (x86)
if exist "C:\Program Files (x86)\Apache\Maven\bin\mvn.bat" (
    "C:\Program Files (x86)\Apache\Maven\bin\mvn.bat" clean compile
    goto :end
)

REM Si no encuentra Maven en ningún lado, muestra un error
echo.
echo ERROR: Maven no fue encontrado en el PATH
echo Por favor, instala Maven desde: https://maven.apache.org/download.cgi
echo O agrega la ruta de Maven al PATH del sistema
echo.
exit /b 1

:end
echo.
echo ========================================
echo Compilación completada
echo ========================================
echo.
echo Los logs se mostrarán en:
echo   - Terminal: Mensajes de INFO en tiempo real
echo   - logs/inventario.log: Todos los logs
echo   - logs/inventario-error.log: Solo errores
echo.
