package com.tradehero.th.api.competition.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;


public class HelpVideoId extends AbstractIntegerDTOKey
{
    public static final String TAG = HelpVideoId.class.getSimpleName();
    public static final String BUNDLE_KEY_KEY = HelpVideoId.class.getName() + ".key";

    public HelpVideoId(Integer key)
    {
        super(key);
    }

    public HelpVideoId(Bundle args)
    {
        super(args);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[HelpVideoId key=%s]", key);
    }
}
