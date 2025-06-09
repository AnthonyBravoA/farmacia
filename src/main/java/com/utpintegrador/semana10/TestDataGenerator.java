package com.utpintegrador.semana10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);
    private final Random random = new Random();

    // Farmacias y medicamentos ficticios
    private final int[] farmaciaIds = {1, 2, 3, 4, 5};
    private final int[] medicamentoIds = {1001, 1002, 1003, 1004, 1005, 2001, 2002, 3001};

    /**
     * Genera una lista de stocks aleatorios válidos
     */
    public List<Stock> generateTestStock(int count) {
        logger.info("🔄 Generando {} registros de stock...", count);

        List<Stock> stocks = new ArrayList<>();
        int intentos = 0;

        for (int i = 1; i <= count; i++) {
            Stock stock = generateRandomStock(i);

            if (stock != null && stock.isValid()) {
                stocks.add(stock);
                if (i % 10 == 0) {
                    logger.info("📊 Generados: {} registros", i);
                }
            } else {
                logger.warn("⚠️ Stock {} inválido, regenerando...", i);
                i--; // Reintentar
                intentos++;
                if (intentos > 10 * count) {
                    logger.error("❌ Demasiados intentos fallidos");
                    break;
                }
            }
        }

        logger.info("✅ Generación completada: {} registros válidos", stocks.size());
        return stocks;
    }

    /**
     * Genera un stock aleatorio
     */
    private Stock generateRandomStock(int id) {
        try {
            Integer farmaciaId = farmaciaIds[random.nextInt(farmaciaIds.length)];
            Integer medicamentoId = medicamentoIds[random.nextInt(medicamentoIds.length)];
            Double precio = 5.0 + (500.0 - 5.0) * random.nextDouble();
            Integer cantidad = random.nextInt(100);
            Boolean disponible = cantidad > 0;

            return new Stock(id, farmaciaId, medicamentoId, Math.round(precio * 100.0) / 100.0, cantidad, disponible);
        } catch (Exception e) {
            logger.error("❌ Error generando stock {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Genera archivo de prueba con datos de stock
     */
    public void generateTestStockFile(String filePath, int count) throws IOException {
        logger.info("🔄 Generando archivo de prueba de stock: {}", filePath);
        List<Stock> stocks = generateTestStock(count);

        ExcelService excelService = new ExcelService();
        excelService.writeStockToExcel(stocks, filePath);

        logger.info("✅ Archivo generado: {} ({} registros)", filePath, stocks.size());
    }

    /**
     * Ejecutable para pruebas
     */
    public static void main(String[] args) {
        TestDataGenerator generator = new TestDataGenerator();

        try {
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("📁 Directorio 'data' creado");
            }

            generator.generateTestStockFile("data/stock_input.xlsx", 50);
        } catch (IOException e) {
            System.err.println("❌ Error al generar archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
