package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Endpoint {

    @Id
    private Integer idEndpoint;

    private String name;
    private String path;
    private String category;
    private String description;

    @OneToMany(mappedBy = "endpoint")
    private List<UsageRecord> usageRecords = new ArrayList<>();

    public Endpoint() {
    }

    public Endpoint(Integer idEndpoint, String name, String path,
                    String category, String description) {
        this.idEndpoint = idEndpoint;
        this.name = name;
        this.path = path;
        this.category = category;
        this.description = description;
    }

    public Integer getIdEndpoint() {
        return idEndpoint;
    }

    public void setIdEndpoint(Integer idEndpoint) {
        this.idEndpoint = idEndpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(idEndpoint, endpoint.idEndpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEndpoint);
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "idEndpoint=" + idEndpoint +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
