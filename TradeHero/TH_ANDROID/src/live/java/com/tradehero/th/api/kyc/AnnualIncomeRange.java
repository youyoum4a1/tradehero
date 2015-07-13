package com.tradehero.th.api.kyc;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AnnualIncomeRange
{
    LESS15KUSD(R.string.annual_income_less_than_15_k_usd, 1),
    FROM15KUSDTO40KUSD(R.string.annual_income_from_15_k_to_40_k_usd, 2),
    FROM40KUSDTO70KUSD(R.string.annual_income_from_40_k_to_70_k_usd, 3),
    FROM70KUSDTO100KUSD(R.string.annual_income_from_70_k_to_100_k_usd, 4),
    MORETHAN100KUSD(R.string.annual_income_more_than_100_k_usd, 5),;

    public static final Map<Integer, AnnualIncomeRange> filedAnnualIncomeRanges;

    @StringRes public final int dropDownText;
    private final int fromServer;

    AnnualIncomeRange(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AnnualIncomeRange getAnnualIncomeRange(int fromServer)
    {
        AnnualIncomeRange candidate = filedAnnualIncomeRanges.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any AnnualIncomeRange");
        }
        return candidate;
    }

    static
    {
        Map<Integer, AnnualIncomeRange> map = new HashMap<>();
        for (AnnualIncomeRange candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedAnnualIncomeRanges = Collections.unmodifiableMap(map);
    }

    @NonNull public static List<String> createTexts(@NonNull Resources resources, @NonNull Collection<AnnualIncomeRange> incomeRanges)
    {
        List<String> created = new ArrayList<>();
        for (AnnualIncomeRange incomeRange : incomeRanges)
        {
            created.add(resources.getString(incomeRange.dropDownText));
        }
        return created;
    }
}
