package com.truckmanagement.controller;

import com.truckmanagement.dto.TripDTO;
import com.truckmanagement.model.Trip;
import com.truckmanagement.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired private TripService tripService;

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "All") String truck,
            @RequestParam(defaultValue = "All") String driver,
            @RequestParam(defaultValue = "") String date_from,
            @RequestParam(defaultValue = "") String date_to) {

        List<Trip> trips = tripService.getFiltered(search, truck, driver, date_from, date_to);
        double totalFreight = trips.stream().mapToDouble(t -> t.getFreight() != null ? t.getFreight() : 0).sum();
        double totalBalance = trips.stream().mapToDouble(t -> t.getBalanceAmount() != null ? t.getBalanceAmount() : 0).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trips", trips);
        result.put("total_freight", Math.round(totalFreight * 100.0) / 100.0);
        result.put("total_balance", Math.round(totalBalance * 100.0) / 100.0);
        return result;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody TripDTO dto) {
        Trip saved = tripService.save(dto);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", saved.getId());
        res.put("message", "Trip saved");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}")
    public Trip get(@PathVariable Long id) {
        return tripService.getFiltered("", "All", "All", "", "").stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trip not found"));
    }

    @PutMapping("/{id}")
    public Map<String, String> update(@PathVariable Long id, @Valid @RequestBody TripDTO dto) {
        tripService.update(id, dto);
        return Map.of("message", "Trip updated");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        tripService.delete(id);
        return Map.of("message", "Trip deleted");
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return tripService.getDashboard();
    }
}
