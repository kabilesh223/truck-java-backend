package com.truckmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName    = "GOODS CARRIER";
    private String companyAddress = "";
    private String companyPhone   = "";
    @Column(length = 2000) private String truckList  = "";
    @Column(length = 2000) private String driverList = "";

    public AppSettings() {}
    public AppSettings(Long id, String companyName, String companyAddress,
                       String companyPhone, String truckList, String driverList) {
        this.id = id; this.companyName = companyName;
        this.companyAddress = companyAddress; this.companyPhone = companyPhone;
        this.truckList = truckList != null ? truckList : "";
        this.driverList = driverList != null ? driverList : "";
    }

    public Long getId() { return id; }
    public String getCompanyName() { return companyName != null ? companyName : "GOODS CARRIER"; }
    public void setCompanyName(String v) { this.companyName = v; }
    public String getCompanyAddress() { return companyAddress != null ? companyAddress : ""; }
    public void setCompanyAddress(String v) { this.companyAddress = v; }
    public String getCompanyPhone() { return companyPhone != null ? companyPhone : ""; }
    public void setCompanyPhone(String v) { this.companyPhone = v; }
    public String getTruckList() { return truckList != null ? truckList : ""; }
    public void setTruckList(String v) { this.truckList = v; }
    public String getDriverList() { return driverList != null ? driverList : ""; }
    public void setDriverList(String v) { this.driverList = v; }
}
