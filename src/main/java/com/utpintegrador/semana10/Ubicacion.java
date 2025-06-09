package com.utpintegrador.semana10;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Representa una ubicación con coordenadas geográficas y detalles de dirección.
 * Contiene información como latitud, longitud, distrito y dirección.
 */
public class Ubicacion {
    private double latitud;
    private double longitud;
    private String distrito;
    private String direccion;

    // Constructor vacío
    public Ubicacion() {
        // Valores por defecto o inicialización si es necesario
    }

    /**
     * Constructor para crear una Ubicacion con todos sus atributos.
     *
     * @param latitud   La latitud de la ubicación.
     * @param longitud  La longitud de la ubicación.
     * @param distrito  El distrito de la ubicación.
     * @param direccion La dirección detallada de la ubicación.
     */
    public Ubicacion(double latitud, double longitud, String distrito, String direccion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.distrito = distrito;
        this.direccion = direccion;
    }

    /**
     * Calcula una distancia hipotética.
     * Nota: Para un cálculo de distancia real entre dos puntos geográficos,
     * se necesitarían las coordenadas de ambos puntos y una fórmula como la de Haversine.
     * Este método es un placeholder simple.
     */
    public void calcularDistancia() {
        System.out.println("Calculando distancia para la ubicación: " + this.direccion);
        // Implementación de cálculo de distancia iría aquí.
        // Por ejemplo, para calcular la distancia a otro punto de ubicación,
        // se necesitaría una instancia de otra Ubicacion como parámetro.
    }

    // Getters y Setters
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Implementación de equals usando Google Guava Objects.
     * Dos ubicaciones se consideran iguales si tienen la misma latitud, longitud y dirección.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ubicacion ubicacion = (Ubicacion) obj;
        return Objects.equal(latitud, ubicacion.latitud) &&
               Objects.equal(longitud, ubicacion.longitud) &&
               Objects.equal(direccion, ubicacion.direccion); // La dirección es clave para unicidad
    }

    /**
     * Implementación de hashCode usando Google Guava Objects.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(latitud, longitud, direccion);
    }

    /**
     * Implementación de toString usando Apache Commons ToStringBuilder.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("latitud", latitud)
                .append("longitud", longitud)
                .append("distrito", distrito)
                .append("direccion", direccion)
                .toString();
    }
}
