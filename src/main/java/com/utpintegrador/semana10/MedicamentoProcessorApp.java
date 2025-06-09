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
 * Aplicación principal para gestionar medicamentos.
 * Realiza lectura, validación y escritura de datos de medicamentos en archivos Excel.
 * Utiliza ExcelService para interactuar con archivos Excel.
 * Utiliza MedicamentoValidator para validar los datos de los medicamentos.
 * Utiliza MedicamentoTestDataGenerator para crear datos de prueba si no existen.
 */
public class MedicamentoProcessorApp {

    private static final Logger logger = LoggerFactory.getLogger(MedicamentoProcessorApp.class);

    private final MedicamentoExcelService excelService;
    private final MedicamentoValidator validator; // Ahora para Medicamentos
    private final MedicamentoTestDataGenerator testDataGenerator; // Ahora para Medicamentos

    /**
     * Constructor que inicializa los servicios y el generador de datos para medicamentos.
     */
    public MedicamentoProcessorApp() {
        this.excelService = new MedicamentoExcelService();
        this.validator = new MedicamentoValidator();
        this.testDataGenerator = new MedicamentoTestDataGenerator();
        logger.info("MedicamentoProcessorApp iniciada. Timestamp: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    /**
     * Orquesta el proceso de un archivo de medicamentos: lee, valida y escribe.
     * Si el archivo de entrada no existe, genera uno con datos de prueba.
     *
     * @param inputFilePath  Ruta del archivo Excel de entrada.
     * @param outputFilePath Ruta del archivo Excel de salida.
     */
    public void processMedicamentoFile(String inputFilePath, String outputFilePath) {

        Stopwatch totalStopwatch = Stopwatch.createStarted();

        logger.info("=== INICIANDO PROCESAMIENTO DE MEDICAMENTOS ===");
        logger.info("Archivo de entrada: {}", inputFilePath);
        logger.info("Archivo de salida: {}", outputFilePath);

        try {
            // Asegura que el archivo de entrada exista; si no, lo genera.
            if (!ensureInputFileExists(inputFilePath)) {
                logger.error("No se pudo preparar el archivo de entrada para medicamentos. Abortando.");
                return;
            }

            // 1. Lee medicamentos del Excel.
            logger.info("--- PASO 1: Leyendo medicamentos del archivo Excel ---");
            Stopwatch readStopwatch = Stopwatch.createStarted();
            List<Medicamento> medicamentos = excelService.readMedicamentosFromExcel(inputFilePath); // Cambiado a Medicamentos
            readStopwatch.stop();
            logger.info("Lectura completada en {} ms. {} medicamentos leídos.",
                         readStopwatch.elapsed(TimeUnit.MILLISECONDS), medicamentos.size());

            if (medicamentos.isEmpty()) {
                logger.warn("No se encontraron medicamentos en el archivo de entrada. Nada que procesar.");
                return;
            }

            // 2. Valida y normaliza los medicamentos.
            logger.info("--- PASO 2: Validando y normalizando medicamentos ---");
            Stopwatch validationStopwatch = Stopwatch.createStarted();
            List<Medicamento> validMedicamentos = validateAndNormalizeMedicamentos(medicamentos); // Cambiado a Medicamentos
            validationStopwatch.stop();
            logger.info("Validación completada en {} ms. {} medicamentos válidos de {} totales.",
                         validationStopwatch.elapsed(TimeUnit.MILLISECONDS),
                         validMedicamentos.size(), medicamentos.size());

            // PASO 3 (Opcional): Integración con un servicio de estadísticas de medicamentos.
            // Si se necesitara, este sería el punto para generar informes o métricas avanzadas.

            // 4. Escribe los medicamentos válidos en el archivo de salida.
            logger.info("--- PASO 4: Escribiendo archivo de salida ---");
            Stopwatch writeStopwatch = Stopwatch.createStarted();
            excelService.writeMedicamentosToExcel(validMedicamentos, outputFilePath); // Cambiado a Medicamentos
            writeStopwatch.stop();
            logger.info("Escritura completada en {} ms.", writeStopwatch.elapsed(TimeUnit.MILLISECONDS));

        } catch (IOException e) {
            logger.error("Error de E/S durante el procesamiento de medicamentos: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado durante el procesamiento de medicamentos: {}", e.getMessage(), e);
        } finally {
            totalStopwatch.stop();
            logger.info("=== PROCESAMIENTO DE MEDICAMENTOS COMPLETADO EN {} ms ===",
                         totalStopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Verifica la existencia del archivo de entrada. Si no existe, lo genera
     * con un conjunto de medicamentos de prueba.
     *
     * @param inputFilePath La ruta del archivo a verificar/generar.
     * @return true si el archivo existe o fue generado con éxito; false en caso contrario.
     */
    private boolean ensureInputFileExists(String inputFilePath) {
        File inputFile = new File(inputFilePath);

        if (inputFile.exists()) {
            logger.info("✅ Archivo de entrada de medicamentos encontrado: {}", inputFilePath);
            return true;
        }

        logger.warn("⚠️ Archivo de entrada de medicamentos no encontrado: {}", inputFilePath);
        logger.info("🔄 Generando archivo de datos de prueba automáticamente (20 medicamentos)...");

        try {
            // Crea el directorio padre si no existe.
            File parentDir = inputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("📁 Directorio 'data' creado: {}", parentDir.getPath());
            }

            // Genera y escribe medicamentos de prueba en el archivo de entrada.
            List<Medicamento> testMedicamentos = testDataGenerator.generateTestMedicamentos(20); // Cambiado a Medicamentos
            excelService.writeMedicamentosToExcel(testMedicamentos, inputFilePath); // Cambiado a Medicamentos

            logger.info("✅ Archivo de entrada de medicamentos generado exitosamente en: {}", inputFilePath);
            return true;

        } catch (Exception e) {
            logger.error("❌ Error al generar archivo de entrada de medicamentos: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Normaliza y filtra una lista de medicamentos, manteniendo solo los válidos.
     *
     * @param medicamentos La lista original de medicamentos.
     * @return Una nueva lista con los medicamentos que pasaron la validación.
     */
    private List<Medicamento> validateAndNormalizeMedicamentos(List<Medicamento> medicamentos) { // Cambiado a Medicamentos
        logger.debug("Iniciando validación y normalización de {} medicamentos.", medicamentos.size());

        List<Medicamento> validMedicamentos = medicamentos.stream() // Cambiado a Medicamentos
                .peek(validator::normalizeMedicamento) // Cambiado a Medicamento
                .filter(medicamento -> { // Cambiado a medicamento
                    List<String> errors = validator.validate(medicamento); // Cambiado a medicamento
                    if (errors.isEmpty()) {
                        return true; // El medicamento es válido.
                    } else {
                        // Registra los errores para medicamentos inválidos.
                        logger.warn("Medicamento '{}' inválido. Errores: {}",
                                    medicamento.getNombre(), String.join(", ", errors)); // Cambiado a medicamento
                        return false; // Excluye el medicamento inválido.
                    }
                })
                .collect(Collectors.toList());

        int validCount = validMedicamentos.size();
        int invalidCount = medicamentos.size() - validCount;

        logger.info("Resumen de validación: {} válidos, {} inválidos.", validCount, invalidCount);

        if (invalidCount > 0) {
            logger.warn("Se excluyeron {} medicamentos con datos inválidos.", invalidCount);
        }

        return validMedicamentos;
    }

    /**
     * Punto de entrada principal de la aplicación.
     * Configura y ejecuta el proceso de manejo de medicamentos.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║    MEDICAMENTO PROCESSOR APPLICATION   ║"); // Cambiado el título
        System.out.println("╚════════════════════════════════════╝");

        logger.info("=== INICIANDO MEDICAMENTO PROCESSOR APPLICATION ==="); // Cambiado el título
        logger.info("Versión Java: {}", System.getProperty("java.version"));
        logger.info("Usuario: {}", System.getProperty("user.name"));
        logger.info("Directorio de trabajo: {}", System.getProperty("user.dir"));

        try {
            MedicamentoProcessorApp app = new MedicamentoProcessorApp(); // Cambiado el nombre de la clase

            String inputFile = "data/medicamentos_input.xlsx"; // Cambiado el nombre del archivo
            String outputFile = "data/medicamentos_processed_" + // Cambiado el nombre del archivo
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                                ".xlsx";

            app.processMedicamentoFile(inputFile, outputFile); // Cambiado el método de proceso

            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║          PROCESAMIENTO EXITOSO!          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.println("Archivos generados:");
            System.out.println("  • " + inputFile + " (datos de entrada)");
            System.out.println("  • " + outputFile + " (datos procesados)");

            logger.info("=== APLICACIÓN FINALIZADA EXITOSAMENTE ===");

        } catch (Exception e) {
            logger.error("Error fatal en la aplicación de medicamentos: {}", e.getMessage(), e); // Cambiado el mensaje
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
