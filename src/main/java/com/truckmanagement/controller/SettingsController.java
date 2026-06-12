package com.truckmanagement.controller;

import com.truckmanagement.dto.SettingsDTO;
import com.truckmanagement.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired private SettingsService settingsService;

    @GetMapping
    public SettingsDTO get() {
        return settingsService.get();
    }

    @PostMapping
    public Map<String, String> save(@RequestBody SettingsDTO dto) {
        settingsService.save(dto);
        return Map.of("message", "Settings saved");
    }
}
