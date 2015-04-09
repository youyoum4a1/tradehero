package com.tradehero.th.api.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.base.TestTHApp;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProviderDTODeserialiserTest
    extends ProviderDTODeserialiserTestBase<ProviderDTO>
{
    @Inject @ForApp protected ObjectMapper normalMapper;

    @Before public void setUp() throws IOException
    {
        super.setUp();
        TestTHApp.staticInject(this);
    }

    @Override protected ProviderDTO readValue(InputStream stream) throws IOException
    {
        return normalMapper.readValue(stream, ProviderDTO.class);
    }

    @Test public void testNormalDeserialiseBody1() throws IOException
    {
        ProviderDTO converted = baseTestNormalDeserialiseBody1();
        assertEquals(ProviderDTO.class, converted.getClass());
    }

    @Test public void testSetSpecifics() throws IOException
    {
        ProviderDTO converted = baseTestSetSpecifics();
        assertEquals(ProviderDTO.class, converted.getClass());
    }
}
