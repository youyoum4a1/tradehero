package com.tradehero.th.fragments.billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.fragments.billing.store.StoreItemDisplayDTO;

public class StoreItemAdapter extends TypedRecyclerAdapter<StoreItemDisplayDTO>
{
    public StoreItemAdapter()
    {
        super(StoreItemDisplayDTO.class, new TypedRecyclerComparator<StoreItemDisplayDTO>()
        {
            @Override public int compare(StoreItemDisplayDTO o1, StoreItemDisplayDTO o2)
            {
                return o1.displayOrder - o2.displayOrder;
            }
        });
    }

    @Override public TypedViewHolder<StoreItemDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new StoreItemDisplayDTOViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.store_recycler_item, parent, false));
    }

    public static class StoreItemDisplayDTOViewHolder extends TypedViewHolder<StoreItemDisplayDTO>
    {
        @Bind(R.id.icon) ImageView img;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.description) TextView desc;
        @Bind(R.id.price) TextView price;

        public StoreItemDisplayDTOViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(StoreItemDisplayDTO storeItem)
        {
            img.setImageResource(storeItem.iconResId);
            title.setText(storeItem.titleResId);
            desc.setText(storeItem.descriptionResId);
            price.setText(storeItem.priceText);
        }
    }
}