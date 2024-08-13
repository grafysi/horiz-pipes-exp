package com.grafysi.simulator.dbupdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StagedCsvRowBundleGenerator implements CsvRowBundleGenerator {

    private static final Logger log = LoggerFactory.getLogger(StagedCsvRowBundleGenerator.class);
    private static final int BUNDLE_ROOT_SIZE = 5;

    private final List<DbTable> tables;
    private final List<String> linkColumns;
    private final Queue<RowBundle> resultQueue = new LinkedList<>();
    private boolean hasNext = true;
    private int rootRowsProcessed = 0;
    private final int[] rowCounters;    // for logging

    public StagedCsvRowBundleGenerator(List<DbTable> tables, List<String> linkColumns) {
        if (tables.isEmpty() || tables.size() != 1 + linkColumns.size()) {
            throw new IllegalArgumentException(
                    "Table list must not be empty and linkColumns size " +
                            "equal to table list size minus one.");
        }
        this.tables = tables;
        this.linkColumns = linkColumns;
        this.rowCounters = new int[tables.size()];
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public RowBundle next() {
        var bundle = new DefaultRowBundle();
        List<DbRow> stageRows = null;
        var rowCounts = new int[tables.size()];
        Arrays.fill(rowCounts, 0);
        for (int stageIndex = 0; stageIndex < tables.size(); stageIndex++) {

            if (stageIndex == 0) {
                //log.info("Generator in start 0...");
                stageRows = tables.getFirst().retrieveRows(rootRowsProcessed, BUNDLE_ROOT_SIZE);
                if (stageRows == null) {
                    throw new NoSuchElementException("Can not generate more row bundles.");
                }

                bundle.addRows(stageRows);
                hasNext = stageRows.size() == BUNDLE_ROOT_SIZE;
                rootRowsProcessed += stageRows.size();

                logRowGeneratedInStage(stageIndex, stageRows.size());
            } else {
                //log.info("Generator start stage {}", stageIndex);
                var linkColumn = linkColumns.get(stageIndex - 1);
                var searchValues = stageRows.stream().map(row -> row.get(linkColumn)).toArray(String[]::new);
                var rowFilter = new ColumnSearchRowFilter(linkColumn, searchValues);
                stageRows = tables.get(stageIndex).retrieveRows(rowFilter, 0);
                bundle.addRows(stageRows);

                logRowGeneratedInStage(stageIndex, stageRows.size());
            }
        }
        return bundle;
    }

    private void logRowGeneratedInStage(int stage, int rowsGenerated) {
        rowCounters[stage] += rowsGenerated;
        //log.info("Generator completed stage {}.", stage);
        log.info("Total rows generated in stage {}: {}", stage, rowCounters[stage]);
    }

}



















