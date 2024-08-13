package com.grafysi.simulator.dbupdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class DbUpdateSimulatorTest {

    private static final Logger log = LoggerFactory.getLogger(DbUpdateSimulatorTest.class);
    private DbUpdateSimulator simulator;

    @BeforeEach
    public void prepareTests() {
        simulator = DbUpdateSimulator.builder()
                .withRootTable("mimiciv_hosp.patients", resource("patients.csv"))
                .withTable("mimiciv_hosp.admissions",
                        resource("admissions.csv"), "subject_id")
                .withTable("mimiciv_hosp.transfers",
                        resource("transfers.csv"), "hadm_id")
                .withJdbcUrl("jdbc:postgresql://localhost:5433/mimic4demo")
                .withJdbcProperty("user", "postgres")
                .withJdbcProperty("password", "abcd1234")
                .build();
    }

    private URL resource(String name) {
        return getClass().getClassLoader().getResource(name);
    }

    @Test
    protected void testLoadingMimic4() {
        simulator.run();
    }
}























