package com.example.xxxx.allergytravelcardapp;

/**
 * Test Suite to run all JUnit tests.
 */


        import org.junit.runner.RunWith;
        import org.junit.runners.Suite;
        import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {CardManagerTest.class, CardTest.class})
public class TestSuite {}