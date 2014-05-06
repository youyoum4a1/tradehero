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


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WarrantDTOUnderlyerTypeComparatorTest extends WarrantDTOComparatorTestBase
{
    public static final String TAG = WarrantDTOUnderlyerTypeComparatorTest.class.getSimpleName();

    private TreeSet<WarrantDTO> treeSet;

    @Before public void setUp()
    {
        WarrantDTOUnderlyerTypeComparator comparator = new WarrantDTOUnderlyerTypeComparator(
                new WarrantDTOUnderlyerComparator(), new WarrantDTOTypeComparator());
        treeSet = new TreeSet<>(comparator);
    }

    @After public void tearDown()
    {
    }

    @Test public void testACnAP1()
    {
        treeSet.add(getAC());
        treeSet.add(getAP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getAC(), iterator.next());
        assertEqualsWarrant(getAP(), iterator.next());
    }

    @Test public void testACnAP2()
    {
        treeSet.add(getAP());
        treeSet.add(getAC());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getAC(), iterator.next());
        assertEqualsWarrant(getAP(), iterator.next());
    }

    @Test public void testAPnANull1()
    {
        treeSet.add(getAP());
        treeSet.add(getANull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getAP(), iterator.next());
        assertEqualsWarrant(getANull(), iterator.next());
    }

    @Test public void testAPnANull2()
    {
        treeSet.add(getANull());
        treeSet.add(getAP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getAP(), iterator.next());
        assertEqualsWarrant(getANull(), iterator.next());
    }

    @Test public void testANullnZC1()
    {
        treeSet.add(getANull());
        treeSet.add(getZC());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getANull(), iterator.next());
        assertEqualsWarrant(getZC(), iterator.next());
    }

    @Test public void testANullnZC2()
    {
        treeSet.add(getZC());
        treeSet.add(getANull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getANull(), iterator.next());
        assertEqualsWarrant(getZC(), iterator.next());
    }

    @Test public void testZCnZP1()
    {
        treeSet.add(getZC());
        treeSet.add(getZP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZC(), iterator.next());
        assertEqualsWarrant(getZP(), iterator.next());
    }

    @Test public void testZCnZP2()
    {
        treeSet.add(getZP());
        treeSet.add(getZC());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZC(), iterator.next());
        assertEqualsWarrant(getZP(), iterator.next());
    }

    @Test public void testZPnZNull1()
    {
        treeSet.add(getZP());
        treeSet.add(getZNull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZP(), iterator.next());
        assertEqualsWarrant(getZNull(), iterator.next());
    }

    @Test public void testZPnZNull2()
    {
        treeSet.add(getZNull());
        treeSet.add(getZP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZP(), iterator.next());
        assertEqualsWarrant(getZNull(), iterator.next());
    }

    @Test public void testZNullnNullC1()
    {
        treeSet.add(getZNull());
        treeSet.add(getNullC());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZNull(), iterator.next());
        assertEqualsWarrant(getNullC(), iterator.next());
    }

    @Test public void testZNullnNullC2()
    {
        treeSet.add(getNullC());
        treeSet.add(getZNull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getZNull(), iterator.next());
        assertEqualsWarrant(getNullC(), iterator.next());
    }

    @Test public void testNullCnNullP1()
    {
        treeSet.add(getNullC());
        treeSet.add(getNullP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullC(), iterator.next());
        assertEqualsWarrant(getNullP(), iterator.next());
    }

    @Test public void testNullCnNullP2()
    {
        treeSet.add(getNullC());
        treeSet.add(getNullP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullC(), iterator.next());
        assertEqualsWarrant(getNullP(), iterator.next());
    }

    @Test public void testNullPnNullNull1()
    {
        treeSet.add(getNullP());
        treeSet.add(getNullNull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullP(), iterator.next());
        assertEqualsWarrant(getNullNull(), iterator.next());
    }

    @Test public void testNullPnNullNull2()
    {
        treeSet.add(getNullNull());
        treeSet.add(getNullP());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullP(), iterator.next());
        assertEqualsWarrant(getNullNull(), iterator.next());
    }

    @Test public void testNullNullnNull1()
    {
        treeSet.add(null);
        treeSet.add(getNullNull());

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullNull(), iterator.next());
        assertEqualsWarrant(getNull(), iterator.next());
    }

    @Test public void testNullNullnNull2()
    {
        treeSet.add(getNullNull());
        treeSet.add(null);

        Iterator<WarrantDTO> iterator = treeSet.iterator();
        assertEqualsWarrant(getNullNull(), iterator.next());
        assertEqualsWarrant(getNull(), iterator.next());
    }
}
