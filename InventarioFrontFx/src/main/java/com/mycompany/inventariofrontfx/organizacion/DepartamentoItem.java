package com.mycompany.inventariofrontfx.organizacion;

import Dtos.DepartamentoDTO;
import javafx.scene.control.TreeItem;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoItem {
    private final DepartamentoDTO departamento;
    private final List<PuestoItem> puestos;
    private TreeItem<String> treeItem;

    public DepartamentoItem(DepartamentoDTO departamento) {
        this.departamento = departamento;
        this.puestos = new ArrayList<>();
        this.treeItem = new TreeItem<>(departamento.getNombre());
        this.treeItem.setExpanded(true);
    }

    public DepartamentoDTO getDepartamento() {
        return departamento;
    }

    public List<PuestoItem> getPuestos() {
        return puestos;
    }

    public void addPuesto(PuestoItem puesto) {
        puestos.add(puesto);
        treeItem.getChildren().add(puesto.getTreeItem());
    }

    public TreeItem<String> getTreeItem() {
        return treeItem;
    }

    public Long getId() {
        return departamento.getId();
    }

    public String getNombre() {
        return departamento.getNombre();
    }
}