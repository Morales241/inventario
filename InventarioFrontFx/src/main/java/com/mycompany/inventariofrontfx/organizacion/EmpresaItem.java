package com.mycompany.inventariofrontfx.organizacion;

import Dtos.EmpresaDTO;
import javafx.scene.control.TreeItem;
import java.util.ArrayList;
import java.util.List;

public class EmpresaItem {
    private final EmpresaDTO empresa;
    private final List<SucursalItem> sucursales;
    private TreeItem<String> treeItem;

    public EmpresaItem(EmpresaDTO empresa) {
        this.empresa = empresa;
        this.sucursales = new ArrayList<>();
        this.treeItem = new TreeItem<>(empresa.getNombre());
        this.treeItem.setExpanded(true);
    }

    public EmpresaDTO getEmpresa() {
        return empresa;
    }

    public List<SucursalItem> getSucursales() {
        return sucursales;
    }

    public void addSucursal(SucursalItem sucursal) {
        sucursales.add(sucursal);
        treeItem.getChildren().add(sucursal.getTreeItem());
    }

    public TreeItem<String> getTreeItem() {
        return treeItem;
    }

    public Long getId() {
        return empresa.getId();
    }

    public String getNombre() {
        return empresa.getNombre();
    }
}