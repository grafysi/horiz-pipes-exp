package com.grafysi.horizpipes;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.debezium.engine.format.KeyValueHeaderChangeEventFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class DummyPostgresConnector {
    private static final Logger log = LoggerFactory.getLogger(DummyPostgresConnector.class);

    private final List<String> includedTables;

    private final String name;

    private Instant lastStarted = null;

    private Instant lastStopped = null;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private DebeziumEngine<ChangeEvent<String, String>> engine;

    private final LogChangeConsumer consumer;

    private final AtomicLong recordsProcessed = new AtomicLong();


    public DummyPostgresConnector(String name, List<String> includedTables, LogChangeConsumer consumer) {
        this.name = name;
        this.includedTables = includedTables;
        this.consumer = consumer;
    }

    public void startUsing(ExecutorService es) {
        if (running.get()) {
            log.info("No action performed. Connector is already running.");
        }

        /*var engine = createEngine(name, includedTables, record -> {
            if (recordsProcessed.incrementAndGet() % 1_000_000 == 0) {
                log.info("Processed {} records after {} sec.",
                        recordsProcessed.get(),
                        Duration.between(lastStarted, Instant.now()).toMillis() / 1000.0);
            }
        });*/

        var engine = createEngine(name, includedTables, consumer);

        this.engine = engine;
        es.submit(engine);
        lastStarted = Instant.now();
    }

    public void stop() {
        try {
            engine.close();
            this.engine = null;
            lastStopped = Instant.now();
        } catch (IOException e) {
            log.error("Failed to stop internal engine", e);
            throw new RuntimeException(e);
        }
    }

    public long getRecordsProcessed() {
        return recordsProcessed.get();
    }

    public double getLastProcessTimeSec() {
        return Duration.between(lastStarted, lastStopped).toMillis() / 1000.0;
    }

    public double getLastProcessRate() {
        return recordsProcessed.get() / getLastProcessTimeSec();
    }

    private DebeziumEngine<ChangeEvent<String, String>> createEngine(
            String connectorName,
            List<String> includedTables, DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>> consumer) {
        var props = defaultProperties();
        props.setProperty("name", connectorName);
        props.setProperty("topic.prefix", connectorName);
        props.setProperty("table.include.list", String.join(",", includedTables));

        return DebeziumEngine.create(KeyValueHeaderChangeEventFormat.of(Json.class, Json.class, Json.class),
                "io.debezium.embedded.async.ConvertingAsyncEngineBuilderFactory")
                .using(props)
                .notifying(consumer)
                .using(new DefaultConnectorCallback(connectorName))
                .build();
    }

    private Properties defaultProperties() {
        var props = new Properties();
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");

        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "5433");
        props.setProperty("database.user", "postgres");
        props.setProperty("database.password", "abcd1234");
        props.setProperty("database.dbname", "mimic4demo");
        props.setProperty("schema.include.list", "mimiciv_hosp");
        props.setProperty("poll.interval.ms", "5000");
        props.setProperty("max.batch.size", "10");
        props.setProperty("record.processing.threads", "1");

        // setup signaling channel
        props.setProperty("signal.data.collection", "public.debezium_signal");

        // setup offset storage
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
        //props.setProperty("offset.storage.file.filename", "/home/nhtri/Documents/dbztest/offsets.dat");
        //props.setProperty("offset.flush.interval.ms", "200");

        // setup snapshot
        props.setProperty("snapshot.mode", "no_data");

        return props;
    }
}




















