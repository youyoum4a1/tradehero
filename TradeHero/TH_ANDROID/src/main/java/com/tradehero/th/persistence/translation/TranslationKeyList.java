package com.tradehero.th.persistence.translation;

import java.util.ArrayList;
import java.util.Collection;

public class TranslationKeyList extends ArrayList<TranslationKey>
{
    //<editor-fold desc="Constructors">
    public TranslationKeyList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public TranslationKeyList()
    {
        super();
    }

    public TranslationKeyList(Collection<? extends TranslationKey> c)
    {
        super(c);
    }
    //</editor-fold>
}
