package com.truckmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String truckNo;
    private String driverName;
    private String loadingPoint;
    private String deliveryPoint;

    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double weight = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double freight = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double toll = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double commission = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double fuelLiters = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double fuelAmount = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double expenses = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double advance = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double billAmount = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double totalTripAmount = 0.0;
    @Column(columnDefinition = "DOUBLE DEFAULT 0") private Double balanceAmount = 0.0;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Trip() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        calculateTotals();
    }

    @PreUpdate
    public void preUpdate() { calculateTotals(); }

    public void calculateTotals() {
        double total = (s(freight) + s(toll) + s(commission) + s(fuelAmount) + s(expenses))
                     - (s(billAmount) + s(advance));
        this.totalTripAmount = Math.round(total * 100.0) / 100.0;
        this.balanceAmount   = this.totalTripAmount; // same value, kept for Excel report
    }

    private double s(Double v) { return v == null ? 0 : v; }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String v) { this.date = v; }
    public String getTruckNo() { return truckNo; }
    public void setTruckNo(String v) { this.truckNo = v; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String v) { this.driverName = v; }
    public String getLoadingPoint() { return loadingPoint; }
    public void setLoadingPoint(String v) { this.loadingPoint = v; }
    public String getDeliveryPoint() { return deliveryPoint; }
    public void setDeliveryPoint(String v) { this.deliveryPoint = v; }
    public Double getWeight() { return weight; }
    public void setWeight(Double v) { this.weight = v; }
    public Double getFreight() { return freight; }
    public void setFreight(Double v) { this.freight = v; }
    public Double getToll() { return toll; }
    public void setToll(Double v) { this.toll = v; }
    public Double getCommission() { return commission; }
    public void setCommission(Double v) { this.commission = v; }
    public Double getFuelLiters() { return fuelLiters; }
    public void setFuelLiters(Double v) { this.fuelLiters = v; }
    public Double getFuelAmount() { return fuelAmount; }
    public void setFuelAmount(Double v) { this.fuelAmount = v; }
    public Double getExpenses() { return expenses; }
    public void setExpenses(Double v) { this.expenses = v; }
    public Double getAdvance() { return advance; }
    public void setAdvance(Double v) { this.advance = v; }
    public Double getBillAmount() { return billAmount; }
    public void setBillAmount(Double v) { this.billAmount = v; }
    public Double getTotalTripAmount() { return totalTripAmount; }
    public void setTotalTripAmount(Double v) { this.totalTripAmount = v; }
    public Double getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(Double v) { this.balanceAmount = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
