package com.grafysi.simulator.dbupdate;

@FunctionalInterface
public interface RowFilter {

    public boolean test(DbRow r);
}
