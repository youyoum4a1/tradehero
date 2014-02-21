package com.tradehero.common.billing.googleplay;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:30 PM To change this template use File | Settings | File Templates. */
public class IABSKUListType extends AbstractStringDTOKey
{
    public static final String TAG = IABSKUListType.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = IABSKUListType.class.getName() + ".key";

    public static IABSKUListType getInApp()
    {
        return new IABSKUListType(IABConstants.ITEM_TYPE_INAPP);
    }

    public static IABSKUListType getSubs()
    {
        return new IABSKUListType(IABConstants.ITEM_TYPE_SUBS);
    }

    //<editor-fold desc="Constructors">
    public IABSKUListType(Bundle args)
    {
        super(args);
    }

    public IABSKUListType(String key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
