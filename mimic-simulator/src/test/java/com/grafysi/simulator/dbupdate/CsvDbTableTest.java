package com.grafysi.simulator.dbupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvDbTableTest {
    private static final Logger log = LoggerFactory.getLogger(CsvDbTableTest.class);

    private DbTable dbTable;

    @BeforeEach
    public void setupDbTable() throws Exception {
        var url = getClass().getClassLoader().getResource("admissions.csv");
        assertNotNull(url);
        var csvFile = new File(url.toURI());
        this.dbTable = new CsvDbTable("admissions", csvFile);
    }

    @Test
    public void testReadAdmissionOfAPatient() {
        var rowFilter = new ColumnSearchRowFilter("subject_id", "10004235");
        var admissions = dbTable.retrieveRows(rowFilter, 0, Long.MAX_VALUE);
        admissions.forEach(r -> log.info(r.toString()));
    }

}


































