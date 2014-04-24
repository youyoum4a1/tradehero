package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessageHeaderId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_KEY = MessageHeaderId.class.getName() + ".key";

    //public boolean markDeleted = false;

    //<editor-fold desc="Constructors">
    public MessageHeaderId(Integer key)
    {
        super(key);
    }

    public MessageHeaderId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
