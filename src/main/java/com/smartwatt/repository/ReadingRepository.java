package com.smartwatt.repository;

import com.smartwatt.entity.Reading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findByApplianceIdOrderByRecordedAtDesc(Long applianceId, Pageable pageable);

    @Query("SELECT r FROM Reading r ORDER BY r.recordedAt DESC")
    List<Reading> findRecentReadings(Pageable pageable);

    @Query("SELECT r FROM Reading r WHERE r.appliance.room.id = :roomId ORDER BY r.recordedAt DESC")
    List<Reading> findRecentReadingsByRoom(@Param("roomId") Long roomId, Pageable pageable);
}
