package com.mycompany.inventariodominio;

import Dao.*;
import Entidades.*;
import Enums.RolCuenta;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("--- Iniciando Generación de Base de Datos ---");

        EntityManagerFactory emf
                = Persistence.createEntityManagerFactory("ConexionPU");

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

//            cargarMockDatos(em);
//            importarDatos(em);
            crearCuenta(em);
            
            em.getTransaction().commit();

            System.out.println("Base de datos generada correctamente");

        } catch (Exception e) {

            em.getTransaction().rollback();

            System.err.println("Error al generar la BD: "
                    + e.getMessage());
            e.printStackTrace();

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void importarDatos(EntityManager em) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream("C:/Users/JesusMorales/Documents/inventario simplificado.xlsx");

        DaoEmpresa daoEmpresa = new DaoEmpresa();

        daoEmpresa.setEntityManager(em);

        importarxlsx lector = new importarxlsx();

        List<Empresa> empresas = lector.readExcelOrganizacion(fis);

        empresas.forEach(e -> {
            daoEmpresa.guardar(e);

            System.out.println(e.toString());
        });

        fis.close();
    }

    private static void cargarMockDatos(EntityManager em) {

        DaoEmpresa daoEmpresa = new DaoEmpresa();
        DaoSucursal daoSucursal = new DaoSucursal();
        DaoDepartamento daoDepartamento = new DaoDepartamento();
        DaoPuesto daoPuesto = new DaoPuesto();
        DaoModelo daoModelo = new DaoModelo();

        // Inyectar EntityManager
        daoEmpresa.setEntityManager(em);
        daoSucursal.setEntityManager(em);
        daoDepartamento.setEntityManager(em);
        daoPuesto.setEntityManager(em);
        daoModelo.setEntityManager(em);

        // ===============================
        // 1️⃣ EMPRESAS
        // ===============================
        Empresa tech = daoEmpresa.guardar(new Empresa("Tech Solutions SA"));
        Empresa logistics = daoEmpresa.guardar(new Empresa("Global Logistics"));
        Empresa market = daoEmpresa.guardar(new Empresa("Mega Market"));
        Empresa finanzas = daoEmpresa.guardar(new Empresa("Finanzas del Norte"));
        Empresa innovacion = daoEmpresa.guardar(new Empresa("Innovación Digital"));

        // ===============================
        // 2️⃣ SUCURSALES
        // ===============================
        Sucursal s1 = daoSucursal.guardar(
                new Sucursal(tech, "Av. Central 123", "Matriz CDMX", new ArrayList<>()));

        Sucursal s2 = daoSucursal.guardar(
                new Sucursal(tech, "Calle Norte 45", "Sucursal Querétaro", new ArrayList<>()));

        Sucursal s3 = daoSucursal.guardar(
                new Sucursal(logistics, "Puerto Industrial S/N", "Bodega Veracruz", new ArrayList<>()));

        Sucursal s4 = daoSucursal.guardar(
                new Sucursal(market, "Centro Comercial Altabrisa", "Punto de Venta Mérida", new ArrayList<>()));

        Sucursal s5 = daoSucursal.guardar(
                new Sucursal(innovacion, "Silicon Drive 88", "Hub Tecnológico", new ArrayList<>()));

        // ===============================
        // 3️⃣ DEPARTAMENTOS
        // ===============================
        Departamento d1 = daoDepartamento.guardar(new Departamento("Sistemas TI", s1));
        Departamento d2 = daoDepartamento.guardar(new Departamento("Recursos Humanos", s1));
        Departamento d3 = daoDepartamento.guardar(new Departamento("Contabilidad", s2));
        Departamento d4 = daoDepartamento.guardar(new Departamento("Ventas", s4));
        Departamento d5 = daoDepartamento.guardar(new Departamento("Operaciones", s3));

        // ===============================
        // 4️⃣ PUESTOS
        // ===============================
        daoPuesto.guardar(new Puesto("Desarrollador Senior", d1));
        daoPuesto.guardar(new Puesto("Analista de Soporte", d1));
        daoPuesto.guardar(new Puesto("Gerente de RH", d2));
        daoPuesto.guardar(new Puesto("Contador General", d3));
        daoPuesto.guardar(new Puesto("Ejecutivo de Ventas", d4));

        // ===============================
        // 5️⃣ MODELOS DE EQUIPO
        // ===============================
        daoModelo.guardar(new Modelo("Optiplex 7090", "Dell", 16, 512, "Intel i7 11th Gen"));
        daoModelo.guardar(new Modelo("EliteBook 840 G8", "HP", 8, 256, "Intel i5 11th Gen"));
        daoModelo.guardar(new Modelo("MacBook Pro M2", "Apple", 16, 512, "Apple M2 Chip"));
        daoModelo.guardar(new Modelo("ThinkPad X1 Carbon", "Lenovo", 32, 1000, "Intel i7 12th Gen"));
        daoModelo.guardar(new Modelo("Galaxy S23", "Samsung", 8, 256, "Snapdragon 8 Gen 2"));

        System.out.println("✔ Datos mock cargados correctamente");
    }

    private static void crearCuenta(EntityManager em) {
        DaoCuentaSistema dao = new DaoCuentaSistema();

        dao.setEntityManager(em);

        dao.guardar(new CuentaSistema("Admin", "GRY", RolCuenta.ADMIN));
    
    }
}
