package com.wright.paul.allergytravelcardapp;

/**
 * Test Suite to run all Espresso UI tests.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all instrumentation tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateCardActivityTest.class, MainActivityTest.class
})

public class EspressoTestSuite {
}