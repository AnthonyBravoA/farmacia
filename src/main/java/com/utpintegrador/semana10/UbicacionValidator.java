package com.utpintegrador.semana10;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para validar y normalizar objetos Ubicacion.
 * Asegura la integridad de los datos antes de su uso o persistencia.
 */
public class UbicacionValidator { // Renombrado de PromocionValidator a UbicacionValidator

    private static final Logger logger = LoggerFactory.getLogger(UbicacionValidator.class); // Cambiado a UbicacionValidator

    /**
     * Valida los campos esenciales de una ubicación.
     *
     * @param ubicacion El objeto Ubicacion a validar.
     * @return Una lista de errores; vacía si la ubicación es válida.
     */
    public List<String> validate(Ubicacion ubicacion) { // Cambiado de Promocion a Ubicacion
        List<String> errors = new ArrayList<>();

        // Valida que el objeto ubicación no sea nulo.
        if (ubicacion == null) {
            errors.add("La ubicación no puede ser nula.");
            return errors;
        }

        // Validación de campos obligatorios/básicos para Ubicacion.
        // Latitud
        if (ubicacion.getLatitud() < -90.0 || ubicacion.getLatitud() > 90.0) {
            errors.add("La latitud debe estar entre -90 y 90.");
        }
        // Longitud
        if (ubicacion.getLongitud() < -180.0 || ubicacion.getLongitud() > 180.0) {
            errors.add("La longitud debe estar entre -180 y 180.");
        }
        // Distrito
        if (StringUtils.isBlank(ubicacion.getDistrito())) {
            errors.add("El distrito no puede estar vacío.");
        }
        // Dirección
        if (StringUtils.isBlank(ubicacion.getDireccion())) {
            errors.add("La dirección no puede estar vacía.");
        }
        
        // Registro de advertencia si se encuentran errores.
        if (!errors.isEmpty()) {
            logger.warn("Errores de validación para ubicación '{}': {}", ubicacion.getDireccion(), String.join(", ", errors)); // Usando getDireccion para identificación
        }

        return errors;
    }

    /**
     * Normaliza los datos de texto de la ubicación (ej., elimina espacios en blanco al inicio/final).
     *
     * @param ubicacion La ubicación a normalizar.
     */
    public void normalizeUbicacion(Ubicacion ubicacion) { // Renombrado de normalizePromocion a normalizeUbicacion
        if (ubicacion == null) {
            return; // No se puede normalizar un objeto nulo.
        }

        // Aplica trim() a los campos String si no son nulos.
        if (ubicacion.getDistrito() != null) {
            ubicacion.setDistrito(ubicacion.getDistrito().trim());
        }
        if (ubicacion.getDireccion() != null) {
            ubicacion.setDireccion(ubicacion.getDireccion().trim());
        }
        // Los campos específicos de Promocion (nombre, descripcion, tipoPromocion, codigoPromocional, aplicableA)
        // no son aplicables a Ubicacion, por lo que se eliminan de la normalización.
    }
}