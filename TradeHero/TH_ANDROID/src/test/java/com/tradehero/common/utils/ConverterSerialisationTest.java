package com.tradehero.common.utils;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.base.TestTHApp;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import retrofit.converter.Converter;

import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConverterSerialisationTest
{
    @Inject Converter converter;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
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
