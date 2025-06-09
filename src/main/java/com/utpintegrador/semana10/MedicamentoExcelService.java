package com.utpintegrador.semana10;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID; // Para manejar UUID en Medicamento

/**
 * Servicio para leer y escribir datos de Medicamentos desde/hacia archivos Excel.
 * Proporciona métodos para interactuar con hojas de cálculo para la persistencia de objetos Medicamento.
 */
public class MedicamentoExcelService { // Renombrado de ExcelService a MedicamentoExcelService

    private static final Logger logger = LoggerFactory.getLogger(MedicamentoExcelService.class);

    // Índices de columnas en el archivo Excel para Medicamentos
    private static final int COL_ID_MEDICAMENTO = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_DESCRIPCION = 2;
    private static final int COL_PRECIO = 3;
    private static final int COL_FECHA_VENCIMIENTO = 4;

    /**
     * Lee una lista de medicamentos desde un archivo Excel.
     * Espera que el Excel tenga las columnas: idMedicamento, nombre, descripcion, precio, fechaVencimiento.
     *
     * @param filePath La ruta del archivo Excel.
     * @return Una lista de objetos Medicamento.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public List<Medicamento> readMedicamentosFromExcel(String filePath) throws IOException { // Cambiado a Medicamento
        logger.info("Iniciando lectura de archivo Excel para medicamentos: {}", filePath);

        List<Medicamento> medicamentos = new ArrayList<>(); // Cambiado a Medicamento

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            logger.debug("Leyendo hoja: {} con {} filas", sheet.getSheetName(), sheet.getLastRowNum());

            // Saltar la fila de encabezados (fila 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Medicamento medicamento = extractMedicamentoFromRow(row); // Cambiado a Medicamento
                    if (medicamento != null) {
                        medicamentos.add(medicamento); // Cambiado a Medicamento
                        logger.debug("Medicamento leído: {}", medicamento.getNombre()); // Cambiado a Medicamento
                    }
                } catch (Exception e) {
                    logger.error("Error al procesar fila {} para medicamento: {}", i, e.getMessage(), e); // Cambiado a medicamento
                }
            }

            logger.info("Se leyeron {} medicamentos del archivo {}", medicamentos.size(), filePath); // Cambiado a medicamento
        }

        return medicamentos; // Cambiado a Medicamento
    }

    /**
     * Extrae un objeto Medicamento de una fila del Excel.
     *
     * @param row La fila del Excel.
     * @return Un objeto Medicamento o null si ocurre un error.
     */
    private Medicamento extractMedicamentoFromRow(Row row) { // Cambiado a Medicamento
        try {
            Medicamento medicamento = new Medicamento(); // Cambiado a Medicamento

            // ID
            Cell idCell = row.getCell(COL_ID_MEDICAMENTO);
            if (idCell != null && idCell.getCellType() == CellType.STRING) {
                medicamento.setIdMedicamento(UUID.fromString(idCell.getStringCellValue())); // Cambiado a Medicamento
            } else if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                // Si el ID es numérico (poco común para UUID), puedes manejarlo aquí si es el caso
                logger.warn("ID de Medicamento numérico en Excel. Se esperaba String para UUID. Fila: {}", row.getRowNum());
                // Podrías generar un nuevo UUID o intentar convertir el número a string si tiene sentido
                medicamento.setIdMedicamento(UUID.randomUUID()); // Generar un nuevo UUID si no se encuentra o es inválido
            } else {
                medicamento.setIdMedicamento(UUID.randomUUID()); // Generar un nuevo UUID si no se encuentra o es inválido
            }

            // Nombre
            Cell nombreCell = row.getCell(COL_NOMBRE);
            if (nombreCell != null) {
                medicamento.setNombre(getCellValueAsString(nombreCell)); // Cambiado a Medicamento
            }

            // Descripción
            Cell descripcionCell = row.getCell(COL_DESCRIPCION);
            if (descripcionCell != null) {
                medicamento.setDescripcion(getCellValueAsString(descripcionCell)); // Cambiado a Medicamento
            }

            // Precio
            Cell precioCell = row.getCell(COL_PRECIO);
            if (precioCell != null && precioCell.getCellType() == CellType.NUMERIC) {
                medicamento.setPrecio(precioCell.getNumericCellValue()); // Cambiado a Medicamento
            }

            // Fecha de vencimiento
            Cell fechaVencimientoCell = row.getCell(COL_FECHA_VENCIMIENTO);
            if (fechaVencimientoCell != null && DateUtil.isCellDateFormatted(fechaVencimientoCell)) {
                Date vencimientoDate = fechaVencimientoCell.getDateCellValue();
                medicamento.setFechaVencimiento(vencimientoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()); // Cambiado a Medicamento
            }

            return medicamento; // Cambiado a Medicamento

        } catch (Exception e) {
            logger.error("Error al extraer medicamento de fila: {}", e.getMessage(), e); // Cambiado a medicamento
            return null;
        }
    }

    /**
     * Obtiene el valor de una celda como String.
     *
     * @param cell La celda de Excel.
     * @return El valor de la celda como String.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // O formatear como fecha si es necesario
                }
                // Si es un número que se debería leer como string (ej. un ID grande)
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Escribe una lista de medicamentos a un archivo Excel con formato profesional.
     *
     * @param medicamentos La lista de objetos Medicamento a escribir.
     * @param filePath     La ruta del archivo Excel de salida.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public void writeMedicamentosToExcel(List<Medicamento> medicamentos, String filePath) throws IOException { // Cambiado a Medicamento
        logger.info("Iniciando escritura de {} medicamentos a archivo: {}", medicamentos.size(), filePath); // Cambiado a medicamento

        try (Workbook workbook = new XSSFWorkbook()) {

            // Crear hoja de medicamentos
            Sheet medicamentoSheet = workbook.createSheet("Medicamentos"); // Cambiado a Medicamentos
            createMedicamentoSheet(workbook, medicamentoSheet, medicamentos); // Cambiado a Medicamento

            // Escribir archivo
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                logger.info("Archivo Excel creado exitosamente: {}", filePath);
            }
        }
    }

    /**
     * Crea un estilo para encabezados de tabla en Excel.
     *
     * @param workbook El libro de trabajo de Excel.
     * @return El estilo de celda configurado para encabezados.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * Crea un estilo para datos generales en celdas de Excel.
     *
     * @param workbook El libro de trabajo de Excel.
     * @return El estilo de celda configurado para datos generales.
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * Crea un estilo para valores de moneda en celdas de Excel.
     *
     * @param workbook El libro de trabajo de Excel.
     * @return El estilo de celda configurado para moneda.
     */
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    /**
     * Crea un estilo para fechas en celdas de Excel.
     *
     * @param workbook El libro de trabajo de Excel.
     * @return El estilo de celda configurado para fechas.
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Crea la hoja de medicamentos con encabezados y datos.
     *
     * @param workbook El libro de trabajo de Excel.
     * @param sheet La hoja donde se escribirán los medicamentos.
     * @param medicamentos La lista de objetos Medicamento a escribir.
     */
    private void createMedicamentoSheet(Workbook workbook, Sheet sheet, List<Medicamento> medicamentos) { // Cambiado a Medicamento

        // Crear estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Crear encabezados de columna para Medicamentos
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID Medicamento", "Nombre", "Descripción", "Precio", "Fecha Vencimiento"}; // Encabezados actualizados

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos de medicamentos
        int rowNum = 1;
        for (Medicamento med : medicamentos) { // Iterar sobre Medicamento
            Row row = sheet.createRow(rowNum++);

            // ID Medicamento
            Cell idCell = row.createCell(COL_ID_MEDICAMENTO);
            idCell.setCellValue(med.getIdMedicamento() != null ? med.getIdMedicamento().toString() : ""); // Cambiado a Medicamento
            idCell.setCellStyle(dataStyle);

            // Nombre
            Cell nombreCell = row.createCell(COL_NOMBRE);
            nombreCell.setCellValue(med.getNombre() != null ? med.getNombre() : ""); // Cambiado a Medicamento
            nombreCell.setCellStyle(dataStyle);

            // Descripción
            Cell descripcionCell = row.createCell(COL_DESCRIPCION);
            descripcionCell.setCellValue(med.getDescripcion() != null ? med.getDescripcion() : ""); // Cambiado a Medicamento
            descripcionCell.setCellStyle(dataStyle);

            // Precio
            Cell precioCell = row.createCell(COL_PRECIO);
            precioCell.setCellValue(med.getPrecio()); // Cambiado a Medicamento
            precioCell.setCellStyle(currencyStyle);

            // Fecha de vencimiento
            Cell fechaVencimientoCell = row.createCell(COL_FECHA_VENCIMIENTO);
            if (med.getFechaVencimiento() != null) { // Cambiado a Medicamento
                fechaVencimientoCell.setCellValue(Date.from(med.getFechaVencimiento().atStartOfDay(ZoneId.systemDefault()).toInstant())); // Cambiado a Medicamento
                fechaVencimientoCell.setCellStyle(dateStyle);
            } else {
                fechaVencimientoCell.setCellValue("");
                fechaVencimientoCell.setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        System.out.println("📊 Hoja Excel creada con " + medicamentos.size() + " medicamentos"); // Cambiado a medicamento
    }
}