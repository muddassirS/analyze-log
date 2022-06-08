package com.alerts.log.alertlog.repository;

import com.alerts.log.alertlog.model.persistence.Alert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends CrudRepository<Alert, String> {
}
