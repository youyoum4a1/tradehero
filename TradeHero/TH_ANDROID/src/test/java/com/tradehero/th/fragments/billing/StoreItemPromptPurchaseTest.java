package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.billing.ProductIdentifierDomain;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StoreItemPromptPurchaseTest
{
    @Inject Context context;
    private StoreItemClickableView storeItemPromptPurchase;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        storeItemPromptPurchase = new StoreItemClickableView(context);
        storeItemPromptPurchase.title = new TextView(context);
        storeItemPromptPurchase.icon = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemPromptPurchaseDTO1()
    {
        storeItemPromptPurchase.display(new StoreItemDTO(1, price, description));
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemPromptPurchaseDTO2()
    {
        storeItemPromptPurchase.display(new StoreItemClickableDTO(
                R.string.cancel,
                R.drawable.default_image));
    }

    @Test public void testOkWhenPassingStoreItemPromptPurchaseDTO()
    {
        assertThat(storeItemPromptPurchase.title.getText()).isEqualTo("");
        assertThat(storeItemPromptPurchase.icon.getDrawable()).isNull();

        storeItemPromptPurchase.display(new StoreItemPromptPurchaseDTO(
                R.string.cancel,
                R.drawable.default_image,
                ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS));

        assertThat(storeItemPromptPurchase.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemPromptPurchase.icon.getDrawable()).isNotNull();
    }
}
