package com.grafysi.simulator.dbupdate;

import java.util.List;

public interface DbRow {

    public String getTable();

    public String formInsertQuery();

    public List<String> getHeaders();

    public List<String> getValues();

    public String get(String column);
}
