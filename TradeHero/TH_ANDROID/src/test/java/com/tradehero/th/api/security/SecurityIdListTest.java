package com.tradehero.th.api.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityIdListTest
{
    public static final String TAG = SecurityIdListTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    private SecurityId getA()
    {
        return new SecurityId("A", "B");
    }

    private SecurityId getC()
    {
        return new SecurityId("C", "D");
    }

    private SecurityIdList getEmpty()
    {
        return new SecurityIdList();
    }

    private SecurityIdList getListA()
    {
        SecurityIdList list = getEmpty();
        list.add(getA());
        return list;
    }

    private SecurityIdList getListC()
    {
        SecurityIdList list = getEmpty();
        list.add(getC());
        return list;
    }

    private SecurityIdList getListAC()
    {
        SecurityIdList list = getListA();
        list.add(getC());
        return list;
    }

    private SecurityIdList getListCA()
    {
        SecurityIdList list = getListC();
        list.add(getA());
        return list;
    }

    @Test public void testEmptyList()
    {
        assertFalse(getEmpty().contains(getA()));
        assertFalse(getEmpty().contains(getC()));
    }

    @Test public void testListA()
    {
        assertTrue(getListA().contains(getA()));
        assertFalse(getListA().contains(getC()));
    }

    @Test public void testListC()
    {
        assertFalse(getListC().contains(getA()));
        assertTrue(getListC().contains(getC()));
    }

    @Test public void testListAC()
    {
        assertTrue(getListAC().contains(getA()));
        assertTrue(getListAC().contains(getC()));
    }

    @Test public void testListCA()
    {
        assertTrue(getListCA().contains(getA()));
        assertTrue(getListCA().contains(getC()));
    }
}
