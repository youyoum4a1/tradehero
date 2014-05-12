package com.tradehero.th.models.chart;

public class ChartSize
{
    public int width;
    public int height;

    //<editor-fold desc="Constructors">
    public ChartSize(int width, int height)
    {
        super();
        this.width = width;
        this.height = height;
    }

    public ChartSize(ChartSize other)
    {
        super();
        this.width = other.width;
        this.height = other.height;
    }

    public ChartSize(int[] wAndH)
    {
        super();
        this.width = wAndH[0];
        this.height = wAndH[1];
    }
    //</editor-fold>

    public int[] getSizeArray()
    {
        return new int[]{this.width, this.height};
    }

    @Override public int hashCode()
    {
        return Integer.valueOf(width).hashCode() ^ Integer.valueOf(height).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof ChartSize) && equals((ChartSize) other);
    }

    public boolean equals(ChartSize other)
    {
        return other != null && width == other.width && height == other.height;
    }

    @Override protected Object clone()
    {
        return new ChartSize(this);
    }
}
