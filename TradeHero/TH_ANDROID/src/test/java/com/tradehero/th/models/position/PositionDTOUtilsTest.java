package com.ayondo.academy.models.position;

import android.content.Context;
import android.widget.TextView;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PositionDTOUtilsTest
{
    @Inject Context context;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void shouldNegativeAndPositiveValueShouldHaveDifferentDirectionArrows()
    {
        TextView positiveTextView = new TextView(context);
        TextView negativeTextView = new TextView(context);

        PositionDTOUtils.setROILook(positiveTextView, -10.0);
        PositionDTOUtils.setROILook(negativeTextView, 10.0);

        assertThat(positiveTextView.getText().toString().charAt(0)).isNotEqualTo(negativeTextView.getText().toString().charAt(0));
    }
}