package com.tradehero.th.utils;

import android.graphics.Color;
import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class ColorUtilsTest
{
    @Test public void maxRedValue_shouldBeLargeEnough()
    {
        assertThat(ColorUtils.MAX_RED_VALUE, equalTo(200));
    }

    @Test public void maxGreenValue_shouldBeLargeEnough()
    {
        assertTrue(ColorUtils.MAX_GREEN_VALUE >= 200);
    }

    @Test public void getColorForPercentage_onMinus1_shouldReturnRed()
    {
        int colorMinus1 = ColorUtils.getColorForPercentage(-1);
        assertThat(colorMinus1, equalTo(Color.rgb(ColorUtils.MAX_RED_VALUE, 0, 0)));
    }

    @Test public void getColorForPercentage_onMinus05_shouldReturnHalfRed()
    {
        int colorMinus1 = ColorUtils.getColorForPercentage(-0.5f);
        assertThat(colorMinus1, equalTo(Color.rgb(ColorUtils.MAX_RED_VALUE / 2, 0, 0)));
    }

    @Test public void getColorForPercentage_on0_shouldReturnBlack()
    {
        int color0 = ColorUtils.getColorForPercentage(0);
        assertThat(color0, equalTo(Color.rgb(0, 0, 0)));
    }

    @Test public void getColorForPercentage_on05_shouldReturnHalfGreen()
    {
        int color1 = ColorUtils.getColorForPercentage(0.5f);
        assertThat(color1, equalTo(Color.rgb(0, ColorUtils.MAX_GREEN_VALUE / 2, 0)));
    }

    @Test public void getColorForPercentage_on1_shouldReturnGreen()
    {
        int color1 = ColorUtils.getColorForPercentage(1);
        assertThat(color1, equalTo(Color.rgb(0, ColorUtils.MAX_GREEN_VALUE, 0)));
    }
}
