package com.grafysi.simulator.dbupdate;

import java.util.Arrays;

public class ColumnSearchRowFilter implements RowFilter {

    private final String searchColumn;

    private final String[] sortedValues;

    public ColumnSearchRowFilter(String searchColumn, String... searchValues) {
        this.searchColumn = searchColumn;
        Arrays.sort(searchValues);
        this.sortedValues = searchValues;
    }

    public boolean test(DbRow dbRow) {
        return Arrays.binarySearch(sortedValues, dbRow.get(searchColumn)) >= 0;
    }


}
