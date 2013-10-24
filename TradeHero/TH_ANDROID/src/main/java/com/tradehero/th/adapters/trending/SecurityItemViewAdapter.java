package com.tradehero.th.adapters.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.widget.trending.TrendingSecurityView;

public class SecurityItemViewAdapter extends DTOAdapter<SecurityCompactDTO, TrendingSecurityView>
{
    private final static String TAG = SecurityItemViewAdapter.class.getSimpleName();

    public SecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int i)
    {
        long itemId = ((SecurityCompactDTO) getItem(i)).getSecurityId().hashCode();
        //THLog.d(TAG, "getItemId " + i + " - " + itemId);
        return itemId;
    }

    @Override protected void fineTune(int position, SecurityCompactDTO securityCompactDTO, final TrendingSecurityView dtoView)
    {
        //THLog.d(TAG, "fineTune position:" + position);
        dtoView.post(new Runnable()
        {
            @Override public void run()
            {
                dtoView.loadImages();
            }
        });
    }
}