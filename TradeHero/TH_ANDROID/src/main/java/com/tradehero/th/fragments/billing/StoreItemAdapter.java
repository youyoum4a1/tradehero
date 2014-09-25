package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import com.tradehero.th.fragments.billing.store.StoreItemTitleDTO;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class StoreItemAdapter extends ArrayAdapter<StoreItemDTO>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_LIKE_BUTTON = 1;
    public static final int VIEW_TYPE_HAS_FURTHER = 2;

    @NotNull private HashMap<Integer, Integer> viewTypeToLayoutId;

    //<editor-fold desc="Constructors">
    public StoreItemAdapter(Context context)
    {
        super(context, 0);
        viewTypeToLayoutId = new HashMap<>();
        buildViewTypeMap();
    }
    //</editor-fold>

    private void buildViewTypeMap()
    {
        viewTypeToLayoutId.put(VIEW_TYPE_HEADER, R.layout.store_item_header);
        viewTypeToLayoutId.put(VIEW_TYPE_LIKE_BUTTON, R.layout.store_item_like_button);
        viewTypeToLayoutId.put(VIEW_TYPE_HAS_FURTHER, R.layout.store_item_has_further);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public int getViewTypeCount()
    {
        return viewTypeToLayoutId.size();
    }

    @Override public int getItemViewType(int position)
    {
        int viewType;
        StoreItemDTO storeItemDTO = getItem(position);
        if (storeItemDTO instanceof StoreItemTitleDTO)
        {
            viewType = VIEW_TYPE_HEADER;
        }
        else if (storeItemDTO instanceof StoreItemPromptPurchaseDTO)
        {
            viewType = VIEW_TYPE_LIKE_BUTTON;
        }
        else if (storeItemDTO instanceof StoreItemHasFurtherDTO)
        {
            viewType = VIEW_TYPE_HAS_FURTHER;
        }
        else
        {
            throw new IllegalArgumentException("Unhandled dto type " + storeItemDTO);
        }
        return viewType;
    }

    public int getLayoutIdFromPosition(int position)
    {
        return viewTypeToLayoutId.get(getItemViewType(position));
    }

    @Override public View getView(int position, View view, ViewGroup viewGroup)
    {
        int layoutToInflate = getLayoutIdFromPosition(position);
        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(layoutToInflate, viewGroup, false);
        }

        //noinspection unchecked
        ((DTOView<StoreItemDTO>) view).display(getItem(position));

        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }
}
