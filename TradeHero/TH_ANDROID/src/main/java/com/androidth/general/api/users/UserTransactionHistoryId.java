package com.androidth.general.api.users;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class UserTransactionHistoryId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = UserTransactionHistoryId.class.getSimpleName() + ".key";

    //<editor-fold desc="Constructors">
    public UserTransactionHistoryId(Integer key)
    {
        super(key);
    }

    public UserTransactionHistoryId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
