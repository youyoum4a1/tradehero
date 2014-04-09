package com.tradehero.th.api.messages;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessageKey extends AbstractIntegerDTOKey
{

    private static final String BUNDLE_KEY_KEY = MessageKey.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public MessageKey(Integer key)
    {
        super(key);
    }

    public MessageKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
