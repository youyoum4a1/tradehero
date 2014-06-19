package com.tradehero.th.api.users;

import android.os.Bundle;
import com.thoj.route.RouteProperty;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class UserBaseKey extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = UserBaseKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserBaseKey()
    {
        super();
    }

    public UserBaseKey(@NotNull Integer key)
    {
        super(key);
    }

    public UserBaseKey(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @RouteProperty
    public void setUserId(int userId)
    {
        this.key = userId;
    }

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
        return String.format("[UserBaseKey key=%d]", key);
    }
}
