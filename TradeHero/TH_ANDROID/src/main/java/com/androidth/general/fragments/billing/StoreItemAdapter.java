package com.androidth.general.fragments.billing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.fragments.billing.store.StoreItemDisplayDTO;
import com.androidth.general.fragments.billing.store.StoreItemProductDisplayDTO;
import com.androidth.general.fragments.billing.store.StoreItemRestoreDisplayDTO;

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
        @BindView(R.id.icon) ImageView img;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView desc;
        @BindView(R.id.price) TextView price;

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