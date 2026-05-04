package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class ApiKey {

    @Id
    private Integer idApiKey;
    private String keyValue;

    @Enumerated(EnumType.STRING)
    private ApiKeyStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    private ClientAccount client;

    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsageRecord> usageRecords = new ArrayList<>();

    public Integer getIdApiKey() {
        return idApiKey;
    }

    public void setIdApiKey(Integer idApiKey) {
        this.idApiKey = idApiKey;
    }

    public ApiKey() {
    }

    public ApiKey(Integer idApiKey, String keyValue, ApiKeyStatus status, ClientAccount client) {
        this.idApiKey = idApiKey;
        this.keyValue = keyValue;
        this.status = status;
        this.client = client;
        this.createdAt = LocalDateTime.now();
    }


    public void adaugaUsageRecord(UsageRecord u) {
        if (!this.usageRecords.contains(u)) {
            this.usageRecords.add(u);
        }
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public ApiKeyStatus getStatus() {
        return status;
    }

    public void setStatus(ApiKeyStatus status) {
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

    public List<UsageRecord> getUsageRecords() {
        return usageRecords;
    }

    public void setUsageRecords(List<UsageRecord> usageRecords) {
        this.usageRecords = usageRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKey apiKey = (ApiKey) o;
        return Objects.equals(idApiKey, apiKey.idApiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idApiKey);
    }

    @Override
    public String toString() {
        return "ApiKey{" +
                "idApiKey=" + idApiKey +
                ", keyValue='" + keyValue + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", client=" + (client != null ? client.getName() : null) +
                '}';
    }
}
