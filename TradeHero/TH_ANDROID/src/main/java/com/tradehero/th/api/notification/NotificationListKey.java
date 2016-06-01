package com.ayondo.academy.api.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.ayondo.academy.api.Querylizable;
import java.util.HashMap;
import java.util.Map;

public class NotificationListKey extends AbstractIntegerDTOKey
    implements Querylizable<String>
{
    private static final Integer ALL_NOTIFICATIONS_KEY = Integer.MIN_VALUE;
    private static final String BUNDLE_KEY_KEY = NotificationListKey.class.getName() + ".key";

    public NotificationListKey()
    {
        this(ALL_NOTIFICATIONS_KEY);
    }

    /**
     * Key will be pushId
     * @param key
     */
    public NotificationListKey(Integer key)
    {
        super(key);
    }

    public NotificationListKey(Bundle args)
    {
        super(args);
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public Map<String, Object> toMap()
    {
        return new HashMap<>();
    }
}
