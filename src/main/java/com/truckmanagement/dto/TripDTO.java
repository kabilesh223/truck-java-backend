package com.truckmanagement.dto;

public class TripDTO {
    private String date;
    private String truckNo;
    private String driverName;
    private String loadingPoint;
    private String deliveryPoint;
    private Double weight     = 0.0;
    private Double freight    = 0.0;
    private Double toll       = 0.0;
    private Double commission = 0.0;
    private Double fuelLiters = 0.0;
    private Double fuelAmount = 0.0;
    private Double expenses   = 0.0;
    private Double advance    = 0.0;
    private Double billAmount = 0.0;

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
}
