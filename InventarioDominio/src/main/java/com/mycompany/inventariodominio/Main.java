package com.mycompany.inventariodominio;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Iniciando Generación de Base de Datos ---");

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConexionPU");
            
            System.out.println("¡Tablas creadas exitosamente en la base de datos!");
            
            emf.close();
            
        } catch (Exception e) {
            System.err.println("Ocurrió un error al generar la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
