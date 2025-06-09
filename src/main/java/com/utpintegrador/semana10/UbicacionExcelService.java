package com.utpintegrador.semana10;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate; // Aunque Ubicacion no tiene fechas, se mantiene por si hay lógica de fecha en otro lugar o se añade
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date; // Aunque Ubicacion no tiene fechas, se mantiene por si hay lógica de fecha en otro lugar o se añade

/**
 * Servicio para leer y escribir datos de Ubicaciones desde/hacia archivos Excel.
 */
public class UbicacionExcelService { // Renombrado de ExcelService a UbicacionExcelService

    private static final Logger logger = LoggerFactory.getLogger(UbicacionExcelService.class); // Cambiado el logger

    /**
     * Lee una lista de ubicaciones desde la primera hoja de un archivo Excel.
     * Espera un formato de columna específico para cada atributo de Ubicacion:
     * 0: Latitud (numérico)
     * 1: Longitud (numérico)
     * 2: Distrito (cadena)
     * 3: Dirección (cadena)
     *
     * @param filePath La ruta al archivo Excel.
     * @return Una lista de objetos Ubicacion.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public List<Ubicacion> readUbicacionesFromExcel(String filePath) throws IOException { // Cambiado el nombre del método
        List<Ubicacion> ubicaciones = new ArrayList<>(); // Cambiado el tipo de lista
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Trabaja con la primera hoja.
            Iterator<Row> rowIterator = sheet.iterator();

            // Omite la fila de encabezado.
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // Itera sobre cada fila para leer ubicaciones.
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // Ignora filas completamente vacías (basado en la primera celda).
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                try {
                    Ubicacion ubicacion = new Ubicacion(); // Crea una nueva instancia de Ubicacion

                    // Lee los atributos de Ubicacion por índice de columna.
                    // Latitud
                    Cell latitudCell = row.getCell(0);
                    if (latitudCell != null && latitudCell.getCellType() == CellType.NUMERIC) {
                        ubicacion.setLatitud(latitudCell.getNumericCellValue());
                    } else {
                        logger.warn("Latitud no válida o nula en fila {}. Se usará 0.0.", row.getRowNum());
                        ubicacion.setLatitud(0.0); // Valor por defecto
                    }

                    // Longitud
                    Cell longitudCell = row.getCell(1);
                    if (longitudCell != null && longitudCell.getCellType() == CellType.NUMERIC) {
                        ubicacion.setLongitud(longitudCell.getNumericCellValue());
                    } else {
                        logger.warn("Longitud no válida o nula en fila {}. Se usará 0.0.", row.getRowNum());
                        ubicacion.setLongitud(0.0); // Valor por defecto
                    }

                    // Distrito
                    ubicacion.setDistrito(getStringCellValue(row.getCell(2)));

                    // Dirección
                    ubicacion.setDireccion(getStringCellValue(row.getCell(3)));
                    
                    ubicaciones.add(ubicacion); // Agrega la ubicación a la lista
                } catch (Exception e) {
                    logger.error("Error leyendo fila de ubicación (Fila {}): {}", row.getRowNum(), e.getMessage(), e);
                }
            }
        }
        logger.info("{} ubicaciones leídas desde archivo Excel: {}", ubicaciones.size(), filePath);
        return ubicaciones;
    }

    /**
     * Escribe una lista de ubicaciones en un nuevo archivo Excel.
     * Crea una hoja llamada "Ubicaciones" con encabezados definidos y autoajusta el ancho de las columnas.
     *
     * @param ubicaciones La lista de objetos Ubicacion a escribir.
     * @param filePath    La ruta al archivo Excel de salida.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public void writeUbicacionesToExcel(List<Ubicacion> ubicaciones, String filePath) throws IOException { // Cambiado el nombre del método
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Ubicaciones"); // Cambiado el nombre de la hoja

            // Define y crea la fila de encabezado.
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Latitud", "Longitud", "Distrito", "Dirección"}; // Encabezados para Ubicacion
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Define estilo de celda de fecha si fuera necesario (no aplica directamente para Ubicacion)
            // CreationHelper createHelper = workbook.getCreationHelper();
            // CellStyle dateCellStyle = workbook.createCellStyle();
            // dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

            // Escribe los datos para cada ubicación en filas consecutivas.
            int rowNum = 1;
            for (Ubicacion ubicacion : ubicaciones) { // Itera sobre objetos Ubicacion
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(ubicacion.getLatitud());
                row.createCell(1).setCellValue(ubicacion.getLongitud());
                row.createCell(2).setCellValue(ubicacion.getDistrito());
                row.createCell(3).setCellValue(ubicacion.getDireccion());
            }

            // Autoajusta todas las columnas a su contenido para una mejor legibilidad.
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos); // Guarda el libro de trabajo en el archivo.
            logger.info("{} ubicaciones escritas en archivo Excel: {}", ubicaciones.size(), filePath); // Mensaje de log actualizado

        } catch (Exception e) {
            logger.error("Error escribiendo ubicaciones en Excel: {}", e.getMessage(), e);
            throw e; // Relanza la excepción para que sea manejada por el llamador.
        }
    }

    /**
     * Obtiene el valor de la celda como String, manejando diferentes tipos de celda.
     *
     * @param cell La celda de Excel.
     * @return El valor de la celda como String; cadena vacía si la celda es nula o el tipo no se maneja.
     */
    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                // Si es numérico y formato de fecha, devuelve la fecha como String.
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula(); // Devuelve la fórmula si es una celda de fórmula.
            default: return ""; // Para tipos de celda inesperados.
        }
    }

    /**
     * Convierte un objeto java.util.Date a java.time.LocalDate.
     * Usa la zona horaria predeterminada del sistema.
     *
     * @param dateToConvert El objeto Date a convertir.
     * @return El objeto LocalDate resultante.
     */
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}