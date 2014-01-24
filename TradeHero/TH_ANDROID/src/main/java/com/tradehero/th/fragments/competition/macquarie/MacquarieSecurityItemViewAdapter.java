package com.tradehero.th.fragments.competition.macquarie;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.api.security.WarrantDTOUtil;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.trending.SecurityItemViewAdapter;
import com.tradehero.th.models.security.WarrantDTOUnderlyerTypeComparator;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;

public class MacquarieSecurityItemViewAdapter extends SecurityItemViewAdapter
{
    private final static String TAG = MacquarieSecurityItemViewAdapter.class.getSimpleName();

    @Inject SecurityCompactCache securityCompactCache;
    @Inject WarrantDTOUnderlyerTypeComparator warrantDTOComparator;
    @Inject WarrantDTOUtil warrantDTOUtil;

    //<editor-fold desc="Constructors">
    public MacquarieSecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, SecurityId securityId, final SecurityItemView dtoView)
    {
        // Nothing to do
    }

    @Override public void setItems(List<SecurityId> items)
    {
        ArrayList<SecurityCompactDTO> dtos = securityCompactCache.get(items);
        if (warrantDTOUtil.areAllWarrants(dtos))
        {
            THLog.d(TAG, "Are all warrants");
            TreeSet<WarrantDTO> ordered = new TreeSet<>(warrantDTOComparator);
            ordered.addAll((List<WarrantDTO>) (List<?>) dtos);
            items.clear();
            for (WarrantDTO warrantDTO: ordered)
            {
                items.add(warrantDTO.getSecurityId());
            }
        }
        else
        {
            THLog.d(TAG, "Not all are warrants");
        }
        super.setItems(items);
    }


}