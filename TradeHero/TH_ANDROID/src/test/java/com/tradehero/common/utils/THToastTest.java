package com.tradehero.common.utils;

import android.widget.TextView;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.misc.exception.THException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class THToastTest
{
    @Test
    public void verifyToastTHException() throws Exception
    {
        THException thException = new THException((String) null);
        THToast.show(thException);

        assertThat(
                ((TextView) ShadowToast
                        .getLatestToast()
                        .getView()
                        .findViewById(android.R.id.message)) // FIXME apparently it is not found
                        .getText())
                .isEqualTo(THApp.context().getString(R.string.error_unknown));
    }
}