package com.utpintegrador.semana10;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Stock {

    private Integer id;
    private Integer farmaciaId;
    private Integer medicamentoId;
    private Double precio;
    private Integer cantidad;
    private Boolean disponible;

    // Constructor vacío
    public Stock() {}

    // Constructor completo
    public Stock(Integer id, Integer farmaciaId, Integer medicamentoId, Double precio, Integer cantidad, Boolean disponible) {
        this.id = id;
        this.farmaciaId = farmaciaId;
        this.medicamentoId = medicamentoId;
        this.precio = precio;
        this.cantidad = cantidad;
        this.disponible = disponible;
    }

    /**
     * Valida si los datos del stock son correctos
     */
    public boolean isValid() {
        return farmaciaId != null && farmaciaId > 0 &&
               medicamentoId != null && medicamentoId > 0 &&
               precio != null && precio >= 0 &&
               cantidad != null && cantidad >= 0 &&
               disponible != null;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFarmaciaId() { return farmaciaId; }
    public void setFarmaciaId(Integer farmaciaId) { this.farmaciaId = farmaciaId; }

    public Integer getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Integer medicamentoId) { this.medicamentoId = medicamentoId; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    // Equals usando Google Guava
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equal(id, stock.id) &&
               Objects.equal(farmaciaId, stock.farmaciaId) &&
               Objects.equal(medicamentoId, stock.medicamentoId);
    }

    // HashCode con Guava
    @Override
    public int hashCode() {
        return Objects.hashCode(id, farmaciaId, medicamentoId);
    }

    // toString elegante
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("farmaciaId", farmaciaId)
                .append("medicamentoId", medicamentoId)
                .append("precio", precio)
                .append("cantidad", cantidad)
                .append("disponible", disponible)
                .toString();
    }
}
