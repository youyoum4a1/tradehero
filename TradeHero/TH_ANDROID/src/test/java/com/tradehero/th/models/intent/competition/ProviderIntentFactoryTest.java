package com.tradehero.th.models.intent.competition;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.TestConstants;
import com.tradehero.th.models.intent.OpenCurrentActivityHolder;
import com.tradehero.th.models.intent.THIntent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/20/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConstants.TRADEHERO_MANIFEST_PATH)
public class ProviderIntentFactoryTest
{
    public static final String TAG = ProviderIntentFactoryTest.class.getSimpleName();

    @Before public void setUp()
    {
        THIntent.currentActivityHolder = new OpenCurrentActivityHolder(Robolectric.getShadowApplication().getApplicationContext());
    }

    @After public void tearDown()
    {
        THIntent.currentActivityHolder = null;
    }

    @Test public void createsPageIntent()
    {
        ProviderIntentFactory factory = new ProviderIntentFactory();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tradehero://providers/456/pages/%252Fcompetitionpages%252Frules%253FproviderId%253D789%2526userId%253D234"));
        ProviderIntent pageIntent = factory.create(intent);

        assertTrue(pageIntent instanceof ProviderPageIntent);
        assertEquals("/competitionpages/rules?providerId=789&userId=234", ((ProviderPageIntent) pageIntent).getForwardUriPath());
    }
}
