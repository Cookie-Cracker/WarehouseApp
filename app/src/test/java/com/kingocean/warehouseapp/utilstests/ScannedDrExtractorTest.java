package com.kingocean.warehouseapp.utilstests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.kingocean.warehouseapp.utils.ScannedDrExtractor;

public class ScannedDrExtractorTest {
    @Test
    public void testExtractValue() {
        String input1 = "%U%33883345-3";
        String expectedOutput1 = "33883345";
        assertEquals(expectedOutput1, ScannedDrExtractor.extractValue(input1));

        String input2 = "%U%12345678";
        String expectedOutput2 = "12345678";
        assertEquals(expectedOutput2, ScannedDrExtractor.extractValue(input2));

        String input3 = "%U%12345678-10";
        String expectedOutput3 = "12345678";
        assertEquals(expectedOutput3, ScannedDrExtractor.extractValue(input3));

        String input4 = "No match";
        assertNull(ScannedDrExtractor.extractValue(input4));
    }
}
