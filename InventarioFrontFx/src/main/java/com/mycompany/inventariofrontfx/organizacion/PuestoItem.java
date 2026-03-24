package com.mycompany.inventariofrontfx.organizacion;

import Dtos.PuestoDTO;
import javafx.scene.control.TreeItem;

public class PuestoItem {
    private final PuestoDTO puesto;
    private TreeItem<String> treeItem;

    public PuestoItem(PuestoDTO puesto) {
        this.puesto = puesto;
        this.treeItem = new TreeItem<>(puesto.getNombre());
    }

    public PuestoDTO getPuesto() {
        return puesto;
    }

    public TreeItem<String> getTreeItem() {
        return treeItem;
    }

    public Long getId() {
        return puesto.getId();
    }

    public String getNombre() {
        return puesto.getNombre();
    }
}