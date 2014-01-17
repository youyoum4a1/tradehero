package com.tradehero.th.api.provider;

import com.tradehero.th.api.competition.ProviderListKey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/17/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ProviderListKeyTest
{
    public static final String TAG = ProviderListKeyTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void equalsTrueOnSame()
    {
        assertTrue(new ProviderListKey().equals(new ProviderListKey()));
    }

    @Test public void equalsFalseWithNull()
    {
        assertFalse(new ProviderListKey().equals(null));
    }
}
