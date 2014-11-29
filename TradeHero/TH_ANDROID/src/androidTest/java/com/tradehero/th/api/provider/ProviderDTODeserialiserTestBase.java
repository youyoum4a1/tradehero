package com.tradehero.th.api.provider;

import com.tradehero.th.api.BaseApiTestClass;
import com.tradehero.th.api.competition.ProviderDTO;
import java.io.IOException;
import java.io.InputStream;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

abstract public class ProviderDTODeserialiserTestBase<ProviderCompactDTOType extends ProviderDTO>
        extends BaseApiTestClass
{
    protected InputStream providerDTOBody1Stream;
    protected InputStream providerDTOBody1WithFakeStream;

    public void setUp() throws IOException
    {
        providerDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/ProviderDTOBody1.json");
        providerDTOBody1WithFakeStream = getClass().getResourceAsStream(getPackagePath() + "/ProviderDTOBody1WithFake.json");
    }

    abstract protected ProviderCompactDTOType readValue(InputStream stream) throws IOException;

    protected ProviderCompactDTOType baseTestNormalDeserialiseBody1() throws IOException
    {
        ProviderCompactDTOType converted = readValue(providerDTOBody1Stream);
        assertEquals(80, converted.singleRowHeightPoint, 0.1);
        return converted;
    }

    protected ProviderCompactDTOType baseTestSetSpecifics() throws IOException
    {
        ProviderCompactDTOType converted = readValue(providerDTOBody1Stream);
        assertThat(converted.specificKnowledge).isNotNull();
        assertThat(converted.specificKnowledge.includeProviderPortfolioOnWarrants).isTrue();
        return converted;
    }

    @Test
    public void testDoesNotCrashOnNewFields() throws IOException
    {
        ProviderCompactDTOType converted = readValue(providerDTOBody1WithFakeStream);
        assertThat(converted.id).isEqualTo(3);
    }
}
