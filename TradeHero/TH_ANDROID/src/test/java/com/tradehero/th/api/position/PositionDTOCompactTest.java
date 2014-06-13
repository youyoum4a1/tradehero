package com.tradehero.th.api.position;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class PositionDTOCompactTest extends BasePositionDTOCompactTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void testCanCopyFieldsWithProperClass()
    {
        PositionDTOCompact first = new PositionDTOCompact();
        first.id = 1;
        first.averagePriceRefCcy = 2.0d;
        first.portfolioId = 3;
        first.shares = 4;
        PositionDTOCompact second = new PositionDTOCompact(first, PositionDTOCompact.class);
        assertTrue(haveSameFields(first, second));
    }
}
