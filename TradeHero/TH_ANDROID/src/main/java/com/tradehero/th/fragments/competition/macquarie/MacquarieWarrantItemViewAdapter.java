package com.tradehero.th.fragments.competition.macquarie;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.api.security.compact.WarrantDTOUtil;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.models.security.WarrantDTOUnderlyerTypeComparator;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;

public class MacquarieWarrantItemViewAdapter extends SecurityItemViewAdapter<WarrantDTO>
{
    @Inject SecurityCompactCache securityCompactCache;
    @Inject WarrantDTOUnderlyerTypeComparator warrantDTOComparator;
    @Inject WarrantDTOUtil warrantDTOUtil;
    protected final Filter filterToUse;
    @Inject ListCharSequencePredicateFilter<WarrantDTO> warrantPredicateFilter;

    //<editor-fold desc="Constructors">
    public MacquarieWarrantItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        filterToUse = new SecurityItemFilter(warrantPredicateFilter);
    }
    //</editor-fold>

    @Override public void setItems(final List<WarrantDTO> items)
    {
        if (items == null)
        {
            // Having null is a natural occurence if there is a network failure
            //Timber.e(new NullPointerException("List<WarrantDTO> was null"), "List<WarrantDTO> was null", items);
            super.setItems(items);
        }
        else
        {
            TreeSet<WarrantDTO> ordered = new TreeSet<>(warrantDTOComparator);
            ordered.addAll(items);
            List<WarrantDTO> newItems = new ArrayList<>();
            for (WarrantDTO warrantDTO: ordered)
            {
                newItems.add(warrantDTO);
            }
            super.setItems(newItems);
        }
    }

    @Override public ListCharSequencePredicateFilter<WarrantDTO> getPredicateFilter()
    {
        return warrantPredicateFilter;
    }

    @Override public Filter getFilter()
    {
        return filterToUse;
    }
}