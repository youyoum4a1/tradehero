package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StoreItemHasFurtherTest
{
    @Inject Context context;
    private StoreItemClickableView storeItemHasFurther;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        storeItemHasFurther = new StoreItemClickableView(context);
        storeItemHasFurther.title = new TextView(context);
        storeItemHasFurther.icon = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemHasFurtherDTO1()
    {
        storeItemHasFurther.display(new StoreItemDTO(1, price, description));
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemHasFurtherDTO2()
    {
        storeItemHasFurther.display(new StoreItemClickableDTO(
                R.string.cancel,
                R.drawable.default_image));
    }

    @Test public void testOkWhenPassingStoreItemHasFurtherDTO()
    {
        assertThat(storeItemHasFurther.title.getText()).isEqualTo("");
        assertThat(storeItemHasFurther.icon.getDrawable()).isNull();

        storeItemHasFurther.display(new StoreItemHasFurtherDTO(
                R.string.cancel,
                R.drawable.default_image,
                //HeroManagerFragment.class,
                null));

        assertThat(storeItemHasFurther.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemHasFurther.icon.getDrawable()).isNotNull();
    }
}
