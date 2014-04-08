package com.tradehero.th.api.messages;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by wangliang on 14-4-4.
 *
 * message type
 */
public class TypedMessageKey extends AbstractIntegerDTOKey
{
    public static final int MESSAGE_TYPE_ALL = -1;

    private static final String BUNDLE_KEY = TypedMessageKey.class.getSimpleName()+".key";

    public TypedMessageKey(Integer key)
    {
        super(key);
    }

    public TypedMessageKey(Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
