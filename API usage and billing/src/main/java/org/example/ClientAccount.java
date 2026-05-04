package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public class ClientAccount {

    @Id
    private Integer idClient;
    private String name;
    private String email;
    private String companyName;
    private String country;
    private LocalDateTime createdAt;
    private Double creditBalance = 0.0; // Banii disponibili în cont

    @ManyToOne
    private Plan plan;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiKey> apiKeys = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsageRecord> usageRecords = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    public ClientAccount() {
    }

    public ClientAccount(Integer idClient, String name, String email,
                         String companyName, String country, Plan plan) {
        this.idClient = idClient;
        this.name = name;
        this.email = email;
        this.companyName = companyName;
        this.country = country;
        this.plan = plan;
        this.createdAt = LocalDateTime.now();
        this.creditBalance = 0.0;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public List<ApiKey> getApiKeys() {
        return apiKeys;
    }

    public void adaugaApiKey(ApiKey k) {
        if (!this.apiKeys.contains(k)) {
            this.apiKeys.add(k);
        }
    }

    public List<UsageRecord> getUsageRecords() {
        return usageRecords;
    }

    public void adaugaUsageRecord(UsageRecord u) {
        if (!this.usageRecords.contains(u)) {
            this.usageRecords.add(u);
        }
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void adaugaInvoice(Invoice inv) {
        if (!this.invoices.contains(inv)) {
            this.invoices.add(inv);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public void addFunds(Double amount) {
        this.creditBalance += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientAccount that = (ClientAccount) o;
        return Objects.equals(idClient, that.idClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClient);
    }

    @Override
    public String toString() {
        return "ClientAccount{" +
                "idClient=" + idClient +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", country='" + country + '\'' +
                ", plan=" + (plan != null ? plan.getName() : null) +
                '}';
    }
}