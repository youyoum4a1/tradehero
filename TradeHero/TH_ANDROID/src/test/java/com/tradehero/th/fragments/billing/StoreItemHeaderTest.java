package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemTitleDTO;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class StoreItemHeaderTest
{
    @Inject Context context;
    private StoreItemHeader storeItemHeader;

    @Before public void setUp()
    {
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
