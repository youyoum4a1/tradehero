package com.tradehero.th.api.users;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 6:39 PM To change this template use File | Settings | File Templates. */
public class UserBaseKey extends AbstractIntegerDTOKey
{
    public final static String TAG = UserBaseKey.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = UserBaseKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserBaseKey(Integer key)
    {
        super(key);
    }

    public UserBaseKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
