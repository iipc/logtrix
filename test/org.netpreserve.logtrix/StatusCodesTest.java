package org.netpreserve.logtrix;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusCodesTest {

    @Test
    public void test() {
        assertEquals("OK", StatusCodes.describe(200));
    }

}