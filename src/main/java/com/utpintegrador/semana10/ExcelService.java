    package com.utpintegrador.semana10;

    import org.apache.poi.ss.usermodel.*;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    public class ExcelService {

        private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

        // Índices de columnas
        private static final int COL_ID = 0;
        private static final int COL_FARMACIA = 1;
        private static final int COL_MEDICAMENTO = 2;
        private static final int COL_PRECIO = 3;
        private static final int COL_CANTIDAD = 4;
        private static final int COL_DISPONIBLE = 5;

        /**
         * Lee stocks desde un archivo Excel
         */
        public List<Stock> readStockFromExcel(String filePath) throws IOException {
            logger.info("Leyendo archivo Excel: {}", filePath);

            List<Stock> stocks = new ArrayList<>();

            try (FileInputStream fis = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    try {
                        Stock stock = extractStockFromRow(row);
                        if (stock != null) {
                            stocks.add(stock);
                            logger.debug("Stock leído: {}", stock.getId());
                        }
                    } catch (Exception e) {
                        logger.error("Error al procesar fila {}: {}", i, e.getMessage());
                    }
                }
            }

            return stocks;
        }

        /**
         * Extrae un Stock desde una fila del Excel
         */
        private Stock extractStockFromRow(Row row) {
            try {
                Integer id = (int) row.getCell(COL_ID).getNumericCellValue();
                Integer farmaciaId = (int) row.getCell(COL_FARMACIA).getNumericCellValue();
                Integer medicamentoId = (int) row.getCell(COL_MEDICAMENTO).getNumericCellValue();
                Double precio = row.getCell(COL_PRECIO).getNumericCellValue();
                Integer cantidad = (int) row.getCell(COL_CANTIDAD).getNumericCellValue();
                Boolean disponible = row.getCell(COL_DISPONIBLE).getBooleanCellValue();

                return new Stock(id, farmaciaId, medicamentoId, precio, cantidad, disponible);
            } catch (Exception e) {
                logger.error("Error al extraer stock: {}", e.getMessage());
                return null;
            }
        }

        /**
         * Escribe una lista de stocks a un archivo Excel
         */
        public void writeStockToExcel(List<Stock> stocks, String filePath) throws IOException {
            logger.info("Escribiendo {} stocks a archivo: {}", stocks.size(), filePath);

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Stock");

                // Encabezado
                Row header = sheet.createRow(0);
                String[] headers = {"ID", "Farmacia ID", "Medicamento ID", "Precio", "Cantidad", "Disponible"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                // Filas de datos
                int rowNum = 1;
                for (Stock stock : stocks) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(COL_ID).setCellValue(stock.getId());
                    row.createCell(COL_FARMACIA).setCellValue(stock.getFarmaciaId());
                    row.createCell(COL_MEDICAMENTO).setCellValue(stock.getMedicamentoId());
                    row.createCell(COL_PRECIO).setCellValue(stock.getPrecio());
                    row.createCell(COL_CANTIDAD).setCellValue(stock.getCantidad());
                    row.createCell(COL_DISPONIBLE).setCellValue(stock.getDisponible());
                }

                // Guardar
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    workbook.write(fos);
                }
            }

            logger.info("Archivo Excel guardado correctamente");
        }
    }
