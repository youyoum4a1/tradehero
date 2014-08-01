package com.tradehero.common.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.misc.exception.THException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowToast;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class THToastTest
{
    @Test
    public void verifyToastTHException() throws Exception
    {
        THException thException = new THException((String) null);
        THToast.show(thException);

        assertThat(ShadowToast.getLatestToast()).isNull();
    }
}