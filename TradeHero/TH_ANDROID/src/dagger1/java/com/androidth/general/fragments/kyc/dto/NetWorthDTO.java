package com.androidth.general.fragments.kyc.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.androidth.general.api.kyc.NetWorthRange;

import java.util.ArrayList;
import java.util.List;

public class NetWorthDTO
{
    @NonNull public final NetWorthRange netWorthRange;
    @NonNull public final String text;

    public NetWorthDTO(@NonNull Resources resources, @NonNull NetWorthRange netWorthRange)
    {
        this(netWorthRange, resources.getString(netWorthRange.dropDownText));
    }

    public NetWorthDTO(@NonNull NetWorthRange netWorthRange, @NonNull String text)
    {
        this.netWorthRange = netWorthRange;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull public static List<NetWorthDTO> createList(@NonNull Resources resources, @NonNull List<NetWorthRange> netWorthRanges)
    {
        List<NetWorthDTO> created = new ArrayList<>();
        for (NetWorthRange netWorthRange : netWorthRanges)
        {
            created.add(new NetWorthDTO(resources, netWorthRange));
        }
        return created;
    }
}
