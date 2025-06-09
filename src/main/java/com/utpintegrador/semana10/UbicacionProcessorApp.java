package com.utpintegrador.semana10;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Aplicación principal para gestionar ubicaciones.
 * Realiza lectura, validación y escritura de datos de ubicaciones en archivos Excel.
 */
public class UbicacionProcessorApp { // Renombrado de PromocionProcessorApp a UbicacionProcessorApp

    private static final Logger logger = LoggerFactory.getLogger(UbicacionProcessorApp.class); // Cambiado a UbicacionProcessorApp

    private final UbicacionExcelService excelService; // Se asume que ExcelService se adaptará para Ubicacion
    private final UbicacionValidator validator; // Se asume que se creará UbicacionValidator
    private final UbicacionTestDataGenerator testDataGenerator; // Se asume que se creará UbicacionTestDataGenerator

    /**
     * Constructor que inicializa los servicios y el generador de datos.
     */
    public UbicacionProcessorApp() {
        this.excelService = new UbicacionExcelService(); // Usará ExcelService adaptado para Ubicacion
        this.validator = new UbicacionValidator(); // Nueva instancia de UbicacionValidator
        this.testDataGenerator = new UbicacionTestDataGenerator(); // Nueva instancia de UbicacionTestDataGenerator
        logger.info("UbicacionProcessorApp iniciada. Timestamp: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // Mensaje actualizado
    }

    /**
     * Orquesta el proceso de una ubicación: lee, valida y escribe.
     * Si el archivo de entrada no existe, genera uno con datos de prueba.
     *
     * @param inputFilePath  Ruta del archivo Excel de entrada.
     * @param outputFilePath Ruta del archivo Excel de salida.
     */
    public void processUbicacionFile(String inputFilePath, String outputFilePath) { // Renombrado de processPromocionFile

        Stopwatch totalStopwatch = Stopwatch.createStarted();

        logger.info("=== INICIANDO PROCESAMIENTO DE UBICACIONES ==="); // Mensaje actualizado
        logger.info("Archivo de entrada: {}", inputFilePath);
        logger.info("Archivo de salida: {}", outputFilePath);

        try {
            // Asegura que el archivo de entrada exista; si no, lo genera.
            if (!ensureInputFileExists(inputFilePath)) {
                logger.error("No se pudo preparar el archivo de entrada para ubicaciones. Abortando."); // Mensaje actualizado
                return;
            }

            // 1. Lee ubicaciones del Excel.
            logger.info("--- PASO 1: Leyendo ubicaciones del archivo Excel ---"); // Mensaje actualizado
            Stopwatch readStopwatch = Stopwatch.createStarted();
            List<Ubicacion> ubicaciones = excelService.readUbicacionesFromExcel(inputFilePath); // Cambiado a Ubicacion y readUbicacionesFromExcel
            readStopwatch.stop();
            logger.info("Lectura completada en {} ms. {} ubicaciones leídas.", // Mensaje actualizado
                         readStopwatch.elapsed(TimeUnit.MILLISECONDS), ubicaciones.size());

            if (ubicaciones.isEmpty()) {
                logger.warn("No se encontraron ubicaciones en el archivo de entrada. Nada que procesar."); // Mensaje actualizado
                return;
            }

            // 2. Valida y normaliza las ubicaciones.
            logger.info("--- PASO 2: Validando y normalizando ubicaciones ---"); // Mensaje actualizado
            Stopwatch validationStopwatch = Stopwatch.createStarted();
            List<Ubicacion> validUbicaciones = validateAndNormalizeUbicaciones(ubicaciones); // Cambiado a Ubicacion y validateAndNormalizeUbicaciones
            validationStopwatch.stop();
            logger.info("Validación completada en {} ms. {} ubicaciones válidas de {} totales.", // Mensaje actualizado
                         validationStopwatch.elapsed(TimeUnit.MILLISECONDS),
                         validUbicaciones.size(), ubicaciones.size());

            // PASO 3 (Opcional): Integración con un servicio de estadísticas de ubicaciones.
            // Si se necesitara, este sería el punto para generar informes o métricas avanzadas.

            // 4. Escribe las ubicaciones válidas en el archivo de salida.
            logger.info("--- PASO 4: Escribiendo archivo de salida ---"); // Mensaje actualizado
            Stopwatch writeStopwatch = Stopwatch.createStarted();
            excelService.writeUbicacionesToExcel(validUbicaciones, outputFilePath); // Cambiado a Ubicacion y writeUbicacionesToExcel
            writeStopwatch.stop();
            logger.info("Escritura completada en {} ms.", writeStopwatch.elapsed(TimeUnit.MILLISECONDS));

        } catch (IOException e) {
            logger.error("Error de E/S durante el procesamiento de ubicaciones: {}", e.getMessage(), e); // Mensaje actualizado
        } catch (Exception e) {
            logger.error("Error inesperado durante el procesamiento de ubicaciones: {}", e.getMessage(), e); // Mensaje actualizado
        } finally {
            totalStopwatch.stop();
            logger.info("=== PROCESAMIENTO DE UBICACIONES COMPLETADO EN {} ms ===", // Mensaje actualizado
                         totalStopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Verifica la existencia del archivo de entrada. Si no existe, lo genera
     * con un conjunto de ubicaciones de prueba.
     *
     * @param inputFilePath La ruta del archivo a verificar/generar.
     * @return true si el archivo existe o fue generado con éxito; false en caso contrario.
     */
    private boolean ensureInputFileExists(String inputFilePath) {
        File inputFile = new File(inputFilePath);

        if (inputFile.exists()) {
            logger.info("✅ Archivo de entrada de ubicaciones encontrado: {}", inputFilePath); // Mensaje actualizado
            return true;
        }

        logger.warn("⚠️ Archivo de entrada de ubicaciones no encontrado: {}", inputFilePath); // Mensaje actualizado
        logger.info("🔄 Generando archivo de datos de prueba automáticamente (20 ubicaciones)..."); // Mensaje actualizado

        try {
            // Crea el directorio padre si no existe.
            File parentDir = inputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("📁 Directorio 'data' creado: {}", parentDir.getPath());
            }

            // Genera y escribe ubicaciones de prueba en el archivo de entrada.
            List<Ubicacion> testUbicaciones = testDataGenerator.generateTestUbicaciones(20); // Cambiado a Ubicacion y generateTestUbicaciones
            excelService.writeUbicacionesToExcel(testUbicaciones, inputFilePath); // Cambiado a Ubicacion y writeUbicacionesToExcel

            logger.info("✅ Archivo de entrada de ubicaciones generado exitosamente en: {}", inputFilePath); // Mensaje actualizado
            return true;

        } catch (Exception e) {
            logger.error("❌ Error al generar archivo de entrada de ubicaciones: {}", e.getMessage(), e); // Mensaje actualizado
            return false;
        }
    }

    /**
     * Normaliza y filtra una lista de ubicaciones, manteniendo solo las válidas.
     *
     * @param ubicaciones La lista original de ubicaciones.
     * @return Una nueva lista con las ubicaciones que pasaron la validación.
     */
    private List<Ubicacion> validateAndNormalizeUbicaciones(List<Ubicacion> ubicaciones) { // Cambiado a Ubicacion
        logger.debug("Iniciando validación y normalización de {} ubicaciones.", ubicaciones.size()); // Mensaje actualizado

        List<Ubicacion> validUbicaciones = ubicaciones.stream() // Cambiado a Ubicacion
                .peek(validator::normalizeUbicacion) // Cambiado a normalizeUbicacion
                .filter(ubicacion -> { // Cambiado a ubicacion
                    List<String> errors = validator.validate(ubicacion); // Cambiado a ubicacion
                    if (errors.isEmpty()) {
                        return true; // La ubicación es válida.
                    } else {
                        // Registra los errores para ubicaciones inválidas.
                        logger.warn("Ubicación '{}' inválida. Errores: {}", // Mensaje actualizado
                                    ubicacion.getDireccion(), String.join(", ", errors)); // Usando getDireccion para identificación
                        return false; // Excluye la ubicación inválida.
                    }
                })
                .collect(Collectors.toList());

        int validCount = validUbicaciones.size();
        int invalidCount = ubicaciones.size() - validCount;

        logger.info("Resumen de validación: {} válidas, {} inválidas.", validCount, invalidCount); // Mensaje actualizado

        if (invalidCount > 0) {
            logger.warn("Se excluyeron {} ubicaciones con datos inválidos.", invalidCount); // Mensaje actualizado
        }

        return validUbicaciones;
    }

    /**
     * Punto de entrada principal de la aplicación.
     * Configura y ejecuta el proceso de manejo de ubicaciones.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║    UBICACION PROCESSOR APPLICATION   ║"); // Cambiado el título
        System.out.println("╚════════════════════════════════════╝");

        logger.info("=== INICIANDO UBICACION PROCESSOR APPLICATION ==="); // Cambiado el título
        logger.info("Versión Java: {}", System.getProperty("java.version"));
        logger.info("Usuario: {}", System.getProperty("user.name"));
        logger.info("Directorio de trabajo: {}", System.getProperty("user.dir"));

        try {
            UbicacionProcessorApp app = new UbicacionProcessorApp(); // Instanciando el nuevo UbicacionProcessorApp

            String inputFile = "data/ubicaciones_input.xlsx"; // Cambiado el nombre del archivo
            String outputFile = "data/ubicaciones_processed_" + // Cambiado el nombre del archivo
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                                ".xlsx";

            app.processUbicacionFile(inputFile, outputFile); // Llamando al método de proceso de ubicaciones

            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║          PROCESAMIENTO EXITOSO!          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.println("Archivos generados:");
            System.out.println("  • " + inputFile + " (datos de entrada)");
            System.out.println("  • " + outputFile + " (datos procesados)");

            logger.info("=== APLICACIÓN FINALIZADA EXITOSAMENTE ===");

        } catch (Exception e) {
            logger.error("Error fatal en la aplicación de ubicaciones: {}", e.getMessage(), e); // Mensaje actualizado
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
