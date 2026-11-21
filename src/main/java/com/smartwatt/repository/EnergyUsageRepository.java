package com.smartwatt.repository;

import com.smartwatt.entity.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {

    Optional<EnergyUsage> findByApplianceIdAndUsageDate(Long applianceId, LocalDate usageDate);

    List<EnergyUsage> findByUsageDate(LocalDate usageDate);

    @Query("SELECT e FROM EnergyUsage e WHERE e.usageDate BETWEEN :startDate AND :endDate ORDER BY e.usageDate ASC")
    List<EnergyUsage> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.totalKwh) FROM EnergyUsage e WHERE e.room.id = :roomId")
    Double sumKwhByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT SUM(e.totalKwh) FROM EnergyUsage e")
    Double sumTotalKwh();
}
