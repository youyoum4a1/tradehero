package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 6:05 PM Copyright (c) TradeHero
 */
public class DiscussionKey implements DTOKey
{
    static final String BUNDLE_KEY_TYPE = DiscussionKey.class.getName() + ".type";
    static final String BUNDLE_KEY_ID = DiscussionKey.class.getName() + ".id";

    public final DiscussionType type;
    public final Integer id;

    protected DiscussionKey(DiscussionType type, Integer id)
    {
        this.type = type;
        this.id = id;
    }

    public Bundle getArgs()
    {
        Bundle bundle = new Bundle();
        putParameters(bundle);
        return bundle;
    }

    protected void putParameters(Bundle args)
    {
        args.putString(BUNDLE_KEY_TYPE, type.description);
        args.putInt(BUNDLE_KEY_ID, id);
    }
}
