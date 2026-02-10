package com.mycompany.inventariodominio;

import Dao.DaoDepartamento;
import Dao.DaoEmpresa;
import Dao.DaoModelo;
import Dao.DaoPuesto;
import Dao.DaoSucursal;
import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Modelo;
import Entidades.Puesto;
import Entidades.Sucursal;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("--- Iniciando Generación de Base de Datos ---");

        try {
            cargarMockDatos();

            System.out.println("¡Tablas creadas exitosamente en la base de datos!");

        } catch (Exception e) {
            System.err.println("Ocurrió un error al generar la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cargarMockDatos() {
        // 1. EMPRESAS
        List<Empresa> empresas = new ArrayList<>();
        empresas.add(new Empresa("Tech Solutions SA"));
        empresas.add(new Empresa("Global Logistics"));
        empresas.add(new Empresa("Mega Market"));
        empresas.add(new Empresa("Finanzas del Norte"));
        empresas.add(new Empresa("Innovación Digital"));

        List<Sucursal> sucursales = new ArrayList<>();
        sucursales.add(new Sucursal(empresas.get(0), "Av. Central 123", "Matriz CDMX", new ArrayList<>()));
        sucursales.add(new Sucursal(empresas.get(0), "Calle Norte 45", "Sucursal Querétaro", new ArrayList<>()));
        sucursales.add(new Sucursal(empresas.get(1), "Puerto Industrial S/N", "Bodega Veracruz", new ArrayList<>()));
        sucursales.add(new Sucursal(empresas.get(2), "Centro Comercial Altabrisa", "Punto de Venta Merida", new ArrayList<>()));
        sucursales.add(new Sucursal(empresas.get(4), "Silicon Drive 88", "Hub Tecnológico", new ArrayList<>()));

        List<Departamento> departamentos = new ArrayList<>();
        departamentos.add(new Departamento("Sistemas e TI", sucursales.get(0)));
        departamentos.add(new Departamento("Recursos Humanos", sucursales.get(0)));
        departamentos.add(new Departamento("Contabilidad", sucursales.get(1)));
        departamentos.add(new Departamento("Ventas", sucursales.get(3)));
        departamentos.add(new Departamento("Operaciones", sucursales.get(2)));

        List<Puesto> puestos = new ArrayList<>();
        puestos.add(new Puesto("Desarrollador Senior", departamentos.get(0)));
        puestos.add(new Puesto("Analista de Soporte", departamentos.get(0)));
        puestos.add(new Puesto("Gerente de RH", departamentos.get(1)));
        puestos.add(new Puesto("Contador General", departamentos.get(2)));
        puestos.add(new Puesto("Ejecutivo de Ventas", departamentos.get(3)));

        List<Modelo> modelos = new ArrayList<>();
        modelos.add(new Modelo("Dell", "Optiplex 7090", 16, 512, "Intel i7 11th Gen"));
        modelos.add(new Modelo("HP", "EliteBook 840 G8", 8, 256, "Intel i5 11th Gen"));
        modelos.add(new Modelo("Apple", "MacBook Pro M2", 16, 512, "Apple M2 Chip"));
        modelos.add(new Modelo("Lenovo", "ThinkPad X1 Carbon", 32, 1000, "Intel i7 12th Gen"));
        modelos.add(new Modelo("Samsung", "Galaxy S23", 8, 256, "Snapdragon 8 Gen 2"));

        DaoEmpresa daoEmpresa = new DaoEmpresa();
        DaoSucursal daoSucursal = new DaoSucursal();
        DaoDepartamento daoDepartamento = new DaoDepartamento();
        DaoPuesto daoPuesto = new DaoPuesto();
        DaoModelo daoModelo = new DaoModelo();

        empresas.forEach(e -> daoEmpresa.guardar(e));
        sucursales.forEach(s -> daoSucursal.guardar(s));
        departamentos.forEach(d -> daoDepartamento.guardar(d));
        puestos.forEach(p -> daoPuesto.guardar(p));
        modelos.forEach(m -> daoModelo.guardar(m));

        System.out.println("=== DATA CARGADO EXITOSAMENTE ===");
    }
}
