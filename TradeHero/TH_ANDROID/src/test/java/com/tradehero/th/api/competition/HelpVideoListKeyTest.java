package com.tradehero.th.api.competition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/22/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class HelpVideoListKeyTest
{
    public static final String TAG = HelpVideoListKeyTest.class.getSimpleName();

    @Test public void notEqualsWhenNotType()
    {
        assertFalse(new HelpVideoListKey(new ProviderId(3)).equals(new Integer(4)));
    }

    @Test public void notEqualsWhenNull()
    {
        assertFalse(new HelpVideoListKey(new ProviderId(4)).equals(null));
    }

    @Test public void equalsWhenSameProviderId()
    {
        assertTrue(new HelpVideoListKey(new ProviderId(3)).equals(new HelpVideoListKey(new ProviderId(3))));
    }

    @Test public void notEqualsWhenDifferentProviderId()
    {
        assertFalse(new HelpVideoListKey(new ProviderId(3)).equals(new HelpVideoListKey(new ProviderId(4))));
    }
}
