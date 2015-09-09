package com.tradehero.th.fragments.billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.billing.THProductDetail;

public class ProductDetailRecyclerAdapter extends TypedRecyclerAdapter<THProductDetail>
{
    public ProductDetailRecyclerAdapter()
    {
        super(THProductDetail.class, new TypedRecyclerComparator<THProductDetail>()
        {
            @Override public int compare(THProductDetail o1, THProductDetail o2)
            {
                return o1.getDisplayOrder() - o2.getDisplayOrder();
            }
        });
    }

    @Override public TypedViewHolder<THProductDetail> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new THProductDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.store_recycler_item, parent, false));
    }

    public static class THProductDetailViewHolder extends TypedViewHolder<THProductDetail>
    {
        @Bind(R.id.icon) ImageView img;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.description) TextView desc;
        @Bind(R.id.price) TextView price;

        public THProductDetailViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(THProductDetail thProductDetail)
        {
            THToast.show(thProductDetail.getDescription());
            img.setImageResource(thProductDetail.getIconResId());
            title.setText(thProductDetail.getDescription());
            price.setText(thProductDetail.getPriceText());
        }
    }
}
