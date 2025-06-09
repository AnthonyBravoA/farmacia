package com.utpintegrador.semana10;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StockApp {

    private static final Logger logger = LoggerFactory.getLogger(StockApp.class);

    private final ExcelService excelService;
    private final StockValidator validator;
    private final StatisticsService statisticsService;

    public StockApp() {
        this.excelService = new ExcelService();
        this.validator = new StockValidator();
        this.statisticsService = new StatisticsService();

        logger.info("StockApp inicializada");
        logger.info("Timestamp: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void processStockFile(String inputFilePath, String outputFilePath) {
        Stopwatch totalStopwatch = Stopwatch.createStarted();

        logger.info("=== INICIANDO PROCESAMIENTO DE STOCK ===");
        logger.info("Archivo de entrada: {}", inputFilePath);
        logger.info("Archivo de salida: {}", outputFilePath);

        try {
            if (!ensureInputFileExists(inputFilePath)) {
                logger.error("No se pudo crear o encontrar el archivo de entrada");
                return;
            }

            logger.info("--- PASO 1: Leyendo datos de stock del archivo Excel ---");
            Stopwatch readStopwatch = Stopwatch.createStarted();
            List<Stock> stocks = excelService.readStockFromExcel(inputFilePath);
            readStopwatch.stop();
            logger.info("Lectura completada en {} ms. {} registros leídos",
                    readStopwatch.elapsed(TimeUnit.MILLISECONDS), stocks.size());

            if (stocks.isEmpty()) {
                logger.warn("No se encontraron registros en el archivo");
                return;
            }

            logger.info("--- PASO 2: Validando datos de stock ---");
            Stopwatch validationStopwatch = Stopwatch.createStarted();
            List<Stock> validStocks = validateStockData(stocks);
            validationStopwatch.stop();
            logger.info("Validación completada en {} ms. {} válidos de {} totales",
                    validationStopwatch.elapsed(TimeUnit.MILLISECONDS), validStocks.size(), stocks.size());

            logger.info("--- PASO 3: Generando estadísticas ---");
            Stopwatch statsStopwatch = Stopwatch.createStarted();
            generateStatistics(validStocks);
            statsStopwatch.stop();
            logger.info("Estadísticas generadas en {} ms",
                    statsStopwatch.elapsed(TimeUnit.MILLISECONDS));

            logger.info("--- PASO 4: Escribiendo archivo de salida ---");
            Stopwatch writeStopwatch = Stopwatch.createStarted();
            excelService.writeStockToExcel(validStocks, outputFilePath);
            writeStopwatch.stop();
            logger.info("Escritura completada en {} ms",
                    writeStopwatch.elapsed(TimeUnit.MILLISECONDS));

            logger.info("--- PASO 5: Filtros avanzados ---");
            demonstrateAdvancedFiltering(validStocks);

        } catch (IOException e) {
            logger.error("Error de E/S durante el procesamiento: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado durante el procesamiento: {}", e.getMessage(), e);
        } finally {
            totalStopwatch.stop();
            logger.info("=== PROCESAMIENTO COMPLETADO EN {} ms ===",
                    totalStopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private boolean ensureInputFileExists(String inputFilePath) {
        File inputFile = new File(inputFilePath);

        if (inputFile.exists()) {
            logger.info("✅ Archivo encontrado: {}", inputFilePath);
            return true;
        }

        logger.warn("⚠️ Archivo no encontrado: {}", inputFilePath);
        logger.info("🔄 Generando archivo de datos de prueba...");

        try {
            File parentDir = inputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("📁 Directorio creado: {}", parentDir.getPath());
            }

            TestDataGenerator generator = new TestDataGenerator();
            generator.generateTestStockFile(inputFilePath, 50);

            logger.info("✅ Archivo generado exitosamente");
            return true;

        } catch (Exception e) {
            logger.error("❌ Error al generar archivo: {}", e.getMessage());
            return false;
        }
    }

    private List<Stock> validateStockData(List<Stock> stocks) {
        logger.debug("Validando {} registros de stock", stocks.size());

        List<Stock> validStocks = stocks.stream()
                .filter(stock -> {
                    List<String> errors = validator.validate(stock);
                    if (errors.isEmpty()) {
                        return true;
                    } else {
                        logger.warn("Stock con ID {} inválido. Errores: {}", stock.getId(), String.join(", ", errors));
                        return false;
                    }
                })
                .collect(Collectors.toList());

        logger.info("Validación final: {} válidos, {} inválidos",
                validStocks.size(), stocks.size() - validStocks.size());

        return validStocks;
    }

    private void generateStatistics(List<Stock> stocks) {
        logger.debug("Generando estadísticas para {} registros", stocks.size());

        StatisticsService.StockSummary summary = statisticsService.createSummary(stocks);
        logger.info("RESUMEN GENERAL:");
        logger.info("  Total registros: {}", summary.getTotalProductos());
        logger.info("  Precio promedio: {}", summary.getPrecioPromedio());
        logger.info("  Farmacias: {}", summary.getFarmacias());

        Map<Integer, StatisticsService.FarmaciaStats> pharmacyStats
                = statisticsService.calculateFarmaciaStatistics(stocks);

        logger.info("ESTADÍSTICAS POR FARMACIA:");
        pharmacyStats.forEach((id, stats) -> {
            logger.info("  Farmacia {} ({} productos):", id, stats.getProductoCount());
            logger.info("    Precio promedio: {}", stats.getPrecioPromedio());
            logger.info("    Total unidades disponibles: {}", stats.getCantidadTotal());
        });

        List<Stock> topStock = statisticsService.getTopProductosPorPrecio(stocks, 5);
        logger.info("TOP 5 PRODUCTOS MÁS CAROS:");
        topStock.forEach(stock -> logger.info("  ID {}: S/.{} (Medicamento ID {})",
                stock.getId(), stock.getPrecio(), stock.getMedicamentoId()));
    }

    private void demonstrateAdvancedFiltering(List<Stock> stocks) {
        logger.info("Aplicando filtros avanzados...");

        List<Stock> expensive = statisticsService.filterStock(stocks, 100.0, null, null);
        logger.info("  Productos con precio >= 100: {}", expensive.size());

        List<Stock> available = statisticsService.filterStock(stocks, null, 1, null);
        logger.info("  Productos con al menos 1 unidad disponible: {}", available.size());

        List<Stock> byPharmacy = statisticsService.filterStock(stocks, null, null, 1);
        logger.info("  Productos de la farmacia ID 1: {}", byPharmacy.size());

        Multimap<Integer, Stock> grouped = statisticsService.groupByFarmacia(stocks);
        logger.info("  Agrupados por farmacia: {} farmacias", grouped.keySet().size());
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║         STOCK PROCESSOR APP        ║");
        System.out.println("╚════════════════════════════════════╝");

        logger.info("=== INICIANDO STOCK PROCESSOR APP ===");
        logger.info("Java Version: {}", System.getProperty("java.version"));
        logger.info("User: {}", System.getProperty("user.name"));
        logger.info("Working Directory: {}", System.getProperty("user.dir"));

        try {
            StockApp app = new StockApp();

            String inputFile = "data/stock_input.xlsx";
            String outputFile = "data/stock_output_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".xlsx";

            app.processStockFile(inputFile, outputFile);

            System.out.println("Archivos generados:");
            System.out.println("  • " + inputFile + " (datos de entrada)");
            System.out.println("  • " + outputFile + " (datos procesados)");

            logger.info("=== APLICACIÓN FINALIZADA EXITOSAMENTE ===");

        } catch (Exception e) {
            logger.error("Error fatal: {}", e.getMessage(), e);
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
