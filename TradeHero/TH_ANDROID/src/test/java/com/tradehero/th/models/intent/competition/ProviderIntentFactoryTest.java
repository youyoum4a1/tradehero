package com.ayondo.academy.models.intent.competition;

import android.content.Intent;
import android.net.Uri;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProviderIntentFactoryTest
{
    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test public void createsPageIntent()
    {
        ProviderIntentFactory factory = new ProviderIntentFactory(RuntimeEnvironment.application.getApplicationContext());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tradehero://providers/456/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234"));
        ProviderIntent pageIntent = factory.create(intent);

        assertTrue(pageIntent instanceof ProviderPageIntent);
        assertEquals("/competitionpages/rules?providerId=789&userId=234", ((ProviderPageIntent) pageIntent).getForwardUriPath());
    }
}
