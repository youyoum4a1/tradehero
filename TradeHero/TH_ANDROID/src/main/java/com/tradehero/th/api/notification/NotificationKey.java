package com.ayondo.academy.api.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class NotificationKey extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = NotificationKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public NotificationKey(Integer key)
    {
        super(key);
    }

    public NotificationKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
