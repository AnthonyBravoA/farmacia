package com.utpintegrador.semana10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File; // Para crear el directorio 'data' si no existe
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID; // Para generar UUIDs para los medicamentos

/**
 * Generador de datos de prueba para la clase Medicamento.
 * Proporciona métodos para crear listas de medicamentos ficticios y guardarlos en un archivo Excel.
 */
public class MedicamentoTestDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MedicamentoTestDataGenerator.class);

    private final Random random = new Random();

    // Datos de ejemplo para generar medicamentos realistas
    private final String[] nombresMedicamento = {
        "Paracetamol", "Ibuprofeno", "Amoxicilina", "Omeprazol", "Loratadina",
        "Salbutamol", "Metformina", "Atorvastatina", "Losartán", "Captopril",
        "Aspirina", "Dexametasona", "Tramadol", "Sertralina", "Clonazepam"
    };

    private final String[] descripcionesMedicamento = {
        "Analgésico y antipirético para alivio del dolor y la fiebre.",
        "Antiinflamatorio no esteroideo para el dolor y la inflamación.",
        "Antibiótico de amplio espectro para infecciones bacterianas.",
        "Reductor de ácido gástrico para acidez y úlceras.",
        "Antihistamínico para aliviar síntomas de alergias.",
        "Broncodilatador para el asma y EPOC.",
        "Hipoglucemiante oral para la diabetes tipo 2.",
        "Hipolipemiante para reducir el colesterol.",
        "Antihipertensivo para la presión arterial alta.",
        "Inhibidor de la ECA para hipertensión y insuficiencia cardíaca."
    };

    /**
     * Genera una lista de medicamentos de prueba.
     *
     * @param count El número de medicamentos a generar.
     * @return Una lista de objetos Medicamento con datos ficticios.
     */
    public List<Medicamento> generateTestMedicamentos(int count) {
        logger.info("🔄 Generando {} medicamentos de prueba...", count);

        List<Medicamento> medicamentos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            try {
                Medicamento medicamento = generateRandomMedicamento(); // Genera un medicamento aleatorio

                // Valida que el medicamento esté completo antes de agregarlo
                if (isMedicamentoComplete(medicamento)) {
                    medicamentos.add(medicamento);
                    if ((i + 1) % 5 == 0) { // Log cada 5 medicamentos generados
                        logger.info("📊 Generados: {} medicamentos", (i + 1));
                    }
                } else {
                    logger.warn("⚠️ Medicamento generado incompleto, reintentando. Iteración: {}", (i + 1));
                    i--; // Reintentar la generación para obtener un medicamento completo
                }
            } catch (Exception e) {
                logger.error("❌ Error al generar medicamento en la iteración {}: {}", (i + 1), e.getMessage(), e);
                // Si ocurre un error grave, podemos decidir si queremos reintentar o simplemente saltar
                // Por ahora, lo registra y reintenta
                i--;
            }
        }

        logger.info("✅ Generación completada: {} medicamentos", medicamentos.size());
        return medicamentos;
    }

    /**
     * Valida que un medicamento tenga todos los datos necesarios para ser considerado completo.
     *
     * @param medicamento El objeto Medicamento a validar.
     * @return true si el medicamento tiene los datos esenciales; false en caso contrario.
     */
    private boolean isMedicamentoComplete(Medicamento medicamento) {
        return medicamento != null &&
               medicamento.getIdMedicamento() != null &&
               medicamento.getNombre() != null && !medicamento.getNombre().trim().isEmpty() &&
               medicamento.getDescripcion() != null && !medicamento.getDescripcion().trim().isEmpty() &&
               medicamento.getPrecio() > 0 && // Precio debe ser positivo
               medicamento.getFechaVencimiento() != null;
    }

    /**
     * Genera un medicamento aleatorio con datos realistas.
     *
     * @return Un objeto Medicamento con propiedades generadas aleatoriamente.
     */
    private Medicamento generateRandomMedicamento() {
        // Genera nombre y descripción aleatorios
        String nombre = nombresMedicamento[random.nextInt(nombresMedicamento.length)];
        String descripcion = descripcionesMedicamento[random.nextInt(descripcionesMedicamento.length)];

        // Genera un precio aleatorio (ej., entre 5.00 y 200.00)
        double precio = 5.00 + (random.nextDouble() * 195.00); // Rango de 5 a 200
        precio = Math.round(precio * 100.0) / 100.0; // Redondear a dos decimales

        // Genera una fecha de vencimiento (entre 1 mes y 5 años a partir de hoy)
        LocalDate fechaVencimiento = LocalDate.now().plusMonths(1)
                                            .plusDays(random.nextInt(365 * 5)); // Añade hasta 5 años en días

        // Crea la instancia de Medicamento
        return new Medicamento(UUID.randomUUID(), nombre, descripcion, precio, fechaVencimiento);
    }

    /**
     * Genera un archivo Excel completo con datos de prueba de medicamentos.
     * Este método utiliza el ExcelService para escribir los datos.
     *
     * @param filePath El path completo donde se guardará el archivo Excel.
     * @param numMedicamentos Número de medicamentos a generar.
     * @throws IOException Si ocurre un error de E/S al escribir el archivo.
     */
    public void generateTestFile(String filePath, int numMedicamentos) throws IOException {

        logger.info("🔄 Generando archivo de prueba: {}", filePath);
        logger.info("📊 Medicamentos a generar: {}", numMedicamentos);

        List<Medicamento> allMedicamentos = new ArrayList<>();

        // Agregar medicamentos normales
        if (numMedicamentos > 0) {
            List<Medicamento> generatedMedicamentos = generateTestMedicamentos(numMedicamentos);
            allMedicamentos.addAll(generatedMedicamentos);
        }

        // Escribir al archivo Excel
        MedicamentoExcelService excelService = new MedicamentoExcelService();
        excelService.writeMedicamentosToExcel(allMedicamentos, filePath); // Debe existir este método en ExcelService

        logger.info("✅ Archivo generado exitosamente: {} ({} medicamentos)", filePath, allMedicamentos.size());
    }

    /**
     * Método main para ejecutar la generación de archivos de prueba de medicamentos.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║    GENERADOR DE DATOS DE MEDICAMENTOS    ║");
        System.out.println("╚════════════════════════════════════╝");

        logger.info("Iniciando generador de datos de prueba de medicamentos.");

        MedicamentoTestDataGenerator generator = new MedicamentoTestDataGenerator();

        try {
            // Crear directorio 'data' si no existe
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                logger.info("📁 Directorio 'data' creado en: {}", dataDir.getAbsolutePath());
            }

            // Generar archivo pequeño para pruebas
            logger.info("\n--- Generando archivo pequeño de medicamentos ---");
            generator.generateTestFile("data/medicamentos_prueba.xlsx", 10);

            // Generar archivo mediano
            logger.info("\n--- Generando archivo mediano de medicamentos ---");
            generator.generateTestFile("data/medicamentos_100.xlsx", 100);

            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║        GENERACIÓN COMPLETADA       ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.println("📁 Archivos generados:");
            System.out.println("  • data/medicamentos_prueba.xlsx (10 medicamentos)");
            System.out.println("  • data/medicamentos_100.xlsx (100 medicamentos)");

            logger.info("Generación de datos de prueba de medicamentos finalizada exitosamente.");

        } catch (IOException e) {
            logger.error("❌ Error de E/S al generar archivos de medicamentos: {}", e.getMessage(), e);
            System.err.println("❌ Error al generar archivos: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("❌ Error inesperado al generar datos de medicamentos: {}", e.getMessage(), e);
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}