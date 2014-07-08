package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.store.StoreItemClickableDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class StoreItemClickableTest
{
    @Inject Context context;
    private StoreItemClickable storeItemClickable;

    @Before public void setUp()
    {
        storeItemClickable = new StoreItemClickable(context);
        storeItemClickable.title = new TextView(context);
        storeItemClickable.icon = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemClickableDTO1()
    {
        storeItemClickable.display(new StoreItemDTO(1));
    }

    @Test public void testOkWhenPassingStoreItemClickableDTO()
    {
        assertThat(storeItemClickable.title.getText()).isEqualTo("");
        assertThat(storeItemClickable.icon.getDrawable()).isNull();

        storeItemClickable.display(new StoreItemClickableDTO(
                R.string.cancel,
                R.drawable.default_image));

        assertThat(storeItemClickable.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemClickable.icon.getDrawable()).isNotNull();
    }
}
