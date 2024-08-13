package com.grafysi.simulator.dbupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class StandardDbRowTest {

    private static final Logger log = LoggerFactory.getLogger(StandardDbRowTest.class);

    private DbRow dbRow;

    @BeforeEach
    public void createDbRow() {
        var data = new HashMap<String, String>();
        data.put("subject_id", "10014729");
        data.put("gender", "F");
        data.put("anchor_age", "21");
        data.put("anchor_year", "2125");
        data.put("anchor_year_group", "2011 - 2013");

        this.dbRow = new StandardDbRow("patients", data);
    }

    @Test
    public void testFormInsertQuery() {
        log.info(dbRow.formInsertQuery());
    }


}
