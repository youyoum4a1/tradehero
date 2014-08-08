package com.tradehero.th.models.position;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class PositionDTOUtilsTest
{
    @Inject Context context;
    @Inject PositionDTOUtils positionDTOUtils;

    @Test public void shouldNegativeAndPositiveValueShouldHaveDifferentDirectionArrows()
    {
        TextView positiveTextView = new TextView(context);
        TextView negativeTextView = new TextView(context);

        positionDTOUtils.setROILook(positiveTextView, -10.0);
        positionDTOUtils.setROILook(negativeTextView, 10.0);

        assertThat(positiveTextView.getText().toString().charAt(0)).isNotEqualTo(negativeTextView.getText().toString().charAt(0));
    }
}