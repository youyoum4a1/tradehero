package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.inject.HierarchyInjector;

public class ContestItemAdapter extends ArrayAdapter<ContestPageDTO>
{
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_VIP = 1;

    @NonNull @LayoutRes private Integer[] typeToResIds;

    //<editor-fold desc="Constructors">
    public ContestItemAdapter(
            @NonNull Context context,
            @LayoutRes int vipViewResourceId,
            @LayoutRes int normalViewResourceId)
    {
        super(context, 0);
        typeToResIds = new Integer[2];
        typeToResIds[TYPE_VIP] = vipViewResourceId;
        typeToResIds[TYPE_NORMAL] = normalViewResourceId;
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return typeToResIds.length;
    }

    @Override public int getItemViewType(int position)
    {

        ContestPageDTO item = getItem(position);
        if (item instanceof ProviderContestPageDTO)
        {
            ProviderDTO providerDTO = ((ProviderContestPageDTO) item).providerDTO;
            if (providerDTO.vip != null && providerDTO.vip)
            {
                return TYPE_VIP;
            }
            else
            {
                return TYPE_NORMAL;
            }
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @LayoutRes public int getItemViewResId(int position)
    {
        return typeToResIds[getItemViewType(position)];
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getItemViewResId(position), viewGroup, false);
        }
        if (convertView instanceof DTOView)
        {
            //noinspection unchecked
            ContestPageDTO item = getItem(position);
            if(getCount()==1){
                if(item instanceof ProviderContestPageDTO){
                    ProviderDTO providerDTO = ((ProviderContestPageDTO) item).providerDTO;
                    if(!providerDTO.isUserEnrolled){
                        providerDTO.displayURL = providerDTO.singleImageUrl;
                        ((DTOView<ContestPageDTO>) convertView).display(getItem(position));
                    }else {
                        providerDTO.displayURL = providerDTO.multiImageUrl;
                        ((DTOView<ContestPageDTO>) convertView).display(getItem(position));
                    }
                }
                ((DTOView<ContestPageDTO>) convertView).display(getItem(position));
            }
            else {

                if(item instanceof ProviderContestPageDTO){
                    ProviderDTO providerDTO = ((ProviderContestPageDTO) item).providerDTO;
                    providerDTO.displayURL = providerDTO.multiImageUrl;
                    ((DTOView<ContestPageDTO>) convertView).display(getItem(position));
                }

            }
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItem(position) instanceof ProviderContestPageDTO;
    }
}
