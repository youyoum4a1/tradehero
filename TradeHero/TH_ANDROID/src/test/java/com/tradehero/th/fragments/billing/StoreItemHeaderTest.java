package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemTitleDTO;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StoreItemHeaderTest
{
    @Inject Context context;
    private StoreItemHeader storeItemHeader;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        storeItemHeader = new StoreItemHeader(context);
        storeItemHeader.title = new TextView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemTitleDTO()
    {
        storeItemHeader.display(new StoreItemDTO(1));
    }

    @Test public void testOkWhenPassingStoreItemTitleDTO()
    {
        assertThat(storeItemHeader.title.getText()).isEqualTo("");

        storeItemHeader.display(new StoreItemTitleDTO(R.string.cancel));

        assertThat(storeItemHeader.title.getText()).isEqualTo("Cancel");
    }
}
