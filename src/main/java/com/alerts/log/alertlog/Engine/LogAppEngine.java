package com.alerts.log.alertlog.Engine;

import com.alerts.log.alertlog.Configuration.LogAppConfig;
import com.alerts.log.alertlog.model.Context;
import com.alerts.log.alertlog.model.Event;
import com.alerts.log.alertlog.model.State;
import com.alerts.log.alertlog.model.persistence.Alert;
import com.alerts.log.alertlog.repository.AlertRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class LogAppEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAppEngine.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private LogAppConfig logAppConfig;

    public void parseAndPersistEvents(Context context) {
        // find the matching-STARTED or FINISHED-events.
        Map<String, Event> eventMap = new HashMap<>();

        Map<String, Alert> alerts = new HashMap<>();
        LOGGER.info("Parsing the events and persisting the alerts. This may take a while...");

        try (LineIterator li = FileUtils.lineIterator(new File(context.getLogFilePath()))) {
            String line = null;

            while (li.hasNext()) {
                Event event;
                try {
                    event = new ObjectMapper().readValue(li.nextLine(), Event.class);
                    LOGGER.trace("{}", event);

                    if (eventMap.containsKey(event.getId())) {
                        Event e1 = eventMap.get(event.getId());
                        long executionTime = getEventExecutionTime(event, e1);

                        Alert alert = new Alert(event, Math.toIntExact(executionTime));

                        if (executionTime > logAppConfig.getAlertThresholdMs()) {
                            alert.setAlert(Boolean.TRUE);
                            LOGGER.trace("!!! Execution time for the event {} is {}ms", event.getId(), executionTime);
                        }

                        // add it to the pool of alerts
                        alerts.put(event.getId(), alert);

                        // remove found the matching event
                        eventMap.remove(event.getId());
                    } else {
                        eventMap.put(event.getId(), event);
                    }
                } catch (JsonProcessingException e) {
                    LOGGER.error("Unable to parse the event! {}", e.getMessage());
                }

                // write off the alerts once the pool has enough alerts
                if (alerts.size() > logAppConfig.getTableRowsWriteoffCount()) {
                    persistAlerts(alerts.values());
                    alerts = new HashMap<>();
                }
            } // END while
            if (alerts.size() != 0) {
                persistAlerts(alerts.values());
            }


        } catch (IOException e) {
            LOGGER.error("!!! Unable to access the file: {}", e.getMessage());
        }
    }

    private void persistAlerts(Collection<Alert> alerts) {
        LOGGER.debug("Persisting {} alerts...", alerts.size());
        // alerts.forEach(al -> System.out.println(al.toString())); checking output
        alertRepository.saveAll(alerts);
    }

    private long getEventExecutionTime(Event event1, Event event2) {
        Event endEvent = Stream.of(event1, event2).filter(e -> State.FINISHED.equals(e.getState())).findFirst().orElse(null);
        Event startEvent = Stream.of(event1, event2).filter(e -> State.STARTED.equals(e.getState())).findFirst().orElse(null);
        return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
    }
}
