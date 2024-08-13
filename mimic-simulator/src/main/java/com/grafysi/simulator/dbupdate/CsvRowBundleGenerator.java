package com.grafysi.simulator.dbupdate;

import java.util.NoSuchElementException;

public interface CsvRowBundleGenerator {

    public boolean hasNext();

    public RowBundle next() throws NoSuchElementException;
}
