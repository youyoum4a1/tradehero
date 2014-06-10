package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.market.ExchangeDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class TrendingFilterTypeDTOFactoryTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void nullBundleSendsException()
    {
        try
        {
            new TrendingFilterTypeDTOFactory().create(null);
            assertNull("We should not reach here");
        }
        catch (NullPointerException e)
        {
        }
    }

    @Test public void canInstantiateBasicFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeBasicDTO(new ExchangeDTO(1, "ABC", 2, null, "A B C", false, true));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = new TrendingFilterTypeDTOFactory().create(args);
        assertTrue(expanded instanceof TrendingFilterTypeBasicDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiateVolumeFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeVolumeDTO(new ExchangeDTO(2, "BCD", 3, null, "B C D", false, true));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = new TrendingFilterTypeDTOFactory().create(args);
        assertTrue(expanded instanceof TrendingFilterTypeVolumeDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiatePriceFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypePriceDTO(new ExchangeDTO(3, "CDE", 4, null, "C D E", false, true));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = new TrendingFilterTypeDTOFactory().create(args);
        assertTrue(expanded instanceof TrendingFilterTypePriceDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiateGenericFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeGenericDTO(new ExchangeDTO(4, "DEF", 5, null, "D E F", false, true));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = new TrendingFilterTypeDTOFactory().create(args);
        assertTrue(expanded instanceof TrendingFilterTypeGenericDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }
}
