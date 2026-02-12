/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 *
 * @author tacot
 */
public class Conexion {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConexionPU");

    private Conexion() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}


