package com.tradehero.common.billing.samsung;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class SamsungSKUListKey extends AbstractStringDTOKey
        implements ProductIdentifierListKey
{
    public final static String BUNDLE_KEY_KEY = SamsungSKUListKey.class.getName() + ".key";

    public SamsungSKUListKey(String key)
    {
        super(key);
    }

    public SamsungSKUListKey(Bundle args)
    {
        super(args);
    }

    @JsonIgnore
    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    public static SamsungSKUListKey getConsumableKey()
    {
        return new SamsungSKUListKey(SamsungIapHelper.ITEM_TYPE_CONSUMABLE);
    }

    public static SamsungSKUListKey getNonConsumableKey()
    {
        return new SamsungSKUListKey(SamsungIapHelper.ITEM_TYPE_NON_CONSUMABLE);
    }

    public static SamsungSKUListKey getSubscriptionKey()
    {
        return new SamsungSKUListKey(SamsungIapHelper.ITEM_TYPE_SUBSCRIPTION);
    }

    public static SamsungSKUListKey getAllKey()
    {
        return new SamsungSKUListKey(SamsungIapHelper.ITEM_TYPE_ALL);
    }
}
