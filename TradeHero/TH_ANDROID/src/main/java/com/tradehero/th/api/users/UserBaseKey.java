package com.tradehero.th.api.users;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.route.RouteProperty;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.users.specific.UserBaseKeyConstants;
import org.jetbrains.annotations.NotNull;

public class UserBaseKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = UserBaseKey.class.getName() + ".key";

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
    public Integer getUserId()
    {
        return key;
    }

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

    @JsonIgnore public boolean isOfficialAccount()
    {
        return key.equals(UserBaseKeyConstants.OFFICIAL_TRADEHERO)
                || key.equals(UserBaseKeyConstants.OFFICIAL_COMMUNITY_MANAGER)
                || key.equals(UserBaseKeyConstants.OFFICIAL_TRADE_MASTER)
                || key.equals(UserBaseKeyConstants.OFFICIAL_ACCOUNT_4)
                || key.equals(UserBaseKeyConstants.OFFICIAL_ACCOUNT_5)
                || key.equals(UserBaseKeyConstants.OFFICIAL_ACCOUNT_6);
    }
}
