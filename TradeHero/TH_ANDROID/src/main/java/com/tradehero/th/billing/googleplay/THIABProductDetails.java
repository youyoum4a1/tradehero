package com.tradehero.th.billing.googleplay;

import com.android.internal.util.Predicate;
import com.tradehero.common.billing.googleplay.BaseIABProductDetails;
import com.tradehero.th.R;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:40 PM To change this template use File | Settings | File Templates. */
public class THIABProductDetails extends BaseIABProductDetails
{
    public static final String TAG = THIABProductDetails.class.getSimpleName();

    public static final String DOMAIN_VIRTUAL_DOLLAR = "virtualDollar";
    public static final String DOMAIN_FOLLOW_CREDITS = "followCredits";
    public static final String DOMAIN_STOCK_ALERTS = "stockAlerts";
    public static final String DOMAIN_RESET_PORTFOLIO = "resetPortfolio";

    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public boolean hasRibbon = false;
    public int iconRibbonResId = R.drawable.default_image;
    public String domain;

    //<editor-fold desc="Constructors">
    public THIABProductDetails(String itemType, String jsonSkuDetails) throws JSONException
    {
        super(itemType, jsonSkuDetails);
    }

    public THIABProductDetails(String jsonSkuDetails) throws JSONException
    {
        super(jsonSkuDetails);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "ThSkuDetails:" + json;
    }

    public static Predicate<THIABProductDetails> getPredicateIsOfCertainDomain(final String domain)
    {
        return new Predicate<THIABProductDetails>()
        {
            @Override public boolean apply(THIABProductDetails THIABProductDetails)
            {
                return THIABProductDetails != null && (THIABProductDetails.domain == null ? domain == null : THIABProductDetails.domain.equals(domain));
            }
        };
    }
}
