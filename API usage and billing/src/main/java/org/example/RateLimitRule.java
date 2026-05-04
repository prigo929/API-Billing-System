package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class RateLimitRule {

    @Id
    private Integer idRule;

    private String scope; // "GLOBAL" sau "ENDPOINT"
    private Long maxRequests;
    private Long perSeconds;

    @ManyToOne
    private Plan plan;

    @ManyToOne
    private Endpoint endpoint; // poate fi null dacă scope = GLOBAL

    public RateLimitRule() {
    }

    public Integer getIdRule() {
        return idRule;
    }

    public void setIdRule(Integer idRule) {
        this.idRule = idRule;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimitRule that = (RateLimitRule) o;
        return Objects.equals(idRule, that.idRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRule);
    }

    @Override
    public String toString() {
        return "RateLimitRule{" +
                "idRule=" + idRule +
                ", scope='" + scope + '\'' +
                ", maxRequests=" + maxRequests +
                ", perSeconds=" + perSeconds +
                ", plan=" + (plan != null ? plan.getName() : null) +
                ", endpoint=" + (endpoint != null ? endpoint.getPath() : null) +
                '}';
    }
}
