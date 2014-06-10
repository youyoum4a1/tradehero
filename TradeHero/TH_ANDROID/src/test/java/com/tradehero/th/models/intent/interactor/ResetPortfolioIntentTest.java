package com.tradehero.th.models.intent.interactor;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ResetPortfolioIntentTest
{
    @Test public void testPathCorrect()
    {
        assertEquals("tradehero://reset-portfolio", new ResetPortfolioIntent().getUriPath());
    }
}
