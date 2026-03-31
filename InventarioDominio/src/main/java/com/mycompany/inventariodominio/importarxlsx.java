package com.mycompany.inventariodominio;

import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Puesto;
import Entidades.Sucursal;
import Entidades.Usuario;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author JesusMorales
 */
public class importarxlsx {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static ArrayList<String> encabezados = new ArrayList<>();
    public static String SHEET = "Hoja1";

    private boolean hasExcelFormat(String fileName) {
        return fileName.endsWith(".xlsx");
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    try {
                        return cell.getStringCellValue().trim();
                    } catch (IllegalStateException e) {
                        double formulaValue = cell.getNumericCellValue();
                        if (formulaValue == (long) formulaValue) {
                            return String.valueOf((long) formulaValue);
                        } else {
                            return String.valueOf(formulaValue);
                        }
                    }

                case BLANK:
                    return "";

                default:
                    return "";
            }
        } catch (Exception e) {
            System.err.println("Error al leer celda: " + e.getMessage());
            return "";
        }
    }

    private int indexColumnaEspecifica(String columna) {
        return encabezados.indexOf(columna);
    }

    private void cargarEncabezados(Iterator<Cell> cellIterator) {
        encabezados.clear();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = getCellValueAsString(cell);
            encabezados.add(cellValue);
        }
    }

    public List<Empresa> readExcelOrganizacion(InputStream file) {
        Workbook workbook = null;
        try {
            encabezados.clear();
            workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(SHEET);

            if (sheet == null) {
                System.err.println("No se encontró la hoja: " + SHEET);
                return new ArrayList<>();
            }

            Iterator<Row> rowIterator = sheet.iterator();
            List<Empresa> empresas = new ArrayList<>();

            int rowNum = 0;
            int colUen = -1;
            int colUbic = -1;
            int colDep = -1;
            int colPuesto = -1;
            int colUsuario = -1;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() == 0 || row.getCell(0) == null) {
                    if (row.getRowNum() == 0) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        cargarEncabezados(cellIterator);

                        colUen = indexColumnaEspecifica("Uen");
                        colUbic = indexColumnaEspecifica("Ubicaion");
                        colDep = indexColumnaEspecifica("Departamento");
                        colPuesto = indexColumnaEspecifica("Puesto");
                        colUsuario = indexColumnaEspecifica("Nombre");

                        System.out.println("Columnas encontradas:");
                        System.out.println("  Uen: " + colUen);
                        System.out.println("  Ubicaion: " + colUbic);
                        System.out.println("  Departamento: " + colDep);
                        System.out.println("  Puesto: " + colPuesto);
                        System.out.println("  Usuario: " + colUsuario);
                    }
                    continue;
                }

                String nombreEmpresa = "";
                String nombreSucursal = "";
                String nombreDepartamento = "";
                String nombrePuesto = "";
                String nombreUsuario = "";

                if (colUen != -1 && colUen < row.getLastCellNum()) {
                    Cell cell = row.getCell(colUen);
                    nombreEmpresa = formatearTexto(getCellValueAsString(cell));
                }

                if (colUbic != -1 && colUbic < row.getLastCellNum()) {
                    Cell cell = row.getCell(colUbic);
                    nombreSucursal = formatearTexto(getCellValueAsString(cell));
                }

                if (colDep != -1 && colDep < row.getLastCellNum()) {
                    Cell cell = row.getCell(colDep);
                    nombreDepartamento = formatearTexto(getCellValueAsString(cell));
                }

                if (colPuesto != -1 && colPuesto < row.getLastCellNum()) {
                    Cell cell = row.getCell(colPuesto);
                    nombrePuesto = formatearTexto(getCellValueAsString(cell));
                }

                if (colUsuario != -1 && colUsuario < row.getLastCellNum()) {
                    Cell cell = row.getCell(colUsuario);
                    nombreUsuario = formatearTexto(getCellValueAsString(cell));
                }

                if (nombreEmpresa.isEmpty()) {
                    System.err.println("Fila " + row.getRowNum() + ": Empresa vacía, saltando...");
                    continue;
                }

                Empresa empresa = buscarEmpresa(empresas, nombreEmpresa);
                if (empresa == null) {
                    empresa = new Empresa();
                    empresa.setNombre(nombreEmpresa);
                    empresas.add(empresa);
                }

                if (!nombreSucursal.isEmpty()) {
                    Sucursal sucursal = buscarSucursal(empresa, nombreSucursal);
                    if (sucursal == null) {
                        sucursal = new Sucursal();
                        sucursal.setNombre(nombreSucursal);
                        sucursal.setUbicacion(nombreSucursal);
                        sucursal.setEmpresa(empresa);
                        empresa.getSucursales().add(sucursal);
                    }

                    if (!nombreDepartamento.isEmpty()) {
                        Departamento departamento = buscarDepartamento(sucursal, nombreDepartamento);
                        if (departamento == null) {
                            departamento = new Departamento();
                            departamento.setNombre(nombreDepartamento);
                            departamento.setSucursal(sucursal);
                            sucursal.getDepartamentos().add(departamento);
                        }

                        if (!nombrePuesto.isEmpty()) {
                            Puesto puesto = buscarPuesto(departamento, nombrePuesto);
                            if (puesto == null) {
                                puesto = new Puesto();
                                puesto.setNombre(nombrePuesto);
                                puesto.setDepartamento(departamento);
                                departamento.getPuestos().add(puesto);
                            }

                            if (!nombreUsuario.isEmpty()) {
//                                Usuario usuario = buscarUsuario(puesto, nombreUsuario);
//                                if (usuario == null) {
//                                    usuario = new Usuario();
//                                    usuario.setActivo(true);
//                                    usuario.setNombre(nombreUsuario);
//                                    usuario.setPuesto(puesto);
//                                    puesto.getUsuarios().add(usuario);
//                                }

                                System.out.println("Registro " + row.getRowNum() + ": "
                                        + nombreEmpresa + " | "
                                        + nombreSucursal + " | "
                                        + nombreDepartamento + " | "
                                        + nombrePuesto + " | "
                                        + nombreUsuario);
                            }
                        }
                    }
                }

                rowNum++;
            }

            System.out.println("\n=== RESUMEN ===");
            System.out.println("Total de empresas: " + empresas.size());
            for (Empresa e : empresas) {
                System.out.println("  " + e.getNombre() + " - Sucursales: " + e.getSucursales().size());
            }

            workbook.close();
            return empresas;

        } catch (IOException e) {
            System.err.println("Error al parsear Excel: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar workbook: " + e.getMessage());
            }
        }
    }

    private Empresa buscarEmpresa(List<Empresa> empresas, String nombre) {
        for (Empresa e : empresas) {
            if (e.getNombre() != null && e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    private Sucursal buscarSucursal(Empresa empresa, String nombre) {
        for (Sucursal s : empresa.getSucursales()) {
            if (s.getNombre() != null && s.getNombre().equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        return null;
    }

    private Departamento buscarDepartamento(Sucursal sucursal, String nombre) {
        for (Departamento d : sucursal.getDepartamentos()) {
            if (d.getNombre() != null && d.getNombre().equalsIgnoreCase(nombre)) {
                return d;
            }
        }
        return null;
    }

    private Puesto buscarPuesto(Departamento departamento, String nombre) {
        for (Puesto p : departamento.getPuestos()) {
            if (p.getNombre() != null && p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    private Usuario buscarUsuario(Puesto puesto, String nombre) {
        for (Usuario u : puesto.getUsuarios()) {
            if (u.getNombre() != null && u.getNombre().equalsIgnoreCase(nombre)) {
                return u;
            }
        }
        return null;
    }

    private String formatearTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];

            if (!palabra.isEmpty()) {
                String palabraLower = palabra.toLowerCase();

                String palabraFormateada = palabraLower.substring(0, 1).toUpperCase()
                        + palabraLower.substring(1);

                resultado.append(palabraFormateada);

                if (i < palabras.length - 1) {
                    resultado.append(" ");
                }
            }
        }

        return resultado.toString();
    }
}
