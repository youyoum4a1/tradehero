package com.tradehero.th.api.discussion;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.Querylizable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thonguyen on 4/4/14.
 */
public class DiscussionListKey extends AbstractIntegerDTOKey
    implements Querylizable<String>
{
    private static final String BUNDLE_KEY_KEY = DiscussionListKey.class.getName() + ".key";
    private static final Integer DEFAULT_DISCUSSION_LIST_KEY_KEY = Integer.MIN_VALUE;

    //<editor-fold desc="Constructors">
    public DiscussionListKey(Integer key)
    {
        super(key);
    }

    public DiscussionListKey(Bundle args)
    {
        super(args);
    }

    public DiscussionListKey()
    {
        this(DEFAULT_DISCUSSION_LIST_KEY_KEY);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public Map<String, Object> toMap()
    {
        return new HashMap<>();
    }
}
