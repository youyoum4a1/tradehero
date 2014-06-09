package com.tradehero.th.models.intent.interactor;

import com.tradehero.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConstants.MANIFEST_PATH)
public class ResetPortfolioIntentTest
{
    @Test public void testPathCorrect()
    {
        assertEquals("tradehero://reset-portfolio", new ResetPortfolioIntent().getUriPath());
    }
}
