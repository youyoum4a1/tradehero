package com.tradehero.th.api.provider;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.competition.key.ProviderListKey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProviderListKeyTest
{
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
