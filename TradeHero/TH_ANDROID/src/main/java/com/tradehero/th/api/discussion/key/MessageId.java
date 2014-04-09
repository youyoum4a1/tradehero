package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessageId extends AbstractIntegerDTOKey
{

    private static final String BUNDLE_KEY_KEY = MessageId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public MessageId(Integer key)
    {
        super(key);
    }

    public MessageId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
