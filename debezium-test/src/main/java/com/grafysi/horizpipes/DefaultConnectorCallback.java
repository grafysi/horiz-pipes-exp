package com.grafysi.horizpipes;

import io.debezium.engine.DebeziumEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConnectorCallback implements DebeziumEngine.ConnectorCallback {
    private static final Logger log = LoggerFactory.getLogger(DefaultConnectorCallback.class);

    private final String connectorName;

    public DefaultConnectorCallback(String connectorName) {
        this.connectorName = connectorName;
    }

    @Override
    public void connectorStarted() {
        log.info("Connector started: {}", connectorName);
    }

    @Override
    public void connectorStopped() {
        log.info("Connector stopped: {}", connectorName);
    }

    @Override
    public void taskStarted() {
        log.info("Task started: of: {}", connectorName);
    }

    @Override
    public void taskStopped() {
        log.info("Task stopped: of: {}", connectorName);
    }
}
