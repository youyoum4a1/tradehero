package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.store.StoreItemClickableDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class StoreItemHasFurtherTest
{
    @Inject Context context;
    private StoreItemHasFurther storeItemHasFurther;

    @Before public void setUp()
    {
        storeItemHasFurther = new StoreItemHasFurther(context);
        storeItemHasFurther.title = new TextView(context);
        storeItemHasFurther.icon = new ImageView(context);
    }

    @Test(expected = ClassCastException.class)
    public void testCrashesWhenPassingNotStoreItemHasFurtherDTO1()
    {
        storeItemHasFurther.display(new StoreItemDTO(1));
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
                HeroManagerFragment.class));

        assertThat(storeItemHasFurther.title.getText()).isEqualTo("Cancel");
        assertThat(storeItemHasFurther.icon.getDrawable()).isNotNull();
    }
}
