package com.tradehero.th.api.alert;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseKey;

public class AlertId implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_USER_ID = AlertId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_ALERT_ID = AlertId.class.getName() + ".alertId";

    public final Integer userId;
    public final Integer alertId;

    //<editor-fold desc="Constructors">
    public AlertId(final UserBaseKey userBaseKey, final Integer alertId)
    {
        this(userBaseKey.key, alertId);
    }

    public AlertId(Bundle args)
    {
        this.userId = args.containsKey(BUNDLE_KEY_USER_ID) ? args.getInt(BUNDLE_KEY_USER_ID) : null;
        this.alertId = args.containsKey(BUNDLE_KEY_ALERT_ID) ? args.getInt(BUNDLE_KEY_ALERT_ID) : null;
    }

    public AlertId(final Integer userId, final Integer alertId)
    {
        this.userId = userId;
        this.alertId = alertId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (userId == null ? 0 : userId.hashCode()) ^
                (alertId == null ? 0 : alertId.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof AlertId) && equals((AlertId) other);
    }

    public boolean equals(AlertId other)
    {
        return (other != null) &&
                (userId == null ? other.userId == null : userId.equals(other.userId)) &&
                (alertId == null ? other.alertId == null : alertId.equals(other.alertId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == AlertId.class)
        {
            return compareTo((AlertId) o);
        }
        return o.getClass().getName().compareTo(AlertId.class.getName());
    }

    public int compareTo(AlertId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        // TODO looks dangerous
        int followedIdComp = userId.compareTo(other.userId);
        if (followedIdComp != 0)
        {
            return followedIdComp;
        }

        return alertId.compareTo(other.alertId);
    }

    @JsonIgnore
    public boolean isValid()
    {
        return userId != null && alertId != null;
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_USER_ID) &&
                args.containsKey(BUNDLE_KEY_ALERT_ID);
    }

    private void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_USER_ID, userId);
        args.putInt(BUNDLE_KEY_ALERT_ID, alertId);
    }

    @JsonIgnore
    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore
    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[userId=%s; alertId=%s]", userId, alertId);
    }
}
