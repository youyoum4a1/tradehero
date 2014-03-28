package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by xavier on 3/28/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BaseSamsungInventoryFetcherTest
{
    private BaseSamsungInventoryFetcher inventoryFetcher;

    @Before public void setUp()
    {
        inventoryFetcher = new SimpleBaseSamsungInventoryFetcher(
                Robolectric.getShadowApplication().getApplicationContext(),
                SamsungIapHelper.IAP_MODE_TEST_SUCCESS);
    }

    @After public void tearDown()
    {
    }

    @Test public void testCanFetchItems()
    {
        inventoryFetcher.fetchInventory(1);
    }
}
