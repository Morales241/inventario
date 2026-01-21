/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Empresa;
import Entidades.Puesto;
import Entidades.Sucursal;
import Entidades.Trabajador;
import Interfaces.IDaoTrabajador;
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
public class DaoTrabajador extends DaoGenerico<Trabajador, Long> implements IDaoTrabajador {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoTrabajador(Class<Trabajador> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Trabajador busquedaEspecifica(String nombre, String nomina) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);

            Root<Trabajador> root = cq.from(Trabajador.class);

            if (!nombre.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("Nombre")), nombre.toLowerCase()));
            }

            if (!nomina.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("Nomina")), nomina.toLowerCase()));
            }

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }

    @Override
    public List<Trabajador> busquedaConFiltros(String cadena, String numero, String puesto) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);

            Root<Trabajador> root = cq.from(Trabajador.class);

            Join<Trabajador, Puesto> join = root.join("empresa");

            if (!cadena.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("%Nombre%")), cadena.toLowerCase()));
            }

            if (!numero.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("%Nomina%")), numero.toLowerCase()));
            }

            if (!puesto.isEmpty()) {
                predicados.add(cb.equal(cb.lower(join.get("Nombre")), puesto.toLowerCase()));
            }

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getResultList();

        }
    }
}
