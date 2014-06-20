package com.tradehero.th.api.market;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.models.market.ExchangeCompactDTODescriptionNameComparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class ExchangeCompactDTODescriptionNameComparatorTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    private ExchangeCompactDTO getBcAb()
    {
        return new ExchangeCompactDTO(1, "Bc", "SG", 1, "Ab", true, true, true);
    }

    private ExchangeCompactDTO getBcAc()
    {
        return new ExchangeCompactDTO(1, "Bc", "SG", 1, "Ac", true, true, true);
    }

    private ExchangeCompactDTO getBcNull()
    {
        return new ExchangeCompactDTO(1, "Bc", "SG", 1, null, true, true, true);
    }

    private ExchangeCompactDTO getBdAb()
    {
        return new ExchangeCompactDTO(1, "Bd", "SG", 1, "Ab", true, true, true);
    }

    private ExchangeCompactDTO getBdAc()
    {
        return new ExchangeCompactDTO(1, "Bd", "SG", 1, "Ac", true, true, true);
    }

    private ExchangeCompactDTO getBdNull()
    {
        return new ExchangeCompactDTO(1, "Bd", "SG", 1, null, true, true, true);
    }

    private boolean sameNameAndDesc(ExchangeCompactDTO lhs, ExchangeCompactDTO rhs)
    {
        return (lhs.desc == null ? rhs.desc == null : lhs.desc.equals(rhs.desc)) &&
                lhs.name.equals(rhs.name);
    }

    @Test public void getExpectedOrder()
    {
        SortedSet<ExchangeCompactDTO> set = new TreeSet<>(new ExchangeCompactDTODescriptionNameComparator<>());

        set.add(getBdAb());
        set.add(getBcNull());
        set.add(getBdAc());
        set.add(getBcAc());
        set.add(null);
        set.add(getBcAb());
        set.add(getBdNull());

        assertEquals(10, set.size());
        Iterator<ExchangeCompactDTO> iterator = set.iterator();

        assertTrue(sameNameAndDesc(iterator.next(), getBcAb()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdAb()));
        assertTrue(sameNameAndDesc(iterator.next(), getBcAc()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdAc()));
        assertTrue(sameNameAndDesc(iterator.next(), getBcNull()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdNull()));
        assertNull(iterator.next());
    }
}
