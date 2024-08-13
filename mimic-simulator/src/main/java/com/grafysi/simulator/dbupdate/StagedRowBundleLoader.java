package com.grafysi.simulator.dbupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class StagedRowBundleLoader implements RowBundleLoader {
    private static final Logger log = LoggerFactory.getLogger(StagedRowBundleLoader.class);

    private final LinkedBlockingQueue<RowBundle> loadQueue;
    private final Connection connection;
    private final List<String> stagedTables;
    private Thread currentThread;

    public StagedRowBundleLoader(LinkedBlockingQueue<RowBundle> loadQueue, Connection connection, List<String> stagedTables) {
        this.loadQueue = loadQueue;
        this.connection = connection;
        this.stagedTables = stagedTables;
    }

    @Override
    public void run() {
        if (currentThread == null) {
            currentThread = Thread.currentThread();
        } else {
            throw new RuntimeException("The stopped loader cannot be reused. Please create new loader.");
        }

        try (var stm = connection.createStatement()) {
            while (true) {
                var rowBundle = loadQueue.take();
                for (var stagedTable: stagedTables) {
                    var rows = rowBundle.getRowsOf(stagedTable);
                    rows.forEach(row -> {
                        var query = row.formInsertQuery();
                        try {
                            stm.executeUpdate(query);
                        } catch (SQLException e) {
                            log.error("Sql error: {}.", query, e);
                        }
                    });
                }
            }
        } catch (InterruptedException e) {
            // ignore
        } catch (SQLException e) {
            log.error("Error when creating sql statement", e);
        }
    }

    @Override
    public void stop() {
        if (currentThread == null) {
            throw new IllegalStateException("This loader is not started before calling stop.");
        }
        currentThread.interrupt();
    }

}




























