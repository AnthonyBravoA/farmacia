package com.utpintegrador.semana10;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.Comparator; // Importar Comparator para las comparaciones funcionales
import java.util.UUID; // Importar UUID para un identificador único

// Implementa la interfaz Comparable para definir un orden natural
public class Medicamento implements Comparable<Medicamento> {
    private UUID idMedicamento;
    private String nombre;
    private String descripcion;
    private double precio; // El tipo de dato en el UML es 'double' para precio
    private LocalDate fechaVencimiento;

    // Constructor sin argumentos, genera automáticamente un identificador único para el medicamento.
    public Medicamento() {
        this.idMedicamento = UUID.randomUUID(); // Se asigna un UUID como identificador único al crearse
    }

    // Constructor que recibe los datos principales del medicamento, excepto el ID (que se genera automáticamente).
    public Medicamento(String nombre, String descripcion, double precio, LocalDate fechaVencimiento) {
        this(); // Llama al constructor sin argumentos para asignar el ID
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Constructor completo que también permite establecer el ID, útil para restaurar objetos desde una base de datos o archivo.
    public Medicamento(UUID idMedicamento, String nombre, String descripcion, double precio, LocalDate fechaVencimiento) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Getters(acceso ) y Setters(modificación )
    public UUID getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(UUID idMedicamento) { this.idMedicamento = idMedicamento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    /**
     * Determina si dos objetos Medicamento son equivalentes.
     * La comparación se basa únicamente en el identificador único (UUID).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Medicamento medicamento = (Medicamento) obj;
        return Objects.equal(idMedicamento, medicamento.idMedicamento);
    }

    /**
     * Genera un valor hash representativo del objeto,
     * considerando únicamente el identificador como base.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(idMedicamento);
    }

    /**
     * Retorna una cadena con la información del objeto,
     * con un formato estilo JSON para facilitar la lectura.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("idMedicamento", idMedicamento)
                .append("nombre", nombre)
                .append("descripcion", descripcion)
                .append("precio", precio)
                .append("fechaVencimiento", fechaVencimiento)
                .toString();
    }

    /**
     * Permite comparar este medicamento con otro para establecer un orden.
     * Primero se compara por nombre (ascendente, permitiendo nulos al inicio),
     * y si los nombres son iguales, se usa la fecha de vencimiento (también ascendente y permitiendo nulos).
     *
     * @param otroMedicamento objeto a comparar con este.
     * @return un número negativo, cero o positivo dependiendo del orden entre ambos objetos.
     */
    @Override
    public int compareTo(Medicamento otroMedicamento) {
        // Compara por nombre, colocando los nulos al principio.
        // Si los nombres son iguales, encadena la comparación por fecha de vencimiento.
        return Comparator.nullsFirst(Comparator.comparing(Medicamento::getNombre))
                         .thenComparing(Comparator.nullsFirst(Comparator.comparing(Medicamento::getFechaVencimiento)))
                         .compare(this, otroMedicamento);
    }
}