package com.utpintegrador.semana10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Clase principal para probar la integración de librerías y funcionalidades de Medicamento.
 * Demuestra logging, manejo de colecciones, utilidades de String, comparaciones y guardado a Excel.
 */
public class MedicamentoDemo { // Renombrado de Ejemplo a MedicamentoDemo

    private static final Logger logger = LoggerFactory.getLogger(MedicamentoDemo.class);

    public static void main(String[] args) {
        logger.info("INICIANDO PRUEBAS DE LIBRERÍAS Y CLASE MEDICAMENTO."); // Mensaje actualizado

        // --- DEMOSTRACIÓN: LOGBACK (Registro de eventos) ---
        logger.info("\n--- Probando Logback ---");
        logger.error("Logback ERROR: Error crítico en la gestión de stock de medicamento.");
        logger.warn("Logback WARN: El medicamento 'Paracetamol' está a punto de vencer.");
        logger.info("Logback INFO: Nuevo medicamento registrado en el sistema.");

        // --- DEMOSTRACIÓN: GUAVA Y CREACIÓN DE MEDICAMENTOS ---
        logger.info("\n--- Probando Guava y creación de Medicamentos ---"); // Mensaje actualizado
        // Creación de medicamentos con datos claros para el Excel.
        Medicamento med1 = new Medicamento( // Cambiado de Promocion a Medicamento
            UUID.randomUUID(), // ID único
            "Aspirina 500mg",
            "Analgésico y antipirético para el alivio de dolores leves a moderados y fiebre.",
            8.50, // Precio
            LocalDate.of(2026, 3, 15) // Fecha de vencimiento
        );

        Medicamento med2 = new Medicamento( // Cambiado de Promocion a Medicamento
            UUID.randomUUID(),
            "Ibuprofeno 400mg",
            "Antiinflamatorio no esteroideo (AINE) para el dolor, la fiebre y la inflamación.",
            12.75,
            LocalDate.of(2025, 1, 30) // Fecha de vencimiento cercana
        );

        Medicamento med3 = new Medicamento( // Cambiado de Promocion a Medicamento
            UUID.randomUUID(),
            "Amoxicilina 250mg",
            "Antibiótico de amplio espectro para el tratamiento de infecciones bacterianas.",
            25.00,
            LocalDate.of(2027, 8, 1)
        );

        Medicamento med4 = new Medicamento( // Cambiado de Promocion a Medicamento
            UUID.randomUUID(),
            "Paracetamol 1g",
            "Analgésico y antipirético, utilizado para aliviar el dolor y reducir la fiebre.",
            6.20,
            LocalDate.of(2026, 11, 20)
        );

        // Uso de Guava para crear la lista de medicamentos.
        List<Medicamento> medicamentos = Lists.newArrayList(med1, med2, med3, med4); // Cambiado a Medicamento
        logger.info("Guava: Se creó una lista de {} medicamentos.", medicamentos.size()); // Mensaje actualizado
        
        logger.info("\n--- Medicamentos en orden de creación ---"); // Mensaje actualizado
        medicamentos.forEach(m -> logger.info("  - Nombre: '{}', Precio: ${:.2f}", m.getNombre(), m.getPrecio())); // Mensaje actualizado


        // --- DEMOSTRACIÓN: COMPARABLE (Orden Natural) ---
        logger.info("\n--- Demostrando Comparable (Orden Natural: Nombre ascendente, luego Fecha Vencimiento) ---"); // Mensaje actualizado
        // Collections.sort() usa el método compareTo() de Medicamento.
        Collections.sort(medicamentos); // Cambiado a Medicamento
        
        medicamentos.forEach(m -> logger.info("  - Nombre: '{}', Vencimiento: {}", m.getNombre(), m.getFechaVencimiento())); // Mensaje actualizado


        // --- DEMOSTRACIÓN: COMPARATOR (Orden Personalizado) ---
        logger.info("\n--- Demostrando Comparator (Orden Personalizado: Precio descendente) ---"); // Mensaje actualizado
        // Ordena por el precio, del más alto al más bajo.
        Comparator<Medicamento> porPrecioDescendente = (mA, mB) -> { // Cambiado a Medicamento
            return Double.compare(mB.getPrecio(), mA.getPrecio()); // Descendente por precio
        };
        Collections.sort(medicamentos, porPrecioDescendente); // Cambiado a Medicamento
        medicamentos.forEach(m -> logger.info("  - Nombre: '{}', Precio: ${:.2f}", m.getNombre(), m.getPrecio())); // Mensaje actualizado
        
        logger.info("\n--- Demostrando Otro Comparator (Orden Personalizado: Descripción ascendente) ---"); // Mensaje actualizado
        // Ordena por descripción alfabéticamente.
        Comparator<Medicamento> porDescripcionAscendente = (mA, mB) -> { // Cambiado a Medicamento
            return Comparator.nullsFirst(Comparator.comparing(Medicamento::getDescripcion)) // Cambiado a Medicamento
                             .compare(mA, mB);
        };
        Collections.sort(medicamentos, porDescripcionAscendente); // Cambiado a Medicamento
        medicamentos.forEach(m -> logger.info("  - Nombre: '{}', Descripción: '{}'", m.getNombre(), StringUtils.abbreviate(m.getDescripcion(), 30))); // Mensaje actualizado


        // --- DEMOSTRACIÓN: APACHE COMMONS LANG3 (Utilidades de String) ---
        logger.info("\n--- Probando Apache Commons Lang3 ---");
        // Convierte el nombre a mayúsculas.
        String nombreMedMayusculas = StringUtils.upperCase(med1.getNombre()); // Cambiado a Medicamento
        logger.info("Commons Lang3: Nombre en mayúsculas: {}", nombreMedMayusculas); // Mensaje actualizado

        // Trunca la descripción si es muy larga.
        String descripcionTruncada = StringUtils.abbreviate(med1.getDescripcion(), 25); // Cambiado a Medicamento
        logger.info("Commons Lang3: Descripción truncada: '{}'", descripcionTruncada); // Mensaje actualizado

        // Verifica si la descripción de un medicamento está vacía o nula.
        if (StringUtils.isBlank(med2.getDescripcion())) { // Cambiado a Medicamento y campo
            logger.info("Commons Lang3: La descripción de '{}' está vacía/nula.", med2.getNombre()); // Mensaje y campo actualizado
        } else {
            logger.info("Commons Lang3: La descripción de '{}' NO está vacía/nula.", med2.getNombre()); // Mensaje y campo actualizado
        }

        // --- DEMOSTRACIÓN: GUARDAR MEDICAMENTOS EN EXCEL ---
        logger.info("\n--- Guardando medicamentos a un archivo Excel ---"); // Mensaje actualizado
        MedicamentoExcelService excelService = new MedicamentoExcelService(); // Cambiado a MedicamentoExcelService
        String outputFilePath = "data/medicamentos_generados_" + // Nombre de archivo actualizado
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                                ".xlsx";
        try {
            // Crea el directorio 'data' si no existe.
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                logger.info("Directorio 'data' creado en: {}", dataDir.getAbsolutePath());
            }
            // Escribe la lista de medicamentos en el archivo Excel.
            excelService.writeMedicamentosToExcel(medicamentos, outputFilePath); // Cambiado a Medicamentos
            logger.info("✅ Medicamentos guardados exitosamente en: {}", outputFilePath); // Mensaje actualizado
        } catch (IOException e) {
            logger.error("❌ Error al guardar medicamentos en Excel: {}", e.getMessage(), e); // Mensaje actualizado
        }

        logger.info("\n--- TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE! ---"); // Mensaje actualizado
    }
}
