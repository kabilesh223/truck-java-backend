package com.truckmanagement.dto;

import java.util.List;

public class SettingsDTO {
    private String companyName    = "GOODS CARRIER";
    private String companyAddress = "";
    private String companyPhone   = "";
    private List<String> trucks   = List.of();
    private List<String> drivers  = List.of();

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { this.companyName = v; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String v) { this.companyAddress = v; }
    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String v) { this.companyPhone = v; }
    public List<String> getTrucks() { return trucks; }
    public void setTrucks(List<String> v) { this.trucks = v; }
    public List<String> getDrivers() { return drivers; }
    public void setDrivers(List<String> v) { this.drivers = v; }
}
