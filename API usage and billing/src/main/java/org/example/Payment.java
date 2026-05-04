package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type", discriminatorType = DiscriminatorType.STRING)
public class Payment {

    @Id
    private Integer idPayment;

    private Double amount;
    private LocalDateTime paymentDate;
    private String method;
    private String transactionId;

    @ManyToOne
    private Invoice invoice;

    public Payment() {
    }

    public Payment(Integer idPayment, Invoice invoice,
                   Double amount, String method, String transactionId) {
        this.idPayment = idPayment;
        this.invoice = invoice;
        this.amount = amount;
        this.method = method;
        this.transactionId = transactionId;
        this.paymentDate = LocalDateTime.now();
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}