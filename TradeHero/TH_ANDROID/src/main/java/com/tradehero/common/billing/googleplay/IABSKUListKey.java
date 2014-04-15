package com.tradehero.common.billing.googleplay;

import android.os.Bundle;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.persistence.AbstractStringDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:30 PM To change this template use File | Settings | File Templates. */
public class IABSKUListKey extends AbstractStringDTOKey implements ProductIdentifierListKey
{
    public static final String TAG = IABSKUListKey.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = IABSKUListKey.class.getName() + ".key";
    public static final String KEY_ALL = "ALL";

    public static IABSKUListKey getInApp()
    {
        return new IABSKUListKey(IABConstants.ITEM_TYPE_INAPP);
    }

    public static IABSKUListKey getSubs()
    {
        return new IABSKUListKey(IABConstants.ITEM_TYPE_SUBS);
    }

    public static IABSKUListKey getAll()
    {
        return new IABSKUListKey(KEY_ALL);
    }

    //<editor-fold desc="Constructors">
    public IABSKUListKey(Bundle args)
    {
        super(args);
    }

    public IABSKUListKey(String key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
