package com.ayondo.academy.fragments.billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.TypedRecyclerAdapter;
import com.ayondo.academy.fragments.billing.store.StoreItemDisplayDTO;
import com.ayondo.academy.fragments.billing.store.StoreItemProductDisplayDTO;
import com.ayondo.academy.fragments.billing.store.StoreItemRestoreDisplayDTO;

public class StoreItemAdapter extends TypedRecyclerAdapter<StoreItemDisplayDTO>
{
    public StoreItemAdapter()
    {
        super(StoreItemDisplayDTO.class, new TypedRecyclerComparator<StoreItemDisplayDTO>()
        {
            @Override public boolean areItemsTheSame(StoreItemDisplayDTO item1, StoreItemDisplayDTO item2)
            {
                return item1.titleResId == item2.titleResId;
            }

            @Override public int compare(StoreItemDisplayDTO o1, StoreItemDisplayDTO o2)
            {
                return o1.displayOrder - o2.displayOrder;
            }
        });
    }

    @Override public TypedViewHolder<StoreItemDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new StoreItemDisplayProductDTOViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.store_recycler_item, parent, false));
    }

    public static class StoreItemDisplayProductDTOViewHolder extends TypedViewHolder<StoreItemDisplayDTO>
    {
        @Bind(R.id.icon) ImageView img;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.description) TextView desc;
        @Bind(R.id.price) TextView price;

        public StoreItemDisplayProductDTOViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(StoreItemDisplayDTO storeItem)
        {
            img.setImageResource(storeItem.iconResId);
            title.setText(storeItem.titleResId);
            if (storeItem instanceof StoreItemProductDisplayDTO)
            {
                desc.setVisibility(View.VISIBLE);
                price.setVisibility(View.VISIBLE);
                desc.setText(((StoreItemProductDisplayDTO) storeItem).descriptionResId);
                price.setText(((StoreItemProductDisplayDTO) storeItem).priceText);
            }
            else if (storeItem instanceof StoreItemRestoreDisplayDTO)
            {
                price.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
            }
        }
    }
}