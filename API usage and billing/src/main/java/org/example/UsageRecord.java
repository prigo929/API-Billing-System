package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class UsageRecord {

    @Id
    private Integer idUsage;
    private LocalDateTime timestamp;
    private String endpointName;
    private Long requestUnits;
    private Boolean success;
    private String errorCode;

    @ManyToOne
    private Endpoint endpoint;

    @ManyToOne
    private ClientAccount client;

    @ManyToOne
    private ApiKey apiKey;

    public UsageRecord() {
    }

    public UsageRecord(Integer idUsage, ClientAccount client, ApiKey apiKey,
                       String endpointName, Long requestUnits,
                       Boolean success, String errorCode) {
        this.idUsage = idUsage;
        this.client = client;
        this.apiKey = apiKey;
        this.endpointName = endpointName;
        this.requestUnits = requestUnits;
        this.success = success;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public Integer getIdUsage() {
        return idUsage;
    }

    public void setIdUsage(Integer idUsage) {
        this.idUsage = idUsage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public Long getRequestUnits() {
        return requestUnits;
    }

    public void setRequestUnits(Long requestUnits) {
        this.requestUnits = requestUnits;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ClientAccount getClient() {
        return client;
    }

    public void setClient(ClientAccount client) {
        this.client = client;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public void setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsageRecord that = (UsageRecord) o;
        return Objects.equals(idUsage, that.idUsage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsage);
    }

    @Override
    public String toString() {
        return "UsageRecord{" +
                "idUsage=" + idUsage +
                ", client=" + (client != null ? client.getName() : null) +
                ", apiKey=" + (apiKey != null ? apiKey.getIdApiKey() : null) +
                ", endpointName='" + endpointName + '\'' +
                ", requestUnits=" + requestUnits +
                ", success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
