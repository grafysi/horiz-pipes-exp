package com.grafysi.simulator.dbupdate;

import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
public class StandardDbRow implements DbRow {

    private final String table;
    private final Map<String, String> data;

    public StandardDbRow(String table, Map<String, String> data) {
        this.table = table;
        this.data = data;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public List<String> getHeaders() {
        return data.keySet().stream().toList();
    }

    @Override
    public List<String> getValues() {
        return data.values().stream().toList();
    }

    @Override
    public String formInsertQuery() {
        return "INSERT INTO " + getTable() +
                " " + formHeaderTuple() + " " +
                "VALUES " + formValueTuple();
    }

    @Override
    public String get(String column) {
        return data.get(column);
    }

    private String formHeaderTuple() {
        return "(" + String.join(",", getHeaders()) + ")";
    }

    private String formValueTuple() {
        return getValues().stream()
                .map(value -> {
                    if (NumberUtils.isParsable(value)) {
                        return value;
                    } else if (StringUtils.isBlank(value)) {
                        return "null";
                    }
                    else {
                        return "'" + value + "'";
                    }
                })
                .collect(Collectors.joining(",", "(", ")"));
    }
}




















