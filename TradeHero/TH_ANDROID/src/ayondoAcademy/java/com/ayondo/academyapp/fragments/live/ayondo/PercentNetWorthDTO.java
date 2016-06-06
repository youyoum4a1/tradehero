package com.ayondo.academyapp.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import java.util.ArrayList;
import java.util.List;

class PercentNetWorthDTO
{
    @NonNull public final PercentNetWorthForInvestmentRange netWorthForInvestmentRange;
    @NonNull public final String text;

    public PercentNetWorthDTO(@NonNull Resources resources, @NonNull PercentNetWorthForInvestmentRange netWorthForInvestmentRange)
    {
        this(netWorthForInvestmentRange, resources.getString(netWorthForInvestmentRange.dropDownText));
    }

    public PercentNetWorthDTO(@NonNull PercentNetWorthForInvestmentRange netWorthForInvestmentRange, @NonNull String text)
    {
        this.netWorthForInvestmentRange = netWorthForInvestmentRange;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<PercentNetWorthDTO> createList(@NonNull Resources resources, @NonNull List<PercentNetWorthForInvestmentRange> netWorthForInvestmentRanges)
    {
        List<PercentNetWorthDTO> created = new ArrayList<>();
        for (PercentNetWorthForInvestmentRange netWorthRange : netWorthForInvestmentRanges)
        {
            created.add(new PercentNetWorthDTO(resources, netWorthRange));
        }
        return created;
    }
}
