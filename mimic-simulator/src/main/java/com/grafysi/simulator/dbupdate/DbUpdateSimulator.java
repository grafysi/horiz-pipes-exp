package com.grafysi.simulator.dbupdate;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public interface DbUpdateSimulator extends Runnable {

    public class Builder {
        private List<DbTable> tables = new ArrayList<>();
        private List<String> stagedLinkColumns = new ArrayList<>();
        private int loaderCount = 1;
        private String jdbcUrl;
        private Properties jdbcProperties;

        public Builder withTable(DbTable table, String stageLinkColumn) {
            tables.add(table);
            stagedLinkColumns.add(stageLinkColumn);
            return this;
        }

        public Builder withRootTable(DbTable table) {
            tables.addFirst(table);
            return this;
        }

        public Builder withRootTable(String name, URL csvUrl) {
            try {
                var table = createTable(name, csvUrl);
                tables.addFirst(table);
                return this;
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid csv url: " + csvUrl, e);
            }
        }

        public Builder withTable(String name, URL csvUrl, String stageLinkColumn) {
            try {
                var table = createTable(name, csvUrl);
                tables.add(table);
                stagedLinkColumns.add(stageLinkColumn);
                return this;
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid csv url: " + csvUrl, e);
            }
        }

        private DbTable createTable(String name, URL csvUrl) throws URISyntaxException{
            var csvFile = new File(csvUrl.toURI());
            return new CsvDbTable(name, csvFile);
        }

        public Builder withJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }

        public Builder withJdbcProperty(String key, String value) {
            if (jdbcProperties == null) {
                jdbcProperties = new Properties();
            }
            jdbcProperties.setProperty(key, value);
            return this;
        }

        public Builder withJdbcProperties(Properties properties) {
            this.jdbcProperties = properties;
            return this;
        }

        public Builder withLoaderCount(int loaderCount) {
            this.loaderCount = loaderCount;
            return this;
        }

        public DbUpdateSimulator build() {
            try {
                var connection = DriverManager.getConnection(jdbcUrl, jdbcProperties);
                return new StagedDbUpdateSimulator(
                        tables, stagedLinkColumns, connection, loaderCount);
            } catch (SQLException e) {
                throw new IllegalArgumentException("Invalid sql connection config.");
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}


























