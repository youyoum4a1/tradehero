package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 6:05 PM Copyright (c) TradeHero
 */
public class DiscussionKey extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = DiscussionKey.class.getName() + ".key";

    public DiscussionKey(Integer key)
    {
        super(key);
    }

    public DiscussionKey(Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
