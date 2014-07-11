package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.thm.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.billing.store.StoreItemClickableDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class StoreItemPromptPurchaseTest
{
    @Inject Context context;
    private StoreItemPromptPurchase storeItemPromptPurchase;

    @Before public void setUp()
    {
        storeItemPromptPurchase = new StoreItemPromptPurchase(context);
        storeItemPromptPurchase.title = new TextView(context);
        storeItemPromptPurchase.icon = new ImageView(context);
        storeItemPromptPurchase.imageButton = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemPromptPurchaseDTO1()
    {
        storeItemPromptPurchase.display(new StoreItemDTO(1));
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
        assertThat(storeItemPromptPurchase.imageButton.getDrawable()).isNull();

        storeItemPromptPurchase.display(new StoreItemPromptPurchaseDTO(
                R.string.cancel,
                R.drawable.default_image,
                R.drawable.default_image,
                ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS));

        assertThat(storeItemPromptPurchase.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemPromptPurchase.icon.getDrawable()).isNotNull();
        assertThat(storeItemPromptPurchase.imageButton.getDrawable()).isNotNull();
    }
}
