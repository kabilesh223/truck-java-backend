package com.truckmanagement.service;

import com.truckmanagement.dto.TripDTO;
import com.truckmanagement.model.Trip;
import com.truckmanagement.repository.TripRepository;
import com.truckmanagement.repository.SettingsRepository;
import com.truckmanagement.model.AppSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

@Service
public class TripService {

    @Autowired private TripRepository tripRepo;
    @Autowired private SettingsRepository settingsRepo;

    public Trip fromDTO(TripDTO dto) {
        Trip t = new Trip();
        t.setDate(dto.getDate());
        t.setTruckNo(dto.getTruckNo() != null ? dto.getTruckNo().toUpperCase().trim() : "");
        t.setDriverName(dto.getDriverName() != null ? toTitleCase(dto.getDriverName()) : "");
        t.setLoadingPoint(dto.getLoadingPoint() != null ? toTitleCase(dto.getLoadingPoint()) : "");
        t.setDeliveryPoint(dto.getDeliveryPoint() != null ? toTitleCase(dto.getDeliveryPoint()) : "");
        t.setWeight(safe(dto.getWeight()));
        t.setFreight(safe(dto.getFreight()));
        t.setToll(safe(dto.getToll()));
        t.setCommission(safe(dto.getCommission()));
        t.setFuelLiters(safe(dto.getFuelLiters()));
        t.setFuelAmount(safe(dto.getFuelAmount()));
        t.setExpenses(safe(dto.getExpenses()));
        t.setAdvance(safe(dto.getAdvance()));
        t.setBillAmount(safe(dto.getBillAmount()));
        t.calculateTotals();
        return t;
    }

    public List<Trip> getFiltered(String search, String truck, String driver,
                                   String dateFrom, String dateTo) {
        String s = search == null ? "" : search.toLowerCase();
        String tk = truck  == null ? "All" : truck;
        String dr = driver == null ? "All" : driver;

        List<Trip> trips = tripRepo.findWithFilters(s, tk, dr);

        // Date filter
        if (dateFrom != null && !dateFrom.isEmpty() && dateTo != null && !dateTo.isEmpty()) {
            trips = trips.stream().filter(t -> {
                try {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    java.time.LocalDate td  = java.time.LocalDate.parse(t.getDate(), fmt);
                    java.time.LocalDate fd  = java.time.LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE);
                    java.time.LocalDate tod = java.time.LocalDate.parse(dateTo,   DateTimeFormatter.ISO_LOCAL_DATE);
                    return !td.isBefore(fd) && !td.isAfter(tod);
                } catch (Exception e) { return true; }
            }).collect(Collectors.toList());
        }
        return trips;
    }

    public Trip save(TripDTO dto) {
        Trip t = fromDTO(dto);
        Trip saved = tripRepo.save(t);
        autoSaveToSettings(dto.getTruckNo(), dto.getDriverName());
        return saved;
    }

    public Trip update(Long id, TripDTO dto) {
        Trip existing = tripRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
        Trip updated = fromDTO(dto);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return tripRepo.save(updated);
    }

    public void delete(Long id) {
        if (!tripRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        tripRepo.deleteById(id);
    }

    public Map<String, Object> getDashboard() {
        List<Trip> trips = tripRepo.findAllByOrderByIdAsc();
        double totalFreight = trips.stream().mapToDouble(t -> safe(t.getFreight())).sum();
        double totalBalance = trips.stream().mapToDouble(t -> safe(t.getBalanceAmount())).sum();
        double totalFuel    = trips.stream().mapToDouble(t -> safe(t.getFuelLiters())).sum();
        long trucksCount    = trips.stream().map(Trip::getTruckNo).filter(Objects::nonNull).distinct().count();

        Map<String, Double> truckFreight = new LinkedHashMap<>();
        Map<String, Long>   monthly      = new LinkedHashMap<>();
        Map<String, Double> driverBal    = new LinkedHashMap<>();

        for (Trip t : trips) {
            String tk = t.getTruckNo() != null ? t.getTruckNo() : "Unknown";
            truckFreight.merge(tk, safe(t.getFreight()), Double::sum);

            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String month = java.time.LocalDate.parse(t.getDate(), fmt)
                        .format(DateTimeFormatter.ofPattern("MMM yyyy"));
                monthly.merge(month, 1L, Long::sum);
            } catch (Exception ignored) {}

            String dr = t.getDriverName() != null ? t.getDriverName() : "Unknown";
            driverBal.merge(dr, safe(t.getBalanceAmount()), Double::sum);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_trips",    trips.size());
        result.put("total_freight",  Math.round(totalFreight * 100.0) / 100.0);
        result.put("total_balance",  Math.round(totalBalance * 100.0) / 100.0);
        result.put("total_fuel",     Math.round(totalFuel    * 100.0) / 100.0);
        result.put("trucks_count",   trucksCount);
        result.put("truck_freight",  truckFreight);
        result.put("monthly",        monthly);
        result.put("driver_bal",     driverBal);
        return result;
    }

    private void autoSaveToSettings(String truck, String driver) {
        settingsRepo.findAll().stream().findFirst().ifPresent(s -> {
            boolean changed = false;
            if (truck != null && !truck.isBlank()) {
                List<String> trucks = new ArrayList<>(Arrays.asList(
                        s.getTruckList().isEmpty() ? new String[0] : s.getTruckList().split(",")));
                String upper = truck.toUpperCase().trim();
                if (!trucks.contains(upper)) { trucks.add(upper); s.setTruckList(String.join(",", trucks)); changed = true; }
            }
            if (driver != null && !driver.isBlank()) {
                List<String> drivers = new ArrayList<>(Arrays.asList(
                        s.getDriverList().isEmpty() ? new String[0] : s.getDriverList().split(",")));
                String title = toTitleCase(driver.trim());
                if (!drivers.contains(title)) { drivers.add(title); s.setDriverList(String.join(",", drivers)); changed = true; }
            }
            if (changed) settingsRepo.save(s);
        });
    }

    private double safe(Double v) { return v == null ? 0 : v; }

    private String toTitleCase(String s) {
        if (s == null || s.isBlank()) return s;
        return Arrays.stream(s.trim().split("\\s+"))
                .map(w -> w.isEmpty() ? w : Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
