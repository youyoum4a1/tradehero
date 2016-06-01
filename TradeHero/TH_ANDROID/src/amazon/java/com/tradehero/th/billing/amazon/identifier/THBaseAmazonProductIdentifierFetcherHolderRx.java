package com.ayondo.academy.billing.amazon.identifier;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.identifier.BaseAmazonProductIdentifierFetcherHolderRx;
import javax.inject.Inject;

public class THBaseAmazonProductIdentifierFetcherHolderRx
        extends BaseAmazonProductIdentifierFetcherHolderRx<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList>
        implements THAmazonProductIdentifierFetcherHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject THBaseAmazonProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonProductIdentifierFetcherRx createFetcher(int requestCode)
    {
        return new THBaseAmazonProductIdentifierFetcherRx(requestCode);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
