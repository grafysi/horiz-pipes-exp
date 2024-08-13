package com.grafysi.simulator.dbupdate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvDbTable implements DbTable {

    private final String tableName;
    private final File csvFile;
    private long rowCursor;
    private long rowsMax = Long.MAX_VALUE;
    private Reader csvIn;
    private Iterator<CSVRecord> recordIterator;

    public CsvDbTable(String tableName, String csvPath) {
        this(tableName, new File(csvPath));
    }

    public CsvDbTable(String tableName, File csvFile) {
        this.tableName = tableName;
        this.csvFile = csvFile;

        try {
            rewindCursor();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid csv path.");
        }
    }



    private void rewindCursor() throws IOException {
        rowCursor = 0;
        if (csvIn != null) {
            csvIn.close();
        }
        this.csvIn = new FileReader(csvFile);
        var csvFormat = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        this.recordIterator = csvFormat.parse(csvIn).iterator();
    }

    private void rewindCursor(long toOffset) throws IOException {
        rewindCursor();
        for (int i = 0; i < toOffset; i++) {
            nextRecord();
        }
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    private CSVRecord nextRecord() {
        rowCursor++;
        if (!recordIterator.hasNext()) {
            this.rowsMax = rowCursor;
            return null;
        }
        return recordIterator.next();
    }

    @Override
    public List<DbRow> retrieveRows(RowFilter filter, long offset, long limit) {

        if (offset >= rowsMax) {
            return null;
        }

        if (offset < rowCursor) {
            try {
                rewindCursor(offset);
            } catch (IOException e) {
                throw new RuntimeException("Record cursor cannot be rewound");
            }
        }

        var result = new ArrayList<DbRow>();
        CSVRecord csvRecord;
        while ((csvRecord = nextRecord()) != null && result.size() < limit) {
            var dbRow = new StandardDbRow(tableName, csvRecord.toMap());
            if (filter.test(dbRow)) {
                result.add(dbRow);
            }
        }
        return result;
    }

    @Override
    public List<DbRow> retrieveRows(long offset, long limit) {
        return retrieveRows((row) -> true, offset, limit);
    }

    @Override
    public List<DbRow> retrieveRows(RowFilter filter, long offset) {
        return retrieveRows(filter, offset, Long.MAX_VALUE);
    }
}




















