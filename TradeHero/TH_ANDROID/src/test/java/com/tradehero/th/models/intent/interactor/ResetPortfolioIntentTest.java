package com.tradehero.th.models.intent.interactor;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class ResetPortfolioIntentTest
{
    @Test public void testPathCorrect()
    {
        assertThat("tradehero://reset-portfolio").isEqualTo(new ResetPortfolioIntent().getUriPath());
    }
}
