package com.utpintegrador.semana10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clase principal para probar la integración de librerías y funcionalidades de Ubicacion.
 * Demuestra logging, manejo de colecciones, utilidades de String, comparaciones y guardado a Excel.
 */
public class UbicacionDemo { // Renombrado de PromocionDemo a UbicacionDemo

    private static final Logger logger = LoggerFactory.getLogger(UbicacionDemo.class); // Cambiado el logger

    public static void main(String[] args) {
        logger.info("INICIANDO PRUEBAS DE LIBRERÍAS Y CLASE UBICACION."); // Mensaje actualizado

        // --- DEMOSTRACIÓN: LOGBACK (Registro de eventos) ---
        logger.info("\n--- Probando Logback ---");
        logger.error("Logback ERROR: Error al registrar ubicación X.");
        logger.warn("Logback WARN: La ubicación 'Calle Falsa 123' podría tener datos incompletos.");
        logger.info("Logback INFO: Nueva ubicación registrada con éxito.");

        // --- DEMOSTRACIÓN: GUAVA Y CREACIÓN DE UBICACIONES ---
        logger.info("\n--- Probando Guava y creación de Ubicaciones ---"); // Mensaje actualizado
        // Creación de objetos Ubicacion con datos de ejemplo.
        Ubicacion ubicacion1 = new Ubicacion(
            -12.046389, // Latitud de Lima Centro
            -77.030556, // Longitud de Lima Centro
            "Cercado de Lima",
            "Plaza Mayor"
        );

        Ubicacion ubicacion2 = new Ubicacion(
            -12.120000, // Latitud de Miraflores
            -77.028333, // Longitud de Miraflores
            "Miraflores",
            "Parque Kennedy"
        );

        Ubicacion ubicacion3 = new Ubicacion(
            -12.083333, // Latitud de San Isidro
            -77.049444, // Longitud de San Isidro
            "San Isidro",
            "Bosque El Olivar"
        );

        Ubicacion ubicacion4 = new Ubicacion(
            -12.145000, // Latitud de Santiago de Surco
            -76.987000, // Longitud de Santiago de Surco
            "Santiago de Surco",
            "Jockey Plaza"
        );

        // Uso de Guava para crear la lista de ubicaciones.
        List<Ubicacion> ubicaciones = Lists.newArrayList(ubicacion1, ubicacion2, ubicacion3, ubicacion4); // Cambiado a Ubicacion
        logger.info("Guava: Se creó una lista de {} ubicaciones.", ubicaciones.size()); // Mensaje actualizado
        
        logger.info("\n--- Ubicaciones en orden de creación ---"); // Mensaje actualizado
        ubicaciones.forEach(u -> logger.info("  - Dirección: '{}', Distrito: '{}'", u.getDireccion(), u.getDistrito()));


        // --- DEMOSTRACIÓN: COMPARATOR (Orden Personalizado) ---
        // La clase Ubicacion no implementa Comparable directamente, usamos Comparators.
        logger.info("\n--- Demostrando Comparator (Orden Personalizado: Distrito ascendente) ---"); // Mensaje actualizado
        // Ordena por el nombre del distrito de forma ascendente.
        Comparator<Ubicacion> porDistritoAscendente = (uA, uB) -> {
            return Comparator.nullsFirst(Comparator.comparing(Ubicacion::getDistrito, String.CASE_INSENSITIVE_ORDER)) // Comparación sin distinguir mayúsculas/minúsculas
                             .compare(uA, uB);
        };
        Collections.sort(ubicaciones, porDistritoAscendente);
        ubicaciones.forEach(u -> logger.info("  - Distrito: '{}', Dirección: '{}'", u.getDistrito(), u.getDireccion()));


        logger.info("\n--- Demostrando Otro Comparator (Orden Personalizado: Latitud descendente) ---"); // Mensaje actualizado
        // Ordena por latitud de forma descendente (de norte a sur).
        Comparator<Ubicacion> porLatitudDescendente = (uA, uB) -> {
            return Double.compare(uB.getLatitud(), uA.getLatitud()); // b - a para orden descendente
        };
        Collections.sort(ubicaciones, porLatitudDescendente);
        ubicaciones.forEach(u -> logger.info("  - Latitud: {:.6f}, Dirección: '{}'", u.getLatitud(), u.getDireccion()));


        // --- DEMOSTRACIÓN: APACHE COMMONS LANG3 (Utilidades de String) ---
        logger.info("\n--- Probando Apache Commons Lang3 ---");
        // Convierte la dirección a mayúsculas.
        String direccionMayusculas = StringUtils.upperCase(ubicacion1.getDireccion()); // Usando getDireccion
        logger.info("Commons Lang3: Dirección en mayúsculas: {}", direccionMayusculas); // Mensaje actualizado

        // Trunca el distrito si es muy largo.
        String distritoTruncado = StringUtils.abbreviate(ubicacion2.getDistrito(), 5); // Usando getDistrito
        logger.info("Commons Lang3: Distrito truncado: '{}'", distritoTruncado); // Mensaje actualizado

        // Verifica si la dirección de una ubicación está vacía o nula.
        // Simulamos una ubicación con dirección vacía para la demostración
        ubicacion3.setDireccion(""); 
        if (StringUtils.isBlank(ubicacion3.getDireccion())) {
            logger.info("Commons Lang3: La dirección del distrito '{}' está vacía/nula, como se esperaba (ejemplo).", ubicacion3.getDistrito()); // Mensaje actualizado
        } else {
            logger.info("Commons Lang3: La dirección del distrito '{}' NO está vacía/nula.", ubicacion3.getDistrito()); // Mensaje actualizado
        }


        // --- DEMOSTRACIÓN: GUARDAR UBICACIONES EN EXCEL ---
        logger.info("\n--- Guardando ubicaciones a un archivo Excel ---"); // Mensaje actualizado
        UbicacionExcelService excelService = new UbicacionExcelService(); // Usando el nuevo servicio ExcelService
        String outputFilePath = "data/ubicaciones_generadas_" + // Nombre de archivo actualizado
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                                ".xlsx";
        try {
            // Crea el directorio 'data' si no existe.
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                logger.info("Directorio 'data' creado en: {}", dataDir.getAbsolutePath());
            }
            // Escribe la lista de ubicaciones en el archivo Excel.
            excelService.writeUbicacionesToExcel(ubicaciones, outputFilePath); // Llamada al método de escritura de Ubicaciones
            logger.info("✅ Ubicaciones guardadas exitosamente en: {}", outputFilePath); // Mensaje actualizado
        } catch (IOException e) {
            logger.error("❌ Error al guardar ubicaciones en Excel: {}", e.getMessage(), e); // Mensaje actualizado
        }

        logger.info("\n--- TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE! ---"); // Mensaje actualizado
    }
}