package com.utpintegrador.semana10;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de validación de productos Stock
 * Adaptado con Apache Commons y Logback
 */
public class StockValidator {

    private static final Logger logger = LoggerFactory.getLogger(StockValidator.class);

    public StockValidator() {
        logger.debug("StockValidator inicializado");
    }

    /**
     * Valida un producto de stock y retorna errores
     */
    public List<String> validate(Stock stock) {
        logger.debug("Iniciando validación del producto: {}", stock != null ? stock.getId() : "null");

        List<String> errors = new ArrayList<>();

        if (stock == null) {
            errors.add("El producto no puede ser nulo");
            logger.warn("Intento de validar producto nulo");
            return errors;
        }

        // Validar ID
        if (stock.getId() == null || stock.getId() <= 0) {
            errors.add("El ID debe ser un número positivo");
        }

        // Validar farmacia
        if (stock.getFarmaciaId() == null || stock.getFarmaciaId() <= 0) {
            errors.add("El ID de farmacia debe ser válido");
        }

        // Validar medicamento
        if (stock.getMedicamentoId() == null || stock.getMedicamentoId() <= 0) {
            errors.add("El ID de medicamento debe ser válido");
        }

        // Validar precio
        if (stock.getPrecio() == null) {
            errors.add("El precio es obligatorio");
        } else if (stock.getPrecio() < 0) {
            errors.add("El precio no puede ser negativo");
        } else if (stock.getPrecio() > 10000) {
            errors.add("El precio es excesivamente alto, verificar");
            logger.warn("Precio alto detectado: S/{} para producto {}", stock.getPrecio(), stock.getId());
        }

        // Validar cantidad
        if (stock.getCantidad() == null) {
            errors.add("La cantidad es obligatoria");
        } else if (stock.getCantidad() < 0) {
            errors.add("La cantidad no puede ser negativa");
        }

        // Validar disponibilidad
        if (stock.getDisponible() == null) {
            errors.add("El campo 'disponible' es obligatorio");
        }

        if (errors.isEmpty()) {
            logger.debug("Producto {} validado correctamente", stock.getId());
        } else {
            logger.info("Producto {} tiene {} errores de validación", stock.getId(), errors.size());
        }

        return errors;
    }

    /**
     * Retorna si el producto es válido (sin errores)
     */
    public boolean isValid(Stock stock) {
        return validate(stock).isEmpty();
    }

    /**
     * Normaliza los datos del producto (no hay strings, pero se deja para posibles extensiones)
     */
    public void normalizeStock(Stock stock) {
        if (stock == null) return;

        logger.debug("Normalizando datos del producto {}", stock.getId());

        // Aquí podrías implementar futura normalización si añades campos tipo String (como nombre de producto)
    }
}
