package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BillingUtils
{
    @NonNull public static String getStoreName()
    {
        return "Amazon Store";
    }

    @NonNull protected static List<String> getPurchaseReportStrings(@Nullable THProductPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            // TODO
        }
        return reported;
    }
}
