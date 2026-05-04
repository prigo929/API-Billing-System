package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PricingTier {
    @Id
    private Integer idTier;

    private Long minUnits;      // ex: 10,000 unități
    private Long maxUnits;      // ex: 50,000 unități (null pt infinit)
    private Double unitPrice;   // ex: 0.0004 (mai ieftin decât standard)

    @ManyToOne
    private Plan plan;


    public PricingTier() {}
    
    public Integer getIdTier() {
        return idTier;
    }

    public void setIdTier(Integer idTier) {
        this.idTier = idTier;
    }

    public Long getMinUnits() {
        return minUnits;
    }

    public void setMinUnits(Long minUnits) {
        this.minUnits = minUnits;
    }

    public Long getMaxUnits() {
        return maxUnits;
    }

    public void setMaxUnits(Long maxUnits) {
        this.maxUnits = maxUnits;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}