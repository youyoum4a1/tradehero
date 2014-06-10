package com.tradehero.th.api.discussion.key;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class TypedMessageListKeyTest extends BaseTypedMessageListKeyTest
{
    @Test public void equalItself()
    {
        assertTrue(key1_1_p().equals(key1_1_p()));
        assertTrue(key1_2_p().equals(key1_2_p()));
        assertTrue(key2_1_p().equals(key2_1_p()));
        assertTrue(key2_2_p().equals(key2_2_p()));
        assertTrue(key1_1_c().equals(key1_1_c()));
        assertTrue(key1_2_c().equals(key1_2_c()));
        assertTrue(key2_1_c().equals(key2_1_c()));
        assertTrue(key2_2_c().equals(key2_2_c()));
    }

    @Test public void notEqualsIfDifferentPage()
    {
        assertFalse(key1_1_p().equals(key2_1_p()));
        assertFalse(key2_1_p().equals(key1_1_p()));
        assertFalse(key1_2_p().equals(key2_2_p()));
        assertFalse(key2_2_p().equals(key1_2_p()));
        assertFalse(key1_1_c().equals(key2_1_c()));
        assertFalse(key2_1_c().equals(key1_1_c()));
        assertFalse(key1_2_c().equals(key2_2_c()));
        assertFalse(key2_2_c().equals(key1_2_c()));
    }

    @Test public void notEqualsIfDifferentPerPage()
    {
        assertFalse(key1_1_p().equals(key1_2_p()));
        assertFalse(key1_2_p().equals(key1_1_p()));
        assertFalse(key2_1_p().equals(key2_2_p()));
        assertFalse(key2_2_p().equals(key2_1_p()));
        assertFalse(key1_1_c().equals(key1_2_c()));
        assertFalse(key1_2_c().equals(key1_1_c()));
        assertFalse(key2_1_c().equals(key2_2_c()));
        assertFalse(key2_2_c().equals(key2_1_c()));
    }

    @Test public void notEqualsIfDifferentType()
    {
        assertFalse(key1_1_p().equals(key1_1_c()));
        assertFalse(key1_1_c().equals(key1_1_p()));
        assertFalse(key1_2_p().equals(key1_2_c()));
        assertFalse(key1_2_c().equals(key1_2_p()));
        assertFalse(key2_1_p().equals(key2_1_c()));
        assertFalse(key2_1_c().equals(key2_1_p()));
        assertFalse(key2_2_p().equals(key2_2_c()));
        assertFalse(key2_2_c().equals(key2_2_p()));
    }
}
