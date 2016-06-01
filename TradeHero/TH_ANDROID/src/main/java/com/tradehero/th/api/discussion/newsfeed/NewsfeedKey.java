package com.ayondo.academy.api.discussion.newsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class NewsfeedKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = NewsfeedKey.class.getName() + ".key";

    public NewsfeedKey(int id)
    {
        super(id);
    }

    public NewsfeedKey(@NonNull Bundle args)
    {
        super(args);
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
