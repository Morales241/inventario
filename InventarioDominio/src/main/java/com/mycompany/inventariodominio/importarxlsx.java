package com.mycompany.inventariodominio;

import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Puesto;
import Entidades.Sucursal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
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

//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            case BLANK:
//                return "";
//            default:
//                return "UNKNOWN";
//        }
        return null;
    }

    private int indexColumnaEspecifica(String columna) {
        return encabezados.indexOf(columna);
    }

    private void cargarEncabezados(Iterator<Cell> cellIterator) {

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            String cellValue = getCellValueAsString(cell);

            encabezados.add(cellValue);

        }
    }

    public List<Empresa> readExcelOrganizacion(InputStream file) {
        try {

            encabezados.clear();

            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheet(SHEET);

            Iterator<Row> rowIterator = sheet.iterator();

            List<Empresa> empresas = new ArrayList<>();
            List<Sucursal> sucursales = new ArrayList<>();
            List<Departamento> departamentos = new ArrayList<>();
            List<Puesto> puestos = new ArrayList<>();

            int rowNum = 0;
            int colUen = 0;
            int colUbic = 0;
            int colDep = 0;
            int colPuesto = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                if (row.getRowNum() == 0) {

                    cargarEncabezados(cellIterator);

                    colUen = indexColumnaEspecifica("Uen");
                    colUbic = indexColumnaEspecifica("Ubicaion");
                    colDep = indexColumnaEspecifica("Departamento");
                    colPuesto = indexColumnaEspecifica("Puesto");

                    rowNum++;
                    continue;
                }

                Empresa empresa = new Empresa();
                Sucursal sucursal = new Sucursal();
                Departamento departamento = new Departamento();
                Puesto puesto = new Puesto();

                int celIdx = 0;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    String cellValue = getCellValueAsString(cell);

                    if (celIdx == colUen) {

                        if (!empresas.isEmpty()) {
                            for (Empresa e : empresas) {
                                if (e.getNombre().contains(cellValue)) {
                                    empresa = e;
                                    break;
                                }
                            }
                        }
                        empresa.setNombre(cellValue);

                    } else if (celIdx == colUbic) {

                        if (!empresa.getSucursales().isEmpty()) {

                            for (Sucursal s : empresa.getSucursales()) {
                                if (s.getNombre().contains(cellValue)) {
                                    sucursal = s;
                                    break;
                                }
                            }

                        }
                        sucursal.setNombre(cellValue);
                        sucursal.setUbicacion(cellValue);

                        empresa.getSucursales().add(sucursal);

                    } else if (celIdx == colDep) {

                        if (!sucursal.getDepartamentos().isEmpty()) {

                            for (Departamento d : sucursal.getDepartamentos()) {
                                if (d.getNombre().contains(cellValue)) {
                                    departamento = d;
                                    break;
                                }
                            }

                        }
                        departamento.setNombre(cellValue);

                        sucursal.getDepartamentos().add(departamento);

                    } else if (celIdx == colPuesto) {

                        if (!departamento.getPuestos().isEmpty()) {

                            for (Puesto p : departamento.getPuestos()) {
                                if (p.getNombre().contains(cellValue)) {
                                    puesto = p;
                                    break;
                                }
                            }

                        }
                        puesto.setNombre(cellValue);

                        departamento.getPuestos().add(puesto);

                        System.out.println("### GENERACION-----: %s".formatted(cellValue));
                    }

                    celIdx++;
                }

                boolean llave = false;

                for (Empresa e : empresas) {
                    if (e.getNombre().equalsIgnoreCase(empresa.getNombre())) {
                        llave = true;
                    }
                }

                if (!llave) {
                    empresas.add(empresa);
                }
            }
            workbook.close();
            return empresas;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
