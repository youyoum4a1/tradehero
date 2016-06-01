package com.ayondo.academy.api.competition.key;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class HelpVideoId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = HelpVideoId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public HelpVideoId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
