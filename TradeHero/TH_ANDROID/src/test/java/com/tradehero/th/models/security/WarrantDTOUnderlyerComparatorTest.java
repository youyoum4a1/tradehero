package com.tradehero.th.models.security;

import com.tradehero.th.api.security.WarrantDTO;
import java.util.Iterator;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WarrantDTOUnderlyerComparatorTest
{
    private TreeSet<WarrantDTO> treeSet;

    @Before public void setUp()
    {
        treeSet = new TreeSet<>(new WarrantDTOUnderlyerComparator());
    }

    @After public void tearDown()
    {
    }

    @Test public void nullAtTheEnd1()
    {
        treeSet.add(null);
        treeSet.add(new WarrantDTO());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertNotNull(iterator.next());
        assertNull(iterator.next());
    }

    @Test public void nullAtTheEnd2()
    {
        treeSet.add(new WarrantDTO());
        treeSet.add(null);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertNotNull(iterator.next());
        assertNull(iterator.next());
    }

    @Test public void nullUnderlyingAtTheEnd1()
    {
        WarrantDTO nullUnderlyer = new WarrantDTO();
        WarrantDTO hasUnderlyer = new WarrantDTO();
        hasUnderlyer.underlyingName = "whack";

        treeSet.add(nullUnderlyer);
        treeSet.add(hasUnderlyer);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEquals("whack", iterator.next().underlyingName);
        assertNull(iterator.next().underlyingName);
    }

    @Test public void nullUnderlyingAtTheEnd2()
    {
        WarrantDTO nullUnderlyer = new WarrantDTO();
        WarrantDTO hasUnderlyer = new WarrantDTO();
        hasUnderlyer.underlyingName = "whack";

        treeSet.add(hasUnderlyer);
        treeSet.add(nullUnderlyer);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEquals("whack", iterator.next().underlyingName);
        assertNull(iterator.next().underlyingName);
    }

    @Test public void underlyingAlphaUp1()
    {
        WarrantDTO underlyer1 = new WarrantDTO();
        underlyer1.underlyingName = "bart";
        WarrantDTO underlyer2 = new WarrantDTO();
        underlyer2.underlyingName = "whack";

        treeSet.add(underlyer1);
        treeSet.add(underlyer2);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEquals("bart", iterator.next().underlyingName);
        assertEquals("whack", iterator.next().underlyingName);
    }

    @Test public void underlyingAlphaUp2()
    {
        WarrantDTO underlyer1 = new WarrantDTO();
        underlyer1.underlyingName = "bart";
        WarrantDTO underlyer2 = new WarrantDTO();
        underlyer2.underlyingName = "whack";

        treeSet.add(underlyer2);
        treeSet.add(underlyer1);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEquals("bart", iterator.next().underlyingName);
        assertEquals("whack", iterator.next().underlyingName);
    }
}
