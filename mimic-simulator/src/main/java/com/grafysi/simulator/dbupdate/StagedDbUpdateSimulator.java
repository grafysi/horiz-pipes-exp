package com.grafysi.simulator.dbupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StagedDbUpdateSimulator implements DbUpdateSimulator {

    private static final Logger log = LoggerFactory.getLogger(StagedDbUpdateSimulator.class);

    private final List<RowBundleLoader> loaders;
    private final List<DbTable> dbTables;
    private final List<String> stageLinkColumns;
    private final Connection connection;
    private final LinkedBlockingQueue<RowBundle> sharedLoadQueue;

    public StagedDbUpdateSimulator(List<DbTable> tables, List<String> stageLinkColumns, Connection connection, int loaderCount) {
        this.dbTables = tables;
        this.stageLinkColumns = stageLinkColumns;
        this.connection = connection;
        this.sharedLoadQueue = new LinkedBlockingQueue<>();
        this.loaders = new LinkedList<>();

        if (loaderCount < 1) throw new IllegalArgumentException("Loader count must greater than 1.");
        for (int i = 0; i < loaderCount; i++) {
            loaders.add(new StagedRowBundleLoader(sharedLoadQueue, connection,
                    dbTables.stream().map(DbTable::getTableName).toList()));
        }
    }

    @Override
    public void run() {
        var generator = new StagedCsvRowBundleGenerator(dbTables, stageLinkColumns);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var loader: loaders) {
                executor.submit(loader);
            }

            executor.shutdown();

            log.info("Start generating row bundles.");
            while (generator.hasNext()) {
                log.info("Try generate new bundle.");
                var bundle = generator.next();
                log.info("New row bundle generated.");
                try {
                    sharedLoadQueue.put(bundle);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // ignore
                    log.error("error", e);
                }
            }

            loaders.forEach(RowBundleLoader::stop);

            if (executor.awaitTermination(10, TimeUnit.SECONDS)) {
                connection.close();
                log.info("All loader have been gracefully stopped.");
                log.info("Database connection closed successfully.");
            }
        } catch (Exception e) {
            log.error("error", e);
        }
    }




}

























