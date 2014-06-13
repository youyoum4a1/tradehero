package com.tradehero.th.api.competition;

import com.tradehero.th.api.competition.key.HelpVideoListKey;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HelpVideoListKeyTest
{
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
