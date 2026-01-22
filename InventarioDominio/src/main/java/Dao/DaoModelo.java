/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Modelo;
import Entidades.Puesto;
import Entidades.Sucursal;
import Entidades.Trabajador;
import Interfaces.IDaoModelo;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JMorales
 */
public class DaoModelo extends DaoGenerico<Modelo, Long> implements IDaoModelo {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoModelo(Class<Modelo> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Modelo busquedaEspecifica(String nombre) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Modelo> cq = cb.createQuery(Modelo.class);

            Root<Modelo> root = cq.from(Modelo.class);

            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }

    @Override
    public List<Modelo> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador) {
        
        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Modelo> cq = cb.createQuery(Modelo.class);

            Root<Modelo> root = cq.from(Modelo.class);


            if (!marca.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("marca")), marca.toLowerCase()));
            }

            if (!memoriaRam.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("memoriaRam")), memoriaRam.toLowerCase()));
            }

            if (!almacenamiento.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("almacenamiento")), almacenamiento.toLowerCase()));
            }
            
            if (!procesador.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("procesador")), procesador.toLowerCase()));
            }

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getResultList();

        }
    }

}
