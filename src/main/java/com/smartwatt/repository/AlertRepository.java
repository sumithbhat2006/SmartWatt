package com.smartwatt.repository;

import com.smartwatt.entity.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByIsResolvedFalseOrderByTimestampDesc();
    List<Alert> findAllByOrderByTimestampDesc(Pageable pageable);
    long countByIsResolvedFalse();
}
