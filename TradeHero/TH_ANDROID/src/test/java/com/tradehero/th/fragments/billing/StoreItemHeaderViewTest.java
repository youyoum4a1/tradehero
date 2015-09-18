package com.tradehero.th.fragments.billing;

import android.content.Context;
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
public class StoreItemHeaderViewTest
{
    @Inject Context context;
    private StoreItemHeaderView storeItemHeaderView;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        storeItemHeaderView = new StoreItemHeaderView(context);
        storeItemHeaderView.title = new TextView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemTitleDTO()
    {
        storeItemHeaderView.display(new StoreItemDTO(1, price, description));
    }

    @Test public void testOkWhenPassingStoreItemTitleDTO()
    {
        assertThat(storeItemHeaderView.title.getText()).isEqualTo("");

        storeItemHeaderView.display(new StoreItemTitleDTO(R.string.cancel));

        assertThat(storeItemHeaderView.title.getText()).isEqualTo("Cancel");
    }
}
