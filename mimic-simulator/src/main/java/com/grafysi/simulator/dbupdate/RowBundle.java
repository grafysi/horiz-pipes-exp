package com.grafysi.simulator.dbupdate;

import java.util.List;

public interface RowBundle {

    public void addRow(DbRow row);

    public void addRows(DbRow... rows);

    public void addRows(List<DbRow> rows);

    public List<DbRow> getRowsOf(String table);
}
