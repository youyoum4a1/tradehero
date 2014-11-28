package com.tradehero.chinabuild.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created by huhaiping on 14-9-10.
 */
public class UGCFromDTO
{
    public String name;
    public String description;
    public int durationDays;
    public int[] exchangeIds;

    public UGCFromDTO(@NotNull String name, @NotNull String description, @NotNull int durationDays, @NotNull int[] exchangeIds)
    {
        this.name = name;
        this.description = description;
        this.durationDays = durationDays;
        this.exchangeIds = exchangeIds;
    }
}
