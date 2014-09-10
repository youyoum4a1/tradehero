package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABProductIdentifierFetcherHolder
    extends BaseIABProductIdentifierFetcherHolder<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductIdentifierFetcher,
        IABException>
    implements THIABProductIdentifierFetcherHolder
{
    @NotNull protected final Provider<THIABProductIdentifierFetcher> thiabProductIdentifierFetcherProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABProductIdentifierFetcherHolder(
            @NotNull Provider<THIABProductIdentifierFetcher> thiabProductIdentifierFetcherProvider
    )
    {
        super();
        this.thiabProductIdentifierFetcherProvider = thiabProductIdentifierFetcherProvider;
    }
    //</editor-fold>

    @Override protected THIABProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return thiabProductIdentifierFetcherProvider.get();
    }
}
