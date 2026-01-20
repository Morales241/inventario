/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.inventariodominio;
import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Movil;
import Entidades.Puesto;
import Entidades.Trabajador;
import conexion.Conexion;
import jakarta.persistence.*;
import java.time.LocalDate; // Necesario para las fechas
import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        // 1. Iniciar conexión
        Conexion conexion = Conexion.getInstancia();
        EntityManagerFactory entityManagerFactory = conexion.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // ---------------------------------------------------
            // 1. Crear EMPRESA (Raíz de la jerarquía)
            // ---------------------------------------------------
            Empresa emp = new Empresa();
            emp.setNombre("Tech Solutions");
            emp.setUbicacion("Ciudad de México, Reforma 222");
            
            // Guardamos primero la empresa para que tenga ID
            entityManager.persist(emp);

            // ---------------------------------------------------
            // 2. Crear DEPARTAMENTO
            // ---------------------------------------------------
            Departamento depto = new Departamento();
            depto.setNombre("Sistemas y Desarrollo");
            depto.setEmpresa(emp); // Relación FK con Empresa
            
            entityManager.persist(depto);

            // ---------------------------------------------------
            // 3. Crear PUESTO
            // ---------------------------------------------------
            Puesto puesto = new Puesto();
            puesto.setNombre("Senior Java Developer");
            puesto.setDepartamento(depto); // Relación FK con Departamento
            
            entityManager.persist(puesto);

            // ---------------------------------------------------
            // 4. Crear TRABAJADOR
            // ---------------------------------------------------
            Trabajador trab = new Trabajador();
            trab.setNombre("Juan Pérez");
            trab.setPuesto(puesto); // Relación FK con Puesto
            
            entityManager.persist(trab);

            // ---------------------------------------------------
            // 5. Crear EQUIPO (MÓVIL)
            // ---------------------------------------------------
            Movil movil = new Movil();
            
            // --- A. Datos de la clase Padre (EquipoDeComputo) ---
            movil.setMarca("Samsung");
            movil.setModelo("Galaxy S23 Ultra");
            movil.setCondicion("Nuevo"); 
            movil.setEstado("Activo"); // Ej: Activo, En reparación, Baja
            movil.setGri(10025); // Número de inventario interno
            movil.setFactura("FAC-2024-9988");
            movil.setAnioCompra(2024);
            movil.setFechaEntrega(LocalDate.now()); // Fecha de hoy
            movil.setObservaciones("Entregado con caja original");
            
            // Relación: Asignamos quién lo tiene
            movil.setTrabajadorAsignado(trab);

            // --- B. Datos de la clase Hija (Movil) ---
            movil.setImei("123456789012345");
            movil.setNumCelular("55-1234-5678");
            movil.setMemoriaSD("No aplica"); // O "128GB"
            movil.setCargador(true);
            movil.setFunda(true);
            movil.setManosLibres(false);

            entityManager.persist(movil);

            // ---------------------------------------------------
            // Confirmar transacción
            // ---------------------------------------------------
            transaction.commit();
            System.out.println("Datos guardados correctamente.");

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("Error al guardar los datos: " + e.getMessage());
        } finally {
            entityManager.close();
            // entityManagerFactory.close(); // Solo cerrar al apagar toda la app
        }
    }
}
