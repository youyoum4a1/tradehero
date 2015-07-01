package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.billing.THProductDetail;
import timber.log.Timber;

public class StoreProductDetailView<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends ProductDetailView<ProductIdentifierType, ProductDetailType>
{
    @Bind(R.id.ic_deliverable) protected ImageView icDeliverable;
    @Bind(R.id.further_description) protected TextView furtherDescription;

    //<editor-fold desc="Constructors">
    public StoreProductDetailView(Context context)
    {
        super(context);
    }

    public StoreProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(@NonNull ProductDetailType productDetail)
    {
        super.display(productDetail);

        if (icDeliverable != null)
        {
            try
            {
                icDeliverable.setImageResource(productDetail.getIconResId());
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }

        if (furtherDescription != null)
        {
            furtherDescription.setVisibility(productDetail.getHasFurtherDetails() ? VISIBLE : GONE);
            furtherDescription.setText(productDetail.getFurtherDetailsResId());
        }
    }
}
