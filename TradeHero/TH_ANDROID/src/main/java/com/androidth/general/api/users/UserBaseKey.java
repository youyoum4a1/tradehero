package com.androidth.general.api.users;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;
import com.androidth.general.api.portfolio.key.PortfolioCompactListKey;
import com.androidth.general.api.users.specific.UserBaseKeyConstants;

public class UserBaseKey extends AbstractIntegerDTOKey implements PortfolioCompactListKey
{
    private static final String BUNDLE_KEY_KEY = UserBaseKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public UserBaseKey()
    {
        super();
    }

    public UserBaseKey(@NonNull Integer key)
    {
        super(key);
    }

    public UserBaseKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @RouteProperty
    public Integer getUserId()
    {
        return key;
    }

    @RouteProperty
    public void setUserId(int userId)
    {
        this.key = userId;
    }

    @NonNull @Override public String getBundleKey()
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

    @JsonIgnore public boolean isOfficialAccount()
    {
        return UserBaseKeyConstants.isOfficialId(key);
    }
}
