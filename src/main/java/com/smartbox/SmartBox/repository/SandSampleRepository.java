package com.smartbox.SmartBox.repository;

import com.smartbox.SmartBox.model.SandSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SandSampleRepository extends JpaRepository<SandSample, Long> {
    // You can add custom queries if needed, e.g.:
    // List<SandSample> findByBeachName(String beachName);
}
