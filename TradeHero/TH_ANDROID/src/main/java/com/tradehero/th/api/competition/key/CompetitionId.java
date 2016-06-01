package com.ayondo.academy.api.competition.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

public class CompetitionId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = CompetitionId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public CompetitionId(Integer key)
    {
        super(key);
    }

    public CompetitionId(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[CompetitionId key=%s]", key);
    }
}
