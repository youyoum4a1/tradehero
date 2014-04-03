package com.tradehero.th.api.notification;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = NotificationKey.class.getName() + ".key";

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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
