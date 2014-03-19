package com.tradehero.th.billing.googleplay;

import com.android.internal.util.Predicate;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.th.R;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:40 PM To change this template use File | Settings | File Templates. */
public class THIABProductDetail extends BaseIABProductDetail
{
    public static final String TAG = THIABProductDetail.class.getSimpleName();

    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public boolean hasRibbon = false;
    public int iconRibbonResId = R.drawable.default_image;
    public String domain;

    //<editor-fold desc="Constructors">
    public THIABProductDetail(String itemType, String jsonSkuDetails) throws JSONException
    {
        super(itemType, jsonSkuDetails);
    }

    public THIABProductDetail(String jsonSkuDetails) throws JSONException
    {
        super(jsonSkuDetails);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "ThSkuDetails:" + json;
    }

    public static Predicate<THIABProductDetail> getPredicateIsOfCertainDomain(final String domain)
    {
        return new Predicate<THIABProductDetail>()
        {
            @Override public boolean apply(THIABProductDetail THIABProductDetail)
            {
                return THIABProductDetail != null && (THIABProductDetail.domain == null ? domain == null : THIABProductDetail.domain.equals(domain));
            }
        };
    }
}
