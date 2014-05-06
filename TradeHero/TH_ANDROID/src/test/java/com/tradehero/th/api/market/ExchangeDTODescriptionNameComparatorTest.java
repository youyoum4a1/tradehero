package com.tradehero.th.api.market;

import com.tradehero.th.models.market.ExchangeDTODescriptionNameComparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ExchangeDTODescriptionNameComparatorTest
{
    public static final String TAG = ExchangeDTODescriptionNameComparatorTest.class.getSimpleName();

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    private ExchangeDTO getBcAb()
    {
        return new ExchangeDTO(1, "Bc", 1, null, "Ab", true, true);
    }

    private ExchangeDTO getBcAc()
    {
        return new ExchangeDTO(1, "Bc", 1, null, "Ac", true, true);
    }

    private ExchangeDTO getBcNull()
    {
        return new ExchangeDTO(1, "Bc", 1, null, null, true, true);
    }

    private ExchangeDTO getBdAb()
    {
        return new ExchangeDTO(1, "Bd", 1, null, "Ab", true, true);
    }

    private ExchangeDTO getBdAc()
    {
        return new ExchangeDTO(1, "Bd", 1, null, "Ac", true, true);
    }

    private ExchangeDTO getBdNull()
    {
        return new ExchangeDTO(1, "Bd", 1, null, null, true, true);
    }

    private ExchangeDTO getNullAb()
    {
        return new ExchangeDTO(1, null, 1, null, "Ab", true, true);
    }

    private ExchangeDTO getNullAc()
    {
        return new ExchangeDTO(1, null, 1, null, "Ac", true, true);
    }

    private ExchangeDTO getNullNull()
    {
        return new ExchangeDTO();
    }

    private boolean sameNameAndDesc(ExchangeDTO lhs, ExchangeDTO rhs)
    {
        return (lhs.desc == null ? rhs.desc == null : lhs.desc.equals(rhs.desc)) &&
                (lhs.name == null ? rhs.name == null : lhs.name.equals(rhs.name));
    }

    @Test public void getExpectedOrder()
    {
        SortedSet<ExchangeDTO> set = new TreeSet<ExchangeDTO>(new ExchangeDTODescriptionNameComparator());

        set.add(getNullAb());
        set.add(getNullNull());
        set.add(getBdAb());
        set.add(getBcNull());
        set.add(getBdAc());
        set.add(getNullAc());
        set.add(getBcAc());
        set.add(null);
        set.add(getBcAb());
        set.add(getBdNull());

        assertEquals(10, set.size());
        Iterator<ExchangeDTO> iterator = set.iterator();

        assertTrue(sameNameAndDesc(iterator.next(), getBcAb()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdAb()));
        assertTrue(sameNameAndDesc(iterator.next(), getNullAb()));
        assertTrue(sameNameAndDesc(iterator.next(), getBcAc()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdAc()));
        assertTrue(sameNameAndDesc(iterator.next(), getNullAc()));
        assertTrue(sameNameAndDesc(iterator.next(), getBcNull()));
        assertTrue(sameNameAndDesc(iterator.next(), getBdNull()));
        assertTrue(sameNameAndDesc(iterator.next(), getNullNull()));
        assertNull(iterator.next());
    }
}
