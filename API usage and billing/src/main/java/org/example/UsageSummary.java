package org.example;

public class UsageSummary {
    private String clientName;
    private Long totalRequests;
    private Long periodRequests;

    public UsageSummary() {
    }

    public UsageSummary(String clientName, Long totalRequests, Long periodRequests) {
        this.clientName = clientName;
        this.totalRequests = totalRequests;
        this.periodRequests = periodRequests;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getPeriodRequests() {
        return periodRequests;
    }

    public void setPeriodRequests(Long periodRequests) {
        this.periodRequests = periodRequests;
    }

    @Override
    public String toString() {
        return "UsageSummary{" +
                "clientName='" + clientName + '\'' +
                ", totalRequests=" + totalRequests +
                ", periodRequests=" + periodRequests +
                '}';
    }
}
