package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class InvoiceLine {

    @Id
    private Integer idLine;

    private String description;
    private Long quantity;
    private Double unitPrice;

    @ManyToOne
    private Invoice invoice;

    public InvoiceLine() {
    }

    public InvoiceLine(Integer idLine, Invoice invoice,
                       String description, Long quantity, Double unitPrice) {
        this.idLine = idLine;
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return this.quantity * this.unitPrice;
    }

    public Integer getIdLine() {
        return idLine;
    }

    public void setIdLine(Integer idLine) {
        this.idLine = idLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceLine that = (InvoiceLine) o;
        return Objects.equals(idLine, that.idLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLine);
    }

    @Override
    public String toString() {
        return "InvoiceLine{" +
                "idLine=" + idLine +
                ", invoice=" + (invoice != null ? invoice.getIdInvoice() : null) +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + getLineTotal() +
                '}';
    }
}
