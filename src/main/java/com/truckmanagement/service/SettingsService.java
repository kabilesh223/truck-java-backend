package com.truckmanagement.service;

import com.truckmanagement.dto.SettingsDTO;
import com.truckmanagement.model.AppSettings;
import com.truckmanagement.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SettingsService {

    @Autowired private SettingsRepository repo;

    public SettingsDTO get() {
        AppSettings s = repo.findAll().stream().findFirst()
                .orElse(new AppSettings(null, "GOODS CARRIER", "", "", "", ""));
        SettingsDTO dto = new SettingsDTO();
        dto.setCompanyName(s.getCompanyName());
        dto.setCompanyAddress(s.getCompanyAddress());
        dto.setCompanyPhone(s.getCompanyPhone());
        String t = s.getTruckList();
        String d = s.getDriverList();
        dto.setTrucks(t.isEmpty() ? List.of() : Arrays.asList(t.split(",")));
        dto.setDrivers(d.isEmpty() ? List.of() : Arrays.asList(d.split(",")));
        return dto;
    }

    public void save(SettingsDTO dto) {
        AppSettings s = repo.findAll().stream().findFirst()
                .orElse(new AppSettings(null, "GOODS CARRIER", "", "", "", ""));
        s.setCompanyName(dto.getCompanyName());
        s.setCompanyAddress(dto.getCompanyAddress());
        s.setCompanyPhone(dto.getCompanyPhone());
        s.setTruckList(dto.getTrucks() == null ? "" : String.join(",", dto.getTrucks()));
        s.setDriverList(dto.getDrivers() == null ? "" : String.join(",", dto.getDrivers()));
        repo.save(s);
    }}
