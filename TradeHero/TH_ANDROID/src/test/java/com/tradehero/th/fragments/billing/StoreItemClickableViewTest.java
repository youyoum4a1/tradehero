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
public class StoreItemClickableViewTest
{
    @Inject Context context;
    private StoreItemClickableView storeItemClickableView;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        storeItemClickableView = new StoreItemClickableView(context);
        storeItemClickableView.title = new TextView(context);
        storeItemClickableView.icon = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemClickableDTO1()
    {
        storeItemClickableView.display(new StoreItemDTO(1, price, description));
    }

    @Test public void testOkWhenPassingStoreItemClickableDTO()
    {
        assertThat(storeItemClickableView.title.getText()).isEqualTo("");
        assertThat(storeItemClickableView.icon.getDrawable()).isNull();

        storeItemClickableView.display(new StoreItemClickableDTO(
                R.string.cancel,
                R.drawable.default_image));

        assertThat(storeItemClickableView.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemClickableView.icon.getDrawable()).isNotNull();
    }
}
