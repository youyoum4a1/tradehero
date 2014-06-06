package com.tradehero;

import com.tradehero.th.auth.operator.FacebookAppId;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class SampleTest
{
    @Inject @FacebookAppId String facebookAppId;

    @Before public void setUp()
    {
    }

    @Test public void testFacebookAppId()
    {
        assertThat(facebookAppId).isEqualToIgnoringCase("431745923529834");
    }
}
