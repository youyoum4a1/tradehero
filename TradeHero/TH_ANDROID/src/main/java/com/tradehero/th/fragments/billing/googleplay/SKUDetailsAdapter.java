package com.tradehero.th.fragments.billing.googleplay;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
abstract public class SKUDetailsAdapter<
        IABProductDetailType extends BaseIABProductDetail,
        SKUDetailViewType extends SKUDetailView<IABProductDetailType>>
    extends ProductDetailAdapter<
        IABSKU,
        IABProductDetailType,
        SKUDetailViewType>
{
    public static final String TAG = SKUDetailsAdapter.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SKUDetailsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, layoutResourceId, skuDomain);
    }
    //</editor-fold>
}
