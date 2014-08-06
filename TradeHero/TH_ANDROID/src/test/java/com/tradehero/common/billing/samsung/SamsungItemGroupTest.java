package com.tradehero.common.billing.samsung;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 4/1/14.
 */
public class SamsungItemGroupTest extends BaseSamsungItemGroupTest
{
    @Test public void testSame()
    {
        assertEquals(createGroup1(), createGroup1());
        assertEquals(createGroup2(), createGroup2());
        assertTrue(createGroup1().equals(createGroup1()));
        assertTrue(createGroup2().equals(createGroup2()));
    }

    @Test public void testDifferent()
    {
        assertFalse(createGroup1().equals(createGroup2()));
        assertFalse(createGroup2().equals(createGroup1()));
    }
}
