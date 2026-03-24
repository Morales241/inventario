package com.mycompany.inventariofrontfx.organizacion;

import Dtos.SucursalDTO;
import javafx.scene.control.TreeItem;
import java.util.ArrayList;
import java.util.List;

public class SucursalItem {
    private final SucursalDTO sucursal;
    private final List<DepartamentoItem> departamentos;
    private TreeItem<String> treeItem;

    public SucursalItem(SucursalDTO sucursal) {
        this.sucursal = sucursal;
        this.departamentos = new ArrayList<>();
        this.treeItem = new TreeItem<>(sucursal.getNombre() + " (" + sucursal.getUbicacion() + ")");
        this.treeItem.setExpanded(true);
    }

    public SucursalDTO getSucursal() {
        return sucursal;
    }

    public List<DepartamentoItem> getDepartamentos() {
        return departamentos;
    }

    public void addDepartamento(DepartamentoItem departamento) {
        departamentos.add(departamento);
        treeItem.getChildren().add(departamento.getTreeItem());
    }

    public TreeItem<String> getTreeItem() {
        return treeItem;
    }

    public Long getId() {
        return sucursal.getId();
    }

    public String getNombre() {
        return sucursal.getNombre();
    }
}