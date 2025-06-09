package com.utpintegrador.semana10;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para validar y normalizar objetos Medicamento.
 * Asegura la integridad de los datos antes de su uso o persistencia.
 */
public class MedicamentoValidator {

    private static final Logger logger = LoggerFactory.getLogger(MedicamentoValidator.class);

    /**
     * Valida los campos esenciales de un medicamento.
     *
     * @param medicamento El objeto Medicamento a validar.
     * @return Una lista de errores; vacía si el medicamento es válido.
     */
    public List<String> validate(Medicamento medicamento) {
        List<String> errors = new ArrayList<>();

        // Valida que el objeto medicamento no sea nulo.
        if (medicamento == null) {
            errors.add("El medicamento no puede ser nulo.");
            return errors;
        }

        // Validación de campos obligatorios/básicos.
        if (medicamento.getIdMedicamento() == null) {
            errors.add("ID de medicamento no puede ser nulo.");
        }
        if (StringUtils.isBlank(medicamento.getNombre())) {
            errors.add("El nombre del medicamento no puede estar vacío.");
        }
        if (StringUtils.isBlank(medicamento.getDescripcion())) {
            errors.add("La descripción del medicamento no puede estar vacía.");
        }
        // Precio debe ser positivo
        if (medicamento.getPrecio() <= 0) {
            errors.add("El precio del medicamento debe ser un número positivo.");
        }
        if (medicamento.getFechaVencimiento() == null) {
            errors.add("La fecha de vencimiento no puede ser nula.");
        } else if (medicamento.getFechaVencimiento().isBefore(LocalDate.now())) {
            errors.add("La fecha de vencimiento no puede ser en el pasado.");
        }
        
        // Registro de advertencia si se encuentran errores.
        if (!errors.isEmpty()) {
            logger.warn("Errores de validación para medicamento '{}': {}", medicamento.getNombre(), String.join(", ", errors));
        }

        return errors;
    }

    /**
     * Normaliza los datos de texto del medicamento (ej., elimina espacios en blanco al inicio/final).
     *
     * @param medicamento El medicamento a normalizar.
     */
    public void normalizeMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            return; // No se puede normalizar un objeto nulo.
        }

        // Aplica trim() a los campos String si no son nulos.
        if (medicamento.getNombre() != null) {
            medicamento.setNombre(medicamento.getNombre().trim());
        }
        if (medicamento.getDescripcion() != null) {
            medicamento.setDescripcion(medicamento.getDescripcion().trim());
        }
    }
}