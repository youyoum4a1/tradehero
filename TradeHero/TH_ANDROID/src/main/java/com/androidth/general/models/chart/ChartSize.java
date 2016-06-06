package com.androidth.general.models.chart;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChartSize
{
    public final int width;
    public final int height;

    //<editor-fold desc="Constructors">
    public ChartSize(int width, int height)
    {
        super();
        this.width = width;
        this.height = height;
    }

    public ChartSize(@NonNull ChartSize other)
    {
        super();
        this.width = other.width;
        this.height = other.height;
    }

    public ChartSize(@NonNull int[] wAndH)
    {
        super();
        this.width = wAndH[0];
        this.height = wAndH[1];
    }
    //</editor-fold>

    @NonNull public int[] getSizeArray()
    {
        return new int[]{this.width, this.height};
    }

    @Override public int hashCode()
    {
        return Integer.valueOf(width).hashCode() ^ Integer.valueOf(height).hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return (other instanceof ChartSize) && equalFields((ChartSize) other);
    }

    protected boolean equalFields(@NonNull ChartSize other)
    {
        return width == other.width && height == other.height;
    }

    @Override protected Object clone()
    {
        return new ChartSize(this);
    }
}
