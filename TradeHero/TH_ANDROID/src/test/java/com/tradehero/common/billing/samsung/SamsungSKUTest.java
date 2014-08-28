package com.tradehero.common.billing.samsung;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 4/1/14.
 */
public class SamsungSKUTest extends BaseSamsunSKUTest
{
    @Test public void testEqualSelf()
    {
        assertTrue(createSKU1().equals(createSKU1()));
        assertTrue(createSKU2().equals(createSKU2()));
        assertTrue(createSKU3().equals(createSKU3()));
        assertTrue(createSKU4().equals(createSKU4()));
    }

    @Test public void testDifferentFromItemGroup()
    {
        assertFalse(createGroup1().equals(createSKU1()));
        assertFalse(createSKU1().equals(createGroup1()));

        assertFalse(createGroup1().equals(createSKU2()));
        assertFalse(createSKU2().equals(createGroup1()));

        assertFalse(createGroup2().equals(createSKU3()));
        assertFalse(createSKU3().equals(createGroup2()));

        assertFalse(createGroup2().equals(createSKU4()));
        assertFalse(createSKU4().equals(createGroup2()));
    }

    @Test public void testDifferentWhenDiffItem()
    {
        assertFalse(createSKU1().equals(createSKU2()));
        assertFalse(createSKU1().equals(createSKU3()));
        assertFalse(createSKU1().equals(createSKU4()));

        assertFalse(createSKU2().equals(createSKU1()));
        assertFalse(createSKU2().equals(createSKU3()));
        assertFalse(createSKU2().equals(createSKU4()));

        assertFalse(createSKU3().equals(createSKU1()));
        assertFalse(createSKU3().equals(createSKU2()));
        assertFalse(createSKU3().equals(createSKU4()));

        assertFalse(createSKU4().equals(createSKU1()));
        assertFalse(createSKU4().equals(createSKU2()));
        assertFalse(createSKU4().equals(createSKU3()));
    }
}
