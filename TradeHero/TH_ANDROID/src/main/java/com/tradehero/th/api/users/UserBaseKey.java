package com.tradehero.th.api.users;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;


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

    public boolean isValid()
    {
        return key > 0;
    }

    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
