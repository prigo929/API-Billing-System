package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Plan {

    @Id
    private Integer idPlan;
    private String name; // FREE, PRO, ENTERPRISE
    private Double monthlyFee;
    private Long includedRequestsPerMonth;
    private Double pricePerExtraRequest;

    @OneToMany(mappedBy = "plan")
    private List<RateLimitRule> rateLimitRules = new ArrayList<>();

    @OneToMany(mappedBy = "plan")
    private List<ClientAccount> clients = new ArrayList<>();

    public Plan() {
    }

    public Plan(Integer idPlan, String name, Double monthlyFee,
                Long includedRequestsPerMonth, Double pricePerExtraRequest) {
        this.idPlan = idPlan;
        this.name = name;
        this.monthlyFee = monthlyFee;
        this.includedRequestsPerMonth = includedRequestsPerMonth;
        this.pricePerExtraRequest = pricePerExtraRequest;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(Double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public Long getIncludedRequestsPerMonth() {
        return includedRequestsPerMonth;
    }

    public void setIncludedRequestsPerMonth(Long includedRequestsPerMonth) {
        this.includedRequestsPerMonth = includedRequestsPerMonth;
    }

    public Double getPricePerExtraRequest() {
        return pricePerExtraRequest;
    }

    public void setPricePerExtraRequest(Double pricePerExtraRequest) {
        this.pricePerExtraRequest = pricePerExtraRequest;
    }

    public List<ClientAccount> getClients() {
        return clients;
    }

    public void setClients(List<ClientAccount> clients) {
        this.clients = clients;
    }

    public void adaugaClient(ClientAccount c) {
        if (!this.clients.contains(c)) {
            this.clients.add(c);
        }
    }

    public List<RateLimitRule> getRateLimitRules() {
        return rateLimitRules;
    }

    public void setRateLimitRules(List<RateLimitRule> rateLimitRules) {
        this.rateLimitRules = rateLimitRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return Objects.equals(idPlan, plan.idPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlan);
    }

    @Override
    public String toString() {
        return "Plan{" +
                "idPlan=" + idPlan +
                ", name='" + name + '\'' +
                ", monthlyFee=" + monthlyFee +
                ", includedRequestsPerMonth=" + includedRequestsPerMonth +
                ", pricePerExtraRequest=" + pricePerExtraRequest +
                '}';
    }
}