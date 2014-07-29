package com.tradehero.th.api.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.api.BaseApiTest;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class AlertCompactDTOTest extends BaseApiTest
{
    @Inject @ForApp ObjectMapper normalMapper;
    private InputStream alertCompactBody1Stream;

    @Before public void setUp()
    {
        alertCompactBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/AlertCompactDTOBody1.json");
    }

    @Test public void testCanDeserialise() throws IOException
    {
        AlertCompactDTO alertCompact1 = normalMapper.readValue(alertCompactBody1Stream, AlertCompactDTO.class);

        assertThat(alertCompact1.id).isEqualTo(1511);
        assertThat(alertCompact1.active).isTrue();
    }
}
