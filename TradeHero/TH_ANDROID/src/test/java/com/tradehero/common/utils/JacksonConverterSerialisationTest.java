package com.tradehero.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JacksonConverterSerialisationTest
{
    private JacksonConverter jacksonConverter;
    private String localPath = "C:\\Users\\xavier\\Documents\\Projects\\TH_ANDROID3\\TradeHero\\TH_ANDROID\\src\\test\\java\\com\\tradehero\\common\\";

    @Before public void setUp()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        jacksonConverter = new JacksonConverter(objectMapper);
    }

    @After public void tearDown()
    {
    }

    private File getLocalFile(String fileName)
    {
        return new File(localPath + fileName);
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //URL url = classLoader.getResource(".");
        //return new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    @Test public void shouldSerialiseSimple1() throws Exception
    {
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(12, 34);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        jacksonConverter.toBody(ownedPortfolioId).writeTo(byteArrayOutputStream);
        String serialised = byteArrayOutputStream.toString("UTF-8");

        assertEquals("{\"userId\":12,\"portfolioId\":34}", serialised);
    }
}
