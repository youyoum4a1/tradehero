package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/7/14 Time: 1:50 PM Copyright (c) TradeHero
 */
public enum TileType
{
    Normal(0, false),
    EarnCredit(R.layout.tile_earn_credit),
    ExtraCash(R.layout.tile_extra_cash),
    ResetPortfolio(R.layout.tile_reset_portfolio),
    Survey(R.layout.tile_survey);

    private final boolean extra;
    private final int layoutResourceId;

    TileType(int layoutResourceId)
    {
        this(layoutResourceId, true);
    }

    TileType(int layoutResourceId, boolean extra)
    {
        this.layoutResourceId = layoutResourceId;
        this.extra = extra;
    }

    public boolean isExtra()
    {
        return extra;
    }

    public int getLayoutResourceId()
    {
        return layoutResourceId;
    }

    static TileType at(int i)
    {
        return TileType.values()[i];
    }
}