package config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class BaseIntegrationTest {
    
    @BeforeEach
    public void setUp() {
        // Configuración común para todas las pruebas
        System.out.println("Iniciando prueba de integración...");
    }
    
    protected void printTestInfo(String testName) {
        System.out.println("Ejecutando: " + testName);
    }
}