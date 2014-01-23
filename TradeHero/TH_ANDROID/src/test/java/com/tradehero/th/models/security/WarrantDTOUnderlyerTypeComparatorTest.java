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

/**
 * Created by xavier on 1/23/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class WarrantDTOUnderlyerTypeComparatorTest
{
    public static final String TAG = WarrantDTOUnderlyerTypeComparatorTest.class.getSimpleName();

    private TreeSet<WarrantDTO> treeSet;

    @Before public void setUp()
    {
        WarrantDTOUnderlyerTypeComparator comparator = new WarrantDTOUnderlyerTypeComparator();
        comparator.underlyerComparator = new WarrantDTOUnderlyerComparator();
        comparator.typeComparator = new WarrantDTOTypeComparator();
        treeSet = new TreeSet<>(comparator);
    }

    @After public void tearDown()
    {
    }

    //<editor-fold desc="Creators">
    private WarrantDTO getAC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    private WarrantDTO getAP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    private WarrantDTO getANull()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        return warrantDTO;
    }

    private WarrantDTO getZC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    private WarrantDTO getZP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    private WarrantDTO getZNull()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        return warrantDTO;
    }

    private WarrantDTO getNullC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    private WarrantDTO getNullP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    private WarrantDTO getNullNull()
    {
        return new WarrantDTO();
    }

    private WarrantDTO getNull()
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Own Asserts">
    private void assertEqualsWarrant(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected == null)
        {
            assertNull(actual);
        }
        else
        {
            assertNotNull(actual);
            assertEqualsUnderlying(expected, actual);
            assertEqualsType(expected, actual);
        }
    }

    private void assertEqualsUnderlying(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected.underlyingName == null)
        {
            assertNull(actual.underlyingName);
        }
        else
        {
            assertNotNull(actual.underlyingName);
            assertEquals(expected.underlyingName, actual.underlyingName);
        }
    }

    private void assertEqualsType(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected.warrantType == null)
        {
            assertNull(actual.warrantType);
        }
        else
        {
            assertNotNull(actual.warrantType);
            assertEquals(expected.warrantType, actual.warrantType);
        }
    }
    //</editor-fold>

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
