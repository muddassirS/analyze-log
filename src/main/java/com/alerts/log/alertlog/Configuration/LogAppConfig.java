package com.alerts.log.alertlog.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.log-alert")
public class LogAppConfig {
    private int alertThresholdMs;
    private int tableRowsWriteoffCount;

    public int getAlertThresholdMs() {
        return alertThresholdMs;
    }

    public void setAlertThresholdMs(int alertThresholdMs) {
        this.alertThresholdMs = alertThresholdMs;
    }

    public int getTableRowsWriteoffCount() {
        return tableRowsWriteoffCount;
    }

    public void setTableRowsWriteoffCount(int tableRowsWriteoffCount) {
        this.tableRowsWriteoffCount = tableRowsWriteoffCount;
    }
}
