package com.androidth.general.common.billing.googleplay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifierListKey;
import com.androidth.general.common.persistence.AbstractStringDTOKey;

public class IABSKUListKey extends AbstractStringDTOKey implements ProductIdentifierListKey
{
    public static final String BUNDLE_KEY_KEY = IABSKUListKey.class.getName() + ".key";

    @NonNull public static IABSKUListKey getInApp()
    {
        return new IABSKUListKey(IABConstants.ITEM_TYPE_INAPP);
    }

    @NonNull public static IABSKUListKey getSubs()
    {
        return new IABSKUListKey(IABConstants.ITEM_TYPE_SUBS);
    }

    //<editor-fold desc="Constructors">
    public IABSKUListKey(Bundle args)
    {
        super(args);
    }

    public IABSKUListKey(@SkuTypeValue @NonNull String key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
