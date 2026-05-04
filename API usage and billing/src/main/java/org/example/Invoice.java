package org.example;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Invoice {

    @Id
    private Integer idInvoice;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public void adaugaPayment(Payment p) {
        if (!payments.contains(p)) {
            payments.add(p);
        }
    }

    @ManyToOne
    private ClientAccount client;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(Integer idInvoice, ClientAccount client,
                   LocalDate periodStart, LocalDate periodEnd,
                   InvoiceStatus status) {
        this.idInvoice = idInvoice;
        this.client = client;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = 0.0;
    }

    public void adaugaLinie(InvoiceLine line) {
        if (!this.lines.contains(line)) {
            this.lines.add(line);
        }
    }

    public Double getTotalAmount() {
        this.totalAmount = 0.0;
        for (InvoiceLine l : lines) {
            this.totalAmount += l.getLineTotal();
        }
        return this.totalAmount;
    }

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ClientAccount getClient() {
        return client;
    }

    public void setClient(ClientAccount client) {
        this.client = client;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(idInvoice, invoice.idInvoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInvoice);
    }
    @Override
    public String toString() {
        return "Invoice{" +
                "idInvoice=" + idInvoice +
                ", client=" + (client != null ? client.getName() : null) +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", totalAmount=" + getTotalAmount() +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
