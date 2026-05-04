package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("WALLET")
public class WalletPayment extends Payment {

    public WalletPayment() {
    }

    public WalletPayment(Integer idPayment, Invoice invoice, Double amount, String method, String transactionId) {
        super(idPayment, invoice, amount, method, transactionId);
    }
}
