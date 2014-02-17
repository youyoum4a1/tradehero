package com.tradehero.th.api.security;

import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.WarrantProviderSecurityListType;
import com.tradehero.th.api.security.key.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 1/23/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityListTypeFactoryTest
{
    public static final String TAG = SecurityListTypeFactoryTest.class.getSimpleName();

    private SecurityListTypeFactory securityListTypeFactory;

    @Before public void setUp()
    {
        securityListTypeFactory = new SecurityListTypeFactory();
    }

    @After public void tearDown()
    {
    }

    //<editor-fold desc="Creators">
    private TrendingBasicSecurityListType getTrendingBasic(int page)
    {
        return new TrendingBasicSecurityListType("ABC", page, 3);
    }

    private TrendingVolumeSecurityListType getTrendingVolume(int page)
    {
        return new TrendingVolumeSecurityListType("BCD", page, 5);
    }

    private TrendingPriceSecurityListType getTrendingPrice(int page)
    {
        return new TrendingPriceSecurityListType("CDE", page, 7);
    }

    private TrendingAllSecurityListType getTrendingAll(int page)
    {
        return new TrendingAllSecurityListType("DEF", page, 9);
    }

    private TrendingSecurityListType getTrending(int page)
    {
        return new TrendingSecurityListType("EFG", page, 11);
    }

    private SearchSecurityListType getSearch(int page)
    {
        return new SearchSecurityListType("FGH", page, 13);
    }

    private BasicProviderSecurityListType getBasicProvider(int page)
    {
        return new BasicProviderSecurityListType(new ProviderId(14), page, 16);
    }

    private WarrantProviderSecurityListType getWarrantProvider(int page)
    {
        return new WarrantProviderSecurityListType(new ProviderId(17), page, 19);
    }
    //</editor-fold>

    //<editor-fold desc="Own Assertions">
    private void assertIsTrendingBasicPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getTrendingBasic(page), securityListType);
    }

    private void assertIsTrendingVolumePagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getTrendingVolume(page), securityListType);
    }

    private void assertIsTrendingPricePagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getTrendingPrice(page), securityListType);
    }

    private void assertIsTrendingAllPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getTrendingAll(page), securityListType);
    }

    private void assertIsTrendingPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getTrending(page), securityListType);
    }

    private void assertIsSearchPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getSearch(page), securityListType);
    }

    private void assertIsBasicProviderPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getBasicProvider(page), securityListType);
    }

    private void assertIsWarrantProviderPagedUp(int page, SecurityListType securityListType)
    {
        assertEquals(getWarrantProvider(page), securityListType);
    }
    //</editor-fold>

    @Test public void testTrendingBasic()
    {
        assertIsTrendingBasicPagedUp(4, securityListTypeFactory.cloneAtPage(getTrendingBasic(3), 4));
    }

    @Test public void testTrendingVolume()
    {
        assertIsTrendingVolumePagedUp(7, securityListTypeFactory.cloneAtPage(getTrendingVolume(4), 7));
    }

    @Test public void testTrendingPrice()
    {
        assertIsTrendingPricePagedUp(5, securityListTypeFactory.cloneAtPage(getTrendingPrice(10), 5));
    }

    @Test public void testTrendingAll()
    {
        assertIsTrendingAllPagedUp(6, securityListTypeFactory.cloneAtPage(getTrendingAll(11), 6));
    }

    @Test public void testTrending()
    {
        assertIsTrendingPagedUp(10, securityListTypeFactory.cloneAtPage(getTrending(9), 10));
    }

    @Test public void testSearch()
    {
        assertIsSearchPagedUp(15, securityListTypeFactory.cloneAtPage(getSearch(19), 15));
    }

    @Test public void testBasicProvider()
    {
        assertIsBasicProviderPagedUp(10, securityListTypeFactory.cloneAtPage(getBasicProvider(21), 10));
    }

    @Test public void testWarrantProvider()
    {
        assertIsWarrantProviderPagedUp(12, securityListTypeFactory.cloneAtPage(getWarrantProvider(22), 12));
    }
}
