package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BANK_TRANSFER")
public class BankTransferPayment extends Payment {
    private String iban;
    private String bankName;

    public BankTransferPayment() {
    }

    public BankTransferPayment(Integer idPayment, Invoice invoice, Double amount, String method, String transactionId, String iban, String bankName) {
        super(idPayment, invoice, amount, method, transactionId);
        this.iban = iban;
        this.bankName = bankName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}