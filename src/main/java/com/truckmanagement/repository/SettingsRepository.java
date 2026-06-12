package com.truckmanagement.repository;

import com.truckmanagement.model.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<AppSettings, Long> {
}
