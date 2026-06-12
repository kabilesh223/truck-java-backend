package com.truckmanagement.repository;

import com.truckmanagement.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t WHERE " +
           "(:search = '' OR LOWER(t.truckNo) LIKE %:search% OR LOWER(t.driverName) LIKE %:search% " +
           "OR LOWER(t.loadingPoint) LIKE %:search% OR LOWER(t.deliveryPoint) LIKE %:search%) AND " +
           "(:truck = 'All' OR UPPER(t.truckNo) = UPPER(:truck)) AND " +
           "(:driver = 'All' OR LOWER(t.driverName) = LOWER(:driver))")
    List<Trip> findWithFilters(
        @Param("search") String search,
        @Param("truck")  String truck,
        @Param("driver") String driver
    );

    List<Trip> findAllByOrderByIdAsc();
}
