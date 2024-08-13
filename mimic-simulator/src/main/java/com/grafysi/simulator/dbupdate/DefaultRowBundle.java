package com.grafysi.simulator.dbupdate;

import java.util.*;

public class DefaultRowBundle implements RowBundle {

    private final Map<String, List<DbRow>> tData;

    public DefaultRowBundle() {
        this.tData = new HashMap<>();
    }

    @Override
    public void addRow(DbRow row) {
        if (!tData.containsKey(row.getTable())) {
            tData.put(row.getTable(), new LinkedList<DbRow>());
        }
        tData.get(row.getTable()).add(row);
    }

    @Override
    public void addRows(DbRow... rows) {
        Arrays.stream(rows).forEach(this::addRow);
    }

    @Override
    public void addRows(List<DbRow> rows) {
        rows.forEach(this::addRow);
    }

    @Override
    public List<DbRow> getRowsOf(String table) {
        return tData.get(table);
    }
}

























