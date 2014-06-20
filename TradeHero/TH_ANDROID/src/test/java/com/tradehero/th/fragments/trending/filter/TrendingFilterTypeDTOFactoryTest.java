package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
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
    @Inject TrendingFilterTypeDTOFactory trendingFilterTypeDTOFactory;
    @Inject Context context;

    @Before public void setUp()
    {
        DaggerUtils.inject(this);
    }

    @After public void tearDown()
    {
    }

    @Test public void nullBundleSendsException()
    {
        try
        {
            trendingFilterTypeDTOFactory.create(null);
            assertNull("We should not reach here");
        }
        catch (NullPointerException e)
        {
        }
    }

    @Test public void canInstantiateBasicFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeBasicDTO(
                new ExchangeCompactSpinnerDTO(
                        context.getResources(),
                        new ExchangeDTO(1, "ABC", null, 2, "A B C", false, true, false, null)));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = trendingFilterTypeDTOFactory.create(args);
        assertTrue(expanded instanceof TrendingFilterTypeBasicDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiateVolumeFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeVolumeDTO(
                new ExchangeCompactSpinnerDTO(
                        context.getResources(),
                        new ExchangeDTO(2, "BCD", null, 3, "B C D", false, true, false, null)));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = trendingFilterTypeDTOFactory.create(args);
        assertTrue(expanded instanceof TrendingFilterTypeVolumeDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiatePriceFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypePriceDTO(
                new ExchangeCompactSpinnerDTO(
                        context.getResources(),
                        new ExchangeDTO(3, "CDE", null, 4, "C D E", false, true, false, null)));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = trendingFilterTypeDTOFactory.create(args);
        assertTrue(expanded instanceof TrendingFilterTypePriceDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }

    @Test public void canInstantiateGenericFilterType()
    {
        TrendingFilterTypeDTO filterTypeDTO = new TrendingFilterTypeGenericDTO(
                new ExchangeCompactSpinnerDTO(
                        context.getResources(),
                        new ExchangeDTO(4, "DEF", null, 5, "D E F", false, true, false, null)));
        Bundle args = filterTypeDTO.getArgs();

        TrendingFilterTypeDTO expanded = trendingFilterTypeDTOFactory.create(args);
        assertTrue(expanded instanceof TrendingFilterTypeGenericDTO);
        assertEquals(filterTypeDTO.titleResId, expanded.titleResId);
        assertEquals(filterTypeDTO.titleIconResId, expanded.titleIconResId);
        assertEquals(filterTypeDTO.descriptionResId, expanded.descriptionResId);
        assertTrue(filterTypeDTO.exchange.equals(expanded.exchange));
    }
}
