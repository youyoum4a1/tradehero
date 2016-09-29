package com.androidth.general.fragments.kyc.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.androidth.general.api.kyc.AnnualIncomeRange;

import java.util.ArrayList;
import java.util.List;

public class AnnualIncomeDTO
{
    @NonNull public final AnnualIncomeRange annualIncomeRange;
    @NonNull public final String text;

    public AnnualIncomeDTO(@NonNull Resources resources, @NonNull AnnualIncomeRange annualIncomeRange)
    {
        this(annualIncomeRange, resources.getString(annualIncomeRange.dropDownText));
    }

    public AnnualIncomeDTO(@NonNull AnnualIncomeRange annualIncomeRange, @NonNull String text)
    {
        this.annualIncomeRange = annualIncomeRange;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull public static List<AnnualIncomeDTO> createList(@NonNull Resources resources, @NonNull List<AnnualIncomeRange> annualIncomeRanges)
    {
        List<AnnualIncomeDTO> created = new ArrayList<>();
        for (AnnualIncomeRange annualIncomeRange : annualIncomeRanges)
        {
            created.add(new AnnualIncomeDTO(resources, annualIncomeRange));
        }
        return created;
    }
}
