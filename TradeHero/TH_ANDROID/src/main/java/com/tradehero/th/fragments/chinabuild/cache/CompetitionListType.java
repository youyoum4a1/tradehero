package com.tradehero.th.fragments.chinabuild.cache;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class CompetitionListType extends AbstractStringDTOKey
{
    public final static String BUNDLE_KEY_KEY = CompetitionListType.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    public int page = 1;
    public int PER_PAGE = 20;

    //<editor-fold desc="Constructors">
    public CompetitionListType()
    {
        this(DEFAULT_KEY);
    }

    public CompetitionListType(Bundle args)
    {
        super(args);
    }

    public CompetitionListType(String key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
