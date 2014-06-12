package com.tradehero.common.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.converter.Converter;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ConverterSerialisationTest
{
    @Inject Converter converter;

    @Before public void setUp()
    {
    }

    @Test public void shouldSerialiseSimple1() throws Exception
    {
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(12, 34);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        converter.toBody(ownedPortfolioId).writeTo(byteArrayOutputStream);
        String serialised = byteArrayOutputStream.toString("UTF-8");

        assertEquals("{\"userId\":12,\"portfolioId\":34}", serialised);
    }
}
