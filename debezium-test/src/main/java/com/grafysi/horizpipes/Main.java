package com.grafysi.horizpipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //runConnectorMultiThreaded();
        runConnectorSingleThreaded();
    }

    private static void runConnector(String name, String... tables) {
        var consumer = new LogChangeConsumer();
        var connector = new DummyPostgresConnector(name, Arrays.asList(tables), consumer);

        try (var es = Executors.newCachedThreadPool()) {
            var start = Instant.now();
            connector.startUsing(es);
            es.shutdown();

            var recordCount = -1L;
            while (recordCount != consumer.getRecordsProcessed()) {
                recordCount = consumer.getRecordsProcessed();
                Thread.sleep(500);
            }

            log.info("============ all processing completed after {} sec",
                    Duration.between(start, Instant.now()).toMillis() / 1000.0);

            Thread.sleep(Long.MAX_VALUE);
            connector.stop();
            if (es.awaitTermination(5, TimeUnit.SECONDS)) {
                log.info("Executor stopped with no remaining workers.");
            } else {
                log.info("Executor stopped with workers remaining.");
            }
        } catch (InterruptedException e) {
            log.error("Error", e);
        }
    }

    private static void runConnectorMultiThreaded() {
        try (var es = Executors.newCachedThreadPool()) {
            es.submit(() -> runConnector("conn-t1", "mimiciv_hosp.omr"));
            es.submit(() -> runConnector("conn-t2", "mimiciv_hosp.microbiologyevents"));
            es.shutdown();

            if (es.awaitTermination(100, TimeUnit.SECONDS)) {
                log.info("Connector MultiThread Test stopped cleanly.");
            } else {
                log.info("Connector MultiThread Test stopped with remaining workers.");
            }
        } catch (InterruptedException e) {
            log.error("Error", e);
        }
    }

    private static void runConnectorSingleThreaded() {
        runConnector("conn-t1", "mimiciv_hosp.patients");
    }
}















