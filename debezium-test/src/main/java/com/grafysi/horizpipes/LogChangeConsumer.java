package com.grafysi.horizpipes;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class LogChangeConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>> {

    private static final Logger log = LoggerFactory.getLogger(LogChangeConsumer.class);

    private final AtomicLong batchCounter = new AtomicLong();

    private final AtomicLong recordsProcessed = new AtomicLong(0);

    @Override
    public void handleBatch(List<ChangeEvent<String, String>> records,
                            DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> committer)
            throws InterruptedException {
        log.info("Start processing batch {}", batchCounter.get());
        for (var record: records) {
            log.info("Process record...");
            log.info("Partition: {}", record.partition());
            log.info("Destination {}", record.destination());
            log.info("Header: {}", record.headers());
            log.info("Key: {}", record.key());
            log.info("Value: {}", record.value());
            log.info("---------------------------------------------");
            committer.markProcessed(record);
            recordsProcessed.incrementAndGet();
            Thread.sleep(500);
        }


        committer.markBatchFinished();
        log.info("Batch {} committed", batchCounter.getAndIncrement());
    }

    public long getRecordsProcessed() {
        return recordsProcessed.get();
    }
}

























