package com.alerts.log.alertlog.service;

import com.alerts.log.alertlog.Configuration.LogAppConfig;
import com.alerts.log.alertlog.Engine.LogAppEngine;
import com.alerts.log.alertlog.model.Context;
import com.alerts.log.alertlog.validator.LogAppValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogAppService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAppService.class);

    @Autowired
    private LogAppValidator validator;

    @Autowired
    private LogAppEngine manager;

    @Autowired
    private LogAppConfig logAppConfig;

    public void execute(String... args) {
        Context context = Context.getInstance();
        validator.validateInput(context, args);
        manager.parseAndPersistEvents(context);
    }

}
