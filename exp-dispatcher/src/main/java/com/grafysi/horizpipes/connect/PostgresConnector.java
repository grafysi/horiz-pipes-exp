package com.grafysi.horizpipes.connect;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.debezium.engine.format.KeyValueHeaderChangeEventFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostgresConnector {

    private static final Logger log = LoggerFactory.getLogger(PostgresConnector.class);

    private final BlockingQueue<ChangeEvent<String, String>> eventQueue;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public PostgresConnector(Properties properties,
                             BlockingQueue<ChangeEvent<String, String>> eventQueue) {
        this.eventQueue = eventQueue;
    }

    private DebeziumEngine<ChangeEvent<String, String>> createDebeziumEngine(Properties properties) {
        return DebeziumEngine.create(KeyValueHeaderChangeEventFormat.of(Json.class, Json.class, Json.class),
                        "io.debezium.embedded.async.ConvertingAsyncEngineBuilderFactory")
                .using(properties)
                .notifying(getChangeConsumer())
                .build();
    }

    private DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>> getChangeConsumer() {
        return null;
    }

    public void start() {

    }

}
























