package com.tradehero.th.api.competition;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionId extends AbstractIntegerDTOKey
{
    public static final String TAG = CompetitionId.class.getSimpleName();

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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[CompetitionId key=%s]", key);
    }
}
