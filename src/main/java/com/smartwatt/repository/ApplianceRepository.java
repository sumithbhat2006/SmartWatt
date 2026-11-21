package com.smartwatt.repository;

import com.smartwatt.entity.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplianceRepository extends JpaRepository<Appliance, Long> {
    List<Appliance> findByRoomId(Long roomId);
    List<Appliance> findByIsOnTrue();

    @Query("SELECT COUNT(a) FROM Appliance a WHERE a.isOn = true")
    long countActiveAppliances();

    @Query("SELECT a FROM Appliance a ORDER BY a.currentWattage DESC")
    List<Appliance> findTopConsumingAppliances();
}
