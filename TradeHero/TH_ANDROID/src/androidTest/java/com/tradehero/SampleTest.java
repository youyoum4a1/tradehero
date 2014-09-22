package com.tradehero;

import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class SampleTest
{
    @Inject @FacebookAppId String facebookAppId;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void testFacebookAppId()
    {
        assertThat(facebookAppId).isEqualToIgnoringCase("431745923529834");
    }
}
