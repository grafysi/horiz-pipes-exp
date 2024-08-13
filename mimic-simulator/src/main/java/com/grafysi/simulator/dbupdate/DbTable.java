package com.grafysi.simulator.dbupdate;

import java.util.List;

public interface DbTable {

    public String getTableName();

    public List<DbRow> retrieveRows(RowFilter filter, long offset, long limit);

    public List<DbRow> retrieveRows(long offset, long limit);

    public List<DbRow> retrieveRows(RowFilter filter, long offset);
}
